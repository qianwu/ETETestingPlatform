package org.charlotte.e2ecore.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@Builder
public class ErrorResponse {
    private int code;
    private String message;

    public static ErrorResponse error(Integer code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ErrorResponse error(IatmException iatmException) {
        return ErrorResponse.builder()
                .code(iatmException.getCode())
                .message(iatmException.getMessage())
                .build();
    }

    public static ErrorResponse error(Exception ex) {
        return ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
    }
}