package nextstep.jwp.exception;

import org.apache.coyote.exception.NotFoundException;

public class ControllerNotFoundException extends NotFoundException {

    public ControllerNotFoundException() {
        super("요청에 맞는 컨트롤러를 찾지 못했습니다.");
    }
}