package com.franc.standard.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * '파라미터 유효성 검증 실패' 처리
     * @param e
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> methodArgmentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        e.printStackTrace();

        String errorCode = String.valueOf(ExceptionResult.PARAMETER_NOT_VALID.getCode().value());
        String errorMessage = ExceptionResult.PARAMETER_NOT_VALID.getMessage();

        return ResponseEntity.status(ExceptionResult.PARAMETER_NOT_VALID.getCode())
                .body(buildExceptionResponse(errorCode, errorMessage));
    }

    /**
     * '비즈니스 예외' 처리
     * @param e
     * @return
     */
    @ExceptionHandler({BizException.class})
    public ResponseEntity<ExceptionResponse> bizExceptionHandler(BizException e) {
        e.printStackTrace();

        String errorCode = String.valueOf(e.getResult().getCode().value());
        String errorMessage = e.getResult().getMessage();

        return ResponseEntity.status(e.getResult().getCode())
                .body(buildExceptionResponse(errorCode, errorMessage));
    }

    /**
     * '기타오류' 처리
     * @param e
     * @return
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> exceptionHandler(Exception e) {
        e.printStackTrace();

        String errorCode = String.valueOf(ExceptionResult.UNKNOWN_EXCEPTION.getCode().value());
        String errorMessage = ExceptionResult.UNKNOWN_EXCEPTION.getMessage();

        return ResponseEntity.status(ExceptionResult.UNKNOWN_EXCEPTION.getCode())
                .body(buildExceptionResponse(errorCode, errorMessage));
    }

    // 예외핸들러의 body를 빌드
    public ExceptionResponse buildExceptionResponse(String errorCode, String errorMessage) {
        return new ExceptionResponse().builder()
                .resultCode(errorCode)
                .resultMessage(errorMessage)
                .build();
    }
}
