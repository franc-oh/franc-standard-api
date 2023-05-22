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
import java.util.List;
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
        AccountVO accountVO = accountMapper.findById(ACCOUNT_NO);

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
        AccountVO accountVO = accountMapper.findById(ACCOUNT_NO);

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
        AccountVO resultVO = accountMapper.findById(ACCOUNT_NO);

        // #3. Then
        assertThat(resultVO).isNotNull();
        assertThat(resultVO.getMemberNo()).isEqualTo(MEMBER_NO);
        assertThat(resultVO.getAccountNo()).isEqualTo(ACCOUNT_NO);
        assertThat(resultVO.getPin()).isEqualTo(PIN);
        assertThat(resultVO.getStatus()).isEqualTo(AccountStatus.USE.code());
        assertThat(resultVO.getCreateDate()).isNotNull();
    }

    @Test
    @DisplayName("목록조회 - 다건등록 후 필터링_옵션값 테스트")
    @Transactional
    public void findAll() throws Exception {
        // #1. Given
        String bankCd1 = "103";
        String bankCd2 = "101";

        accountMapper.save(AccountVO.builder()
                .memberNo(MEMBER_NO)
                .accountNo("123441414")
                .bankCd(bankCd1)
                .pin("123456")
                .build());
        accountMapper.save(AccountVO.builder()
                .memberNo(MEMBER_NO)
                .accountNo("13455646")
                .bankCd(bankCd1)
                .pin("333333")
                .build());
        accountMapper.save(AccountVO.builder()
                .memberNo(MEMBER_NO)
                .accountNo("444555353")
                .bankCd(bankCd2)
                .pin("556677")
                .build());

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("memberNo", MEMBER_NO);

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("memberNo", MEMBER_NO);
        paramMap2.put("offset", 1);
        paramMap2.put("limit", 1);

        Map<String, Object> paramMap3 = new HashMap<>();
        paramMap3.put("memberNo", MEMBER_NO);
        paramMap3.put("bankCd", bankCd1);

        // #2. When
        List<AccountVO> myAccountsAll = accountMapper.findAll(paramMap1);
        List<AccountVO> myAccountsRow1 = accountMapper.findAll(paramMap2);
        List<AccountVO> myAccountsBankCd1 = accountMapper.findAll(paramMap3);

        accountMapper.save(AccountVO.builder()
                .memberNo(MEMBER_NO)
                .accountNo("13455646")
                .bankCd(bankCd1)
                .status(AccountStatus.STOP.code())
                .pin("333333")
                .build());

        paramMap1.put("status", AccountStatus.USE.code());
        List<AccountVO> myAccountsAllStatus1 = accountMapper.findAll(paramMap1);

        // #3. Then
        assertThat(myAccountsAll.size()).isEqualTo(3);
        assertThat(myAccountsRow1.size()).isEqualTo(1);
        assertThat(myAccountsBankCd1.size()).isEqualTo(2);
        assertThat(myAccountsAllStatus1.size()).isEqualTo(2);
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
