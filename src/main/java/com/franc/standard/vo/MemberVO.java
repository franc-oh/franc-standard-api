package com.franc.standard.vo;

import lombok.*;

@Getter @ToString
@EqualsAndHashCode(of = {"memberNo"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberVO {

    private Long memberNo;
    private String memberName;
    private String phone;
    private String email;

}
