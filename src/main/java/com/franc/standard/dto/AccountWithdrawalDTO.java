package com.franc.standard.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AccountWithdrawalDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Request {

        @NotNull
        @Min(1)
        private Long memberNo;

        @NotNull
        private String accountNo;

        @NotNull
        private String bankCd;

        @NotNull
        private String pin;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @NoArgsConstructor
    @SuperBuilder
    public static class Response extends BaseResponse{

    }
}
