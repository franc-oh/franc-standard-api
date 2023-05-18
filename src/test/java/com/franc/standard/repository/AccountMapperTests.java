package com.franc.standard.repository;

import com.franc.standard.code.AccountStatus;
import com.franc.standard.vo.AccountVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@MybatisTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountMapperTests {

    @Autowired
    private AccountMapper accountMapper;



    private static final Long MEMBER_NO = 1L;
    private static final String ACCOUNT_NO = "1234111122290";
    private static final String BANK_CD = "102";
    private static final String PIN = "123456";


    @Test
    @DisplayName("신규저장 & findById와 대조")
    @Transactional
    public void save_new() throws Exception {
        // #1. Given

        // #2. When
        accountMapper.save(getAccountVO());
        AccountVO accountVO = accountMapper.findById(getParamMap());

        // #3. Then
        assertThat(accountVO).isNotNull();
        assertThat(accountVO.getMemberNo()).isEqualTo(MEMBER_NO);
        assertThat(accountVO.getAccountNo()).isEqualTo(ACCOUNT_NO);
        assertThat(accountVO.getPin()).isEqualTo(PIN);
        assertThat(accountVO.getCreateDate()).isNotNull();
    }


    @Test
    @DisplayName("계좌해지 & 활성화")
    @Transactional
    public void save_changeStatus() throws Exception {
        // #1. Given
        accountMapper.save(getAccountVO());
        AccountVO accountVO = accountMapper.findById(getParamMap());

        // #2. When - 해지
        accountVO.withdrawal();
        accountMapper.save(accountVO);

        // #3. Then
        assertThat(accountVO).isNotNull();
        assertThat(accountVO.getMemberNo()).isEqualTo(MEMBER_NO);
        assertThat(accountVO.getAccountNo()).isEqualTo(ACCOUNT_NO);
        assertThat(accountVO.getPin()).isEqualTo(PIN);
        assertThat(accountVO.getStatus()).isEqualTo(AccountStatus.WITHDRAWAL.code());
        assertThat(accountVO.getCreateDate()).isNotNull();


        // #2. When - 재활성
        accountVO.reUse();
        accountMapper.save(accountVO);
        AccountVO resultVO = accountMapper.findById(getParamMap());

        // #3. Then
        assertThat(resultVO).isNotNull();
        assertThat(resultVO.getMemberNo()).isEqualTo(MEMBER_NO);
        assertThat(resultVO.getAccountNo()).isEqualTo(ACCOUNT_NO);
        assertThat(resultVO.getPin()).isEqualTo(PIN);
        assertThat(resultVO.getStatus()).isEqualTo(AccountStatus.USE.code());
        assertThat(resultVO.getCreateDate()).isNotNull();
    }




    public Map<String, Object> getParamMap() throws Exception {
        Map<String, Object> accountParamMap = new HashMap<>();
        accountParamMap.put("memberNo", MEMBER_NO);
        accountParamMap.put("accountNo", ACCOUNT_NO);
        accountParamMap.put("bankCd", BANK_CD);

        return accountParamMap;
    }

    public AccountVO getAccountVO() throws Exception {
        return AccountVO.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .pin(PIN)
                .build();
    }
}
