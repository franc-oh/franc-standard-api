package com.franc.standard;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.BaseCode;
import com.franc.standard.code.TransFg;
import com.franc.standard.controller.TransController;
import com.franc.standard.dto.TransDTO;
import com.franc.standard.dto.TransGetListDTO;
import com.franc.standard.exception.ControllerExceptionHandler;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.repository.TransMapper;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.TransVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TransApiTests {

    @Autowired
    private TransController transController;

    @Autowired
    private TransMapper transMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(transController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    private static final String URL = "/api/standard/trans";
    private static final Long MEMBER_NO = 1L;
    private static final Long TO_MEMBER_NO = 3L;
    private static final String ACCOUNT_NO = "1014111122290";
    private static final String TO_ACCOUNT_NO = "10200000000";




    @Test
    @DisplayName("거래실패 - 유효성")
    @Transactional
    public void trans_exception_valid() throws Exception {
        // #1. Given
        TransDTO.Request request = TransDTO.Request.builder().build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.PARAMETER_NOT_VALID.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.PARAMETER_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("거래실패 - 회원X")
    @Transactional
    public void trans_exception_NoMember() throws Exception {
        // #1. Given
        TransDTO.Request request = TransDTO.Request.builder()
                .memberNo(99999999999999L)
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.DEPOSIT.code())
                .transAmt(1000)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_MEMBER.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_MEMBER.getMessage()));
    }

    @Test
    @DisplayName("거래성공_입금")
    @Transactional
    public void trans_deposit() throws Exception {
        // #1. Given
        saveAccount(ACCOUNT_NO, MEMBER_NO);

        TransDTO.Request request = TransDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.DEPOSIT.code())
                .transAmt(1000)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isAccepted())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

    }

    @Test
    @DisplayName("거래성공_출금")
    @Transactional
    public void trans_withdraw() throws Exception {
        // #1. Given
        trans_deposit();

        TransDTO.Request request = TransDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.WITHDRAW.code())
                .transAmt(100)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isAccepted())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

    }

    @Test
    @DisplayName("거래성공_이체")
    @Transactional
    public void trans_transfer() throws Exception {
        // #1. Given
        trans_deposit();

        accountMapper.save(AccountVO.builder()
                .accountNo(TO_ACCOUNT_NO)
                .bankCd(TO_ACCOUNT_NO.substring(0, BaseCode.BANK_CD_LENGTH))
                .memberNo(2L)
                .pin("123456")
                .build());

        TransDTO.Request request = TransDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .toAccountNo(TO_ACCOUNT_NO)
                .transFg(TransFg.TRANSFER.code())
                .transAmt(100)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isAccepted())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

    }

    public RequestBuilder transRequestBuilder(String content) {
        return MockMvcRequestBuilders.post(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }


    @Test
    @DisplayName("거래내역조회_테스트_데이터있을때")
    @Transactional
    public void getTransList() throws Exception {
        // #1. Given
        beforeProcGet();

        TransGetListDTO.Request request1 = TransGetListDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .transFg(TransFg.TRANSFER.code())
                .bankCd(ACCOUNT_NO.substring(0, BaseCode.BANK_CD_LENGTH))
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transListRequestBuilder(objectMapper.writeValueAsString(request1))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("dataCnt").value(1))
                .andExpect(jsonPath("$.dataList[0].bankName").value("국민은행"))
                .andExpect(jsonPath("$.dataList[0].toBankName").value("신한은행"))
                .andExpect(jsonPath("$.dataList[0].memberName").value("KIM"))
                .andExpect(jsonPath("$.dataList[0].toMemberName").value("OH"))
                .andExpect(jsonPath("$.dataList[0].transText").value("축의금"));
    }

    @Test
    @DisplayName("거래내역조회_테스트_데이터없을때")
    @Transactional
    public void getTransList_null() throws Exception {
        // #1. Given
        beforeProcGet();

        TransGetListDTO.Request request1 = TransGetListDTO.Request.builder()
                .memberNo(TO_MEMBER_NO)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transListRequestBuilder(objectMapper.writeValueAsString(request1))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("dataCnt").value(0))
                .andExpect(jsonPath("dataList").isEmpty());
    }


    public RequestBuilder transListRequestBuilder(String content) {
        return MockMvcRequestBuilders.get(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }



    @Test
    @DisplayName("거래상세조회_테스트_데이터있을때")
    @Transactional
    public void getTransInfo() throws Exception {
        // #1. Given
        beforeProcGet();

        String transId = "333333333";

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transInfoRequestBuilder(transId)
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("transId").value(transId))
                .andExpect(jsonPath("transFg").value(TransFg.TRANSFER.code().toString()))
                .andExpect(jsonPath("transFgName").value(TransFg.TRANSFER.getName()))
                .andExpect(jsonPath("memberNo").value(MEMBER_NO))
                .andExpect(jsonPath("memberName").value("KIM"))
                .andExpect(jsonPath("bankCd").value("101"))
                .andExpect(jsonPath("bankName").value("국민은행"))
                .andExpect(jsonPath("accountNo").value(ACCOUNT_NO))
                .andExpect(jsonPath("toAccountNo").value(TO_ACCOUNT_NO))
                .andExpect(jsonPath("toMemberNo").value(TO_MEMBER_NO))
                .andExpect(jsonPath("toMemberName").value("OH"))
                .andExpect(jsonPath("toBankCd").value("102"))
                .andExpect(jsonPath("toBankName").value("신한은행"))
                .andExpect(jsonPath("transAmt").value(2000))
                .andExpect(jsonPath("transText").value("축의금"))
                .andExpect(jsonPath("memo").value("이체 테스트"))
                .andExpect(jsonPath("transDateStr").isNotEmpty());
    }


    @Test
    @DisplayName("거래상세조회_테스트_데이터없을때")
    @Transactional
    public void getTransInfo_null() throws Exception {
        // #1. Given
        beforeProcGet();

        String transId = "4444434545";

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transInfoRequestBuilder(transId)
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_TRANS.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_TRANS.getMessage()));
    }

    public RequestBuilder transInfoRequestBuilder(String transId) {
        StringBuilder urlBuilder = new StringBuilder(URL)
                .append("/")
                .append(URLEncoder.encode(transId, StandardCharsets.UTF_8));

        String url = urlBuilder.toString().replaceAll("%22", "");

        return MockMvcRequestBuilders.get(url)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
    }

    public void beforeProcGet() throws Exception {
        // 1. 계좌 등록 (FR, TO)
        saveAccount(ACCOUNT_NO, MEMBER_NO);
        saveAccount(TO_ACCOUNT_NO, 3L);

        // 2. 입금 10000원
        transMapper.save(TransVO.builder()
                .transId("1111111")
                .transFg(TransFg.DEPOSIT.code())
                .transAmt(10000)
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(ACCOUNT_NO.substring(0, BaseCode.BANK_CD_LENGTH))
                .memo("입금 테스트")
                .build());

        // 3. 출금 3000원
        transMapper.save(TransVO.builder()
                .transId("2222222")
                .transFg(TransFg.WITHDRAW.code())
                .transAmt(3000)
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(ACCOUNT_NO.substring(0, BaseCode.BANK_CD_LENGTH))
                .memo("출금 테스트")
                .build());

        // 4. 이체 2000원
        transMapper.save(TransVO.builder()
                .transId("333333333")
                .transFg(TransFg.TRANSFER.code())
                .transAmt(2000)
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(ACCOUNT_NO.substring(0, BaseCode.BANK_CD_LENGTH))
                .toMemberNo(TO_MEMBER_NO)
                .toAccountNo(TO_ACCOUNT_NO)
                .toBankCd(TO_ACCOUNT_NO.substring(0, BaseCode.BANK_CD_LENGTH))
                .memo("이체 테스트")
                .transText("축의금")
                .build());


    }


    public void saveAccount(String accountNo, Long memberNo) throws Exception {
        accountMapper.save(AccountVO.builder()
                .accountNo(accountNo)
                .bankCd(accountNo.substring(0, BaseCode.BANK_CD_LENGTH))
                .memberNo(memberNo)
                .pin("123456")
                .build());
    }


}
