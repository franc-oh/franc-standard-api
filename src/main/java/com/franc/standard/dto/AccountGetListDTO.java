package com.franc.standard.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AccountGetListDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Request {

        @NotNull
        @Min(1)
        private Long memberNo;

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
        private List<AccountInfo> dataList;


        public void setDataList(List<AccountInfo> dataList) {
            if(!dataList.isEmpty()) {
                this.dataList = dataList;
                this.dataCnt = dataList.size();
            }
        }
    }

    @Getter
    public static class AccountInfo {
        private Long memberNo;
        private String accountNo;
        private String bankCd;

        private String bankName;
        private Integer deposit;
    }
}
