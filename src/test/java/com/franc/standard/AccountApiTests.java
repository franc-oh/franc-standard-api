package com.franc.standard;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.BaseCode;
import com.franc.standard.controller.AccountController;
import com.franc.standard.dto.AccountSaveDTO;
import com.franc.standard.exception.ControllerExceptionHandler;
import com.franc.standard.exception.ExceptionResult;
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
public class AccountApiTests {

    @Autowired
    private AccountController accountController;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    private static final String URL = "/api/standard/account";
    private static final Long MEMBER_NO = 1L;
    private static final String ACCOUNT_NO = "1234111122290";
    private static final String BANK_CD = "102";
    private static final String PIN = "123456";




    @Test
    @DisplayName("계좌등록실패 - 유효성")
    @Transactional
    public void save_exception_valid() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder().build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                saveRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.PARAMETER_NOT_VALID.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.PARAMETER_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("계좌등록실패 - 회원X")
    @Transactional
    public void save_exception_NoMember() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(99999999999999L)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .pin(PIN)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                saveRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_MEMBER.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_MEMBER.getMessage()));
    }

    @Test
    @DisplayName("계좌등록실패 - 은행X")
    @Transactional
    public void save_exception_NoBank() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd("1")
                .pin(PIN)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                saveRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_BANK.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_BANK.getMessage()));
    }


    @Test
    @DisplayName("계좌등록성공")
    @Transactional
    public void save_exception_business() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .pin(PIN)
                .build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                saveRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

    }


    public RequestBuilder saveRequestBuilder(String content) {
        return MockMvcRequestBuilders.post(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }


}
