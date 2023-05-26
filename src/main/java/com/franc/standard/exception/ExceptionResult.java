package com.franc.standard.exception;

import com.franc.standard.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionResult {

    ALEADY_SAVE_ACCOUNT(HttpStatus.BAD_REQUEST, "이미 등록된 계좌입니다."),
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원이 아닙니다."),
    NOT_FOUND_BANK(HttpStatus.BAD_REQUEST, "해당 코드에 해당하는 은행이 존재하지 않습니다."),
    NOT_FOUND_ACCOUNT(HttpStatus.BAD_REQUEST, "해당 계좌는 존재하지 않는 계좌입니다."),
    NOT_ACTIVE_ACCOUNT(HttpStatus.BAD_REQUEST, "해당 계좌는 정지(또는 해지)된 계좌입니다."),
    WRONG_PIN_NUMBER(HttpStatus.BAD_REQUEST, "PIN번호가 일치하지 않습니다."),
    WRONG_ACCOUNT_NO(HttpStatus.BAD_REQUEST, "잘못된 계좌번호입니다."),
    PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 요청 데이터입니다."),
    INSUFFICIENT_CACH(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    NOT_FOUND_TRANS(HttpStatus.BAD_REQUEST, "거래정보가 없습니다."),
    TRANSFER_NOT_ACCOUNT(HttpStatus.BAD_REQUEST, "이체계좌 정보가 없습니다."),
    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, BaseCode.RESPONSE_MESSAGE_500);

    private final HttpStatus code;
    private final String message;


}
