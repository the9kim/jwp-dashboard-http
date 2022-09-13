package nextstep.jwp.exception;

import org.apache.coyote.exception.InternalServerException;

public class DuplicateAccountRegisterException extends InternalServerException {

    public DuplicateAccountRegisterException() {
        super("이미 존재하는 사용자입니다.");
    }
}