package com.franc.standard.vo;

import com.franc.standard.code.AccountStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@EqualsAndHashCode(of = {"accountNo"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountVO {

    private Long memberNo;
    private String accountNo;
    private String bankCd;

    @Builder.Default
    private Character status = AccountStatus.USE.code();
    private String pin;

    @Builder.Default
    private Integer deposit = 0;
    private LocalDateTime createDate;



    /* 계좌 해지 */
    public void withdrawal() {
        status = AccountStatus.WITHDRAWAL.code();
    }

    /* 계좌 재활성 */
    public void reUse() {
        status = AccountStatus.USE.code();
    }

}
