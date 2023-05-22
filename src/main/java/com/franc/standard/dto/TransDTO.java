package com.franc.standard.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TransDTO {

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
        @Min(1)
        private Integer transAmt;

        @NotNull
        private Character transFg;

        @Size(min = 3, max = 30)
        private String toAccountNo;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class Response extends BaseResponse{

    }
}
