package com.franc.standard.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class AccountGetInfoDTO {
    @Getter
    @Setter
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class Response extends BaseResponse {

        private Long memberNo;
        private String accountNo;
        private String bankCd;
        private String bankName;
        private Integer deposit;
        private Character status;
        private String statusName;

        private String createDateStr;

    }

}
