package com.franc.standard.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    USE('1', "사용"),
    STOP('9', "정지"),
    WITHDRAWAL('0', "해지");


    private final Character code;
    private final String name;


    public Character code() {
        return code;
    }

    public static final Map<Character, AccountStatus> CACHED_STATUS =
            Stream.of(values()).collect(Collectors.toMap(AccountStatus::code, e -> e));

    public static AccountStatus of(final Character code) {
        return CACHED_STATUS.get(code);
    }

}
