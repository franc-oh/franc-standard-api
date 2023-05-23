package com.franc.standard.repository;


import com.franc.standard.vo.MemberVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MemberMapperTests {

    @Autowired
    private MemberMapper memberMapper;


    @Test
    public void findById() throws Exception {
        // #1. Given
        Long memberNo = 2L;
        String phone = "01049283019";

        // #2. When
        MemberVO memberVO = memberMapper.findById(memberNo);

        // #3. Then
        assertThat(memberVO).isNotNull();
        assertThat(memberVO.getMemberNo()).isEqualTo(memberNo);
        assertThat(memberVO.getPhone()).isEqualTo(phone);
    }
}
