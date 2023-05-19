package com.franc.standard.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
        @Size(min = 3, max = 30)
        private String accountNo;

        @NotNull
        private String pin;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class Response extends BaseResponse{

    }
}
