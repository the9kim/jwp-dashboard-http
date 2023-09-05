package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String REGISTER_PAGE = "/register.html";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));


            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            org.apache.coyote.http11.response.HttpResponse httpResponse = handleRequest(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public org.apache.coyote.http11.response.HttpResponse handleRequest(final HttpRequest request) throws URISyntaxException {

        final String uriPath = request.getPath();

        if (uriPath.equals("/login")) {
            final String path = LOGIN_PAGE;
            if (request.getMethod().equals("GET")) {
                return org.apache.coyote.http11.response.HttpResponse.of(HttpStatus.OK, path);
            }
            if (request.getMethod().equals("POST")) {
                final boolean isAuthenticated = processLogin(request);
                final String redirectUrlPath = isAuthenticated ? INDEX_PAGE : UNAUTHORIZED_PAGE;
                return org.apache.coyote.http11.response.HttpResponse.of(HttpStatus.FOUND, redirectUrlPath);
            }
        }

        if (uriPath.equals("/register")) {
            final String path = REGISTER_PAGE;
            if (request.getMethod().equals("GET")) {
                return org.apache.coyote.http11.response.HttpResponse.of(HttpStatus.OK, path);
            }
            if (request.getMethod().equals("POST")) {
                processRegister(request);
                return org.apache.coyote.http11.response.HttpResponse.of(HttpStatus.FOUND, INDEX_PAGE);
            }
        }

        return org.apache.coyote.http11.response.HttpResponse.of(HttpStatus.OK, uriPath);
    }

    public boolean processLogin(HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY)) {
            return false;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.checkPassword(password)) {
                log.info(user.toString());
                return true;
            }
        }
        return false;
    }

    public void processRegister(final HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY) || !request.containsBody(EMAIL_KEY)) {
            return;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);
        final String email = request.getBody(EMAIL_KEY);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return;
        }
        final User registeredUser = new User(account, password, email);
        InMemoryUserRepository.save(registeredUser);
    }
}
