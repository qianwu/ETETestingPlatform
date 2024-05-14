package org.charlotte.e2ecore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BizError {

    BAD_REQUEST(400, "Bad request", HttpStatus.BAD_REQUEST),
    SERVER_ERROR(500, "Server error", HttpStatus.INTERNAL_SERVER_ERROR),

    NO_SUCH_DATA(1001, "No such data found", HttpStatus.NOT_FOUND),
    MORE_THAN_ONE_RECORD_FOUND(1002, "More than one record found, please check your input", HttpStatus.BAD_REQUEST);

    private Integer code;
    private String message;
    private HttpStatus httpStatus;

    BizError(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    BizError(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
