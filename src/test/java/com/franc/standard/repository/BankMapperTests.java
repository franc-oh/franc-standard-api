package com.franc.standard.repository;


import com.franc.standard.vo.BankVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@MybatisTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class BankMapperTests {

    @Autowired
    private BankMapper bankMapper;


    @Test
    public void findAll() throws Exception {
        // #1. Given

        // #2. When
        List<BankVO> banks = bankMapper.findAll();

        // #3. Then
        assertThat(banks).isNotNull();
        assertThat(banks.size()).isGreaterThan(0);
        assertThat(banks.get(0).getBankCd()).isEqualTo("101");
    }

    @Test
    public void findById() throws Exception {
        // #1. Given
        String bankCd = "103";
        String bankName = "기업은행";

        // #2. When
        BankVO bankVO = bankMapper.findById(bankCd);

        // #3. Then
        assertThat(bankVO).isNotNull();
        assertThat(bankVO.getBankCd()).isEqualTo(bankCd);
        assertThat(bankVO.getBankName()).isEqualTo(bankName);
    }

}

