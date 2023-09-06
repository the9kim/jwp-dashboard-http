package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    IMAGE_X_ICON("ico", "image/x-icon");

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType findContentType(String extension) {
        return Arrays.stream(values())
                .filter(value -> value.extension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("적절한 확장자가 아닙니다."));
    }

    public String getContentType() {
        return contentType;
    }
}
