package com.franc.standard.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum TransFg {
    DEPOSIT('1', "입금"),
    WITHDRAW('2', "출금"),
    TRANSFER('3', "이체");


    private final Character code;
    private final String name;


    public Character code() {
        return code;
    }

    public static final Map<Character, TransFg> CACHED_STATUS =
            Stream.of(values()).collect(Collectors.toMap(TransFg::code, e -> e));

    public static TransFg of(final Character code) {
        return CACHED_STATUS.get(code);
    }

}
