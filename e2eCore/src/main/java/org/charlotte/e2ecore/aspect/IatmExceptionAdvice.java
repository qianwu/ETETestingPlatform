//package org.charlotte.e2ecore.aspect;
//

//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//@Slf4j
//@ControllerAdvice
//public class IatmExceptionAdvice {
//
//    @ExceptionHandler(value = {Exception.class})
//    public ResponseEntity<ErrorResponse> handleCommonException(Exception e) {
//        log.error("handleCommonException! ", e);
//        return new ResponseEntity<>(ErrorResponse.error(e), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//
//    @ExceptionHandler(value = {IllegalArgumentException.class})
//    public ResponseEntity<ErrorResponse> handleInputException(Exception e) {
//        log.error("handleInputException! ", e);
//        return new ResponseEntity<>(ErrorResponse.error(HttpStatus.BAD_REQUEST.value(), "please check your argument(s)")
//                , HttpStatus.BAD_REQUEST);
//    }
//
//
//    @ExceptionHandler(value = {IatmException.class})
//    public ResponseEntity<ErrorResponse> handleIatmException(IatmException e) {
//        log.error("handleIatmException! ", e);
//        return new ResponseEntity<>(ErrorResponse.error(e), e.getHttpStatus());
//    }
//
//
//}
