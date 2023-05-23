package com.franc.standard.vo;

import com.franc.standard.code.AccountStatus;
import com.franc.standard.code.TransFg;
import com.franc.standard.util.DateUtil;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString
@EqualsAndHashCode(of = {"transId"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransVO {

    @Setter
    private String transId;
    private Character transFg;
    private Integer transAmt;
    private LocalDateTime transDate;
    @Setter
    private Long memberNo;
    private String accountNo;
    @Setter
    private String bankCd;
    private String transText;
    @Setter
    private Long toMemberNo;
    private String toAccountNo;
    @Setter
    private String toBankCd;
    private String memo;



    // 기타 필드
    private String memberName;
    private String bankName;
    private String toMemberName;
    private String toBankName;
    private String transFgName;
    private String transDateStr;


    public void setTransFg(Character transFg) {
        if(transFg != null) {
            this.transFg = transFg;
            this.transFgName = TransFg.of(transFg).getName();
        }
    }

    public void setTransDate(LocalDateTime transDate) {
        this.transDate = transDate;
        this.transDateStr = DateUtil.localDateTimeToString(transDate);
    }

}
