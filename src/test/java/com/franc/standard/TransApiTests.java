package com.franc.standard;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.BaseCode;
import com.franc.standard.code.TransFg;
import com.franc.standard.controller.TransController;
import com.franc.standard.dto.TransDTO;
import com.franc.standard.exception.ControllerExceptionHandler;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.vo.AccountVO;
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
        saveAccount();

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


    public void saveAccount() throws Exception {
        accountMapper.save(AccountVO.builder()
                .accountNo(ACCOUNT_NO)
                .bankCd(ACCOUNT_NO.substring(0, BaseCode.BANK_CD_LENGTH))
                .memberNo(MEMBER_NO)
                .pin("123456")
                .build());
    }


}
