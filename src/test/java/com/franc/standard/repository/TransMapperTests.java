package com.franc.standard.repository;

import com.franc.standard.code.TransFg;
import com.franc.standard.vo.TransVO;
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
public class TransMapperTests {

    @Autowired
    private TransMapper transMapper;



    private static final Character TRANS_FG = TransFg.TRANSFER.code();
    private static final int TRANS_AMT = 10000;
    private static final long MEMBER_NO = 1L;
    private static final String BANK_CD = "101";
    private static final String ACCOUNT_NO = "1010000022";
    private static final long TO_MEMBER_NO = 2L;
    private static final String TO_BANK_CD = "103";
    private static final String TO_ACCOUNT_NO = "1035934839402";




    @Test
    @DisplayName("거래일련번호 생성")
    public void test_issueTransSeq() throws Exception {
        // #1. Given

        // #2. When
        String transId = issueTransId();

        // #3. Then
        assertThat(transId).isNotNull();
        assertThat(transId.length()).isEqualTo(20);

    }

    @Test
    @Transactional
    @DisplayName("저장 후 ID 조회해서 대조")
    public void save() throws Exception {
        // #1. Given
        String transId = issueTransId();

        // #2. When
        transMapper.save(getDefaultVO(transId));
        TransVO transVO = transMapper.findById(transId);

        // #3. Then
        assertThat(transVO).isNotNull();
        assertThat(transVO.getTransId()).isEqualTo(transId);
        assertThat(transVO.getTransFg()).isEqualTo(TRANS_FG);
        assertThat(transVO.getTransAmt()).isEqualTo(TRANS_AMT);
        assertThat(transVO.getTransDate()).isNotNull();
        assertThat(transVO.getMemberNo()).isEqualTo(MEMBER_NO);
        assertThat(transVO.getAccountNo()).isEqualTo(ACCOUNT_NO);
        assertThat(transVO.getBankCd()).isEqualTo(BANK_CD);
        assertThat(transVO.getTransText()).isNotNull();
        assertThat(transVO.getToMemberNo()).isEqualTo(TO_MEMBER_NO);
        assertThat(transVO.getToAccountNo()).isEqualTo(TO_ACCOUNT_NO);
        assertThat(transVO.getToBankCd()).isEqualTo(TO_BANK_CD);
        assertThat(transVO.getMemo()).isNotNull();
    }



    @Test
    @Transactional
    @DisplayName("다건저장 후 여러조건으로 목록조회 후 대조")
    public void findAll() throws Exception {
        String transId = "1";
        String transId2 = "2";
        String transId3 = "3";

        transMapper.save(getDefaultVO(transId));
        transMapper.save(getDefaultVO(transId2));
        transMapper.save(getDefaultVO(transId3));

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("memberNo", MEMBER_NO);
        paramMap1.put("transFg", TRANS_FG);

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("memberNo", MEMBER_NO);
        paramMap2.put("offset", 1);
        paramMap2.put("limit", 1);

        Map<String, Object> paramMap3 = new HashMap<>();
        paramMap3.put("memberNo", MEMBER_NO);
        paramMap3.put("accountNo", ACCOUNT_NO);

        // #2. When
        List<TransVO> transVOs1 = transMapper.findAll(paramMap1);
        List<TransVO> transVOs2 = transMapper.findAll(paramMap2);
        List<TransVO> transVOs3 = transMapper.findAll(paramMap3);

        // #3. Then
        assertThat(transVOs1.size()).isEqualTo(3);
        assertThat(transVOs2.size()).isEqualTo(1);
        assertThat(transVOs3.size()).isEqualTo(3);

    }



    public String issueTransId() throws Exception {
        return transMapper.issueTransId();
    }

    public TransVO getDefaultVO(String transId) {
        return TransVO.builder()
                .transId(transId)
                .transFg(TRANS_FG)
                .transAmt(TRANS_AMT)
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .transText("테스트")
                .toMemberNo(TO_MEMBER_NO)
                .toAccountNo(TO_ACCOUNT_NO)
                .toBankCd(TO_BANK_CD)
                .memo("메모123")
                .build();
    }
}
