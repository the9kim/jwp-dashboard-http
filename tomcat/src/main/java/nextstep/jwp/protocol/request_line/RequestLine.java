package nextstep.jwp.protocol.request_line;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private Path path;
    private Protocol protocol;

}