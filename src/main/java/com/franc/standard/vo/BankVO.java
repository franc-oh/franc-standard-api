package com.franc.standard.vo;

import lombok.*;

@Getter @ToString
@EqualsAndHashCode(of = {"bankCd"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankVO {

    private String bankCd;
    private String bankName;

}
