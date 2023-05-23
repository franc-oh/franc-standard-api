package com.franc.standard.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TransGetInfoDTO {

    @Getter
    @Setter
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class Response extends BaseResponse {
        private String transId;
        private Character transFg;
        private String transFgName;
        private Long memberNo;
        private String memberName;
        private String bankCd;
        private String bankName;
        private String accountNo;
        private String toAccountNo;
        private String toMemberNo;
        private String toMemberName;
        private String toBankCd;
        private String toBankName;
        private Integer transAmt;
        private String transText;
        private String transDateStr;
        private String memo;

    }

}
