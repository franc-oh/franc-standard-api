package com.franc.standard.vo;

import com.franc.standard.code.AccountStatus;
import com.franc.standard.util.DateUtil;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@EqualsAndHashCode(of = {"accountNo"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountVO {
    private String accountNo;
    private Long memberNo;

    @Setter
    private String bankCd;

    @Builder.Default
    private Character status = AccountStatus.USE.code();
    private String pin;

    @Builder.Default
    private Integer deposit = 0;
    private LocalDateTime createDate;



    // 기타 필드
    private String bankName;
    private String statusName;
    private String createDateStr;


    public void setStatus(Character status) {
        if(status != null) {
            this.status = status;
            this.statusName = AccountStatus.of(status).getName();
        }

    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
        this.createDateStr = DateUtil.localDateTimeToString(createDate);
    }

    /* 계좌 해지 */
    public void withdrawal() {
        status = AccountStatus.WITHDRAWAL.code();
    }

    /* 계좌 재활성 */
    public void reUse() {
        status = AccountStatus.USE.code();
    }

}
