package com.franc.standard.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TransGetListDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Request {

        @NotNull
        @Min(1)
        private Long memberNo;
        private Character transFg;
        private String bankCd;
        private Integer offset;
        private Integer limit;

    }

    @Getter
    @Setter
    @ToString(callSuper = true, exclude = "dataList")
    @NoArgsConstructor
    public static class Response extends BaseResponse {

        private Integer dataCnt = 0;
        private List<TransInfo> dataList;


        public void setDataList(List<TransInfo> dataList) {
            if(!dataList.isEmpty()) {
                this.dataList = dataList;
                this.dataCnt = dataList.size();
            }
        }
    }

    @Getter
    public static class TransInfo {
        private String transId;
        private Character transFg;
        private String transFgName;
        private String memberName;
        private String bankCd;
        private String bankName;
        private String accountNo;
        private String toAccountNo;
        private String toMemberName;
        private String toBankCd;
        private String toBankName;
        private Integer transAmt;
        private String transText;
        private String transDateStr;
    }
}
