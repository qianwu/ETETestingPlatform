package org.charlotte.e2ecore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author ：charlotte
 * @date ：Created in 16/12/21 2:44 PM
 * @description ：customized Exception
 */
@Getter
public class IatmException extends Exception {
    private HttpStatus httpStatus;
    private int code;
    private String message;

    public IatmException(BizError bizError) {
        this.code = bizError.getCode();
        this.message = bizError.getMessage();
        this.httpStatus = bizError.getHttpStatus();
    }

    public IatmException(String message) {
        this.code = 500;
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
