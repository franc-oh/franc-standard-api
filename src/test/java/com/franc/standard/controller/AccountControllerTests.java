package com.franc.standard.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.BaseCode;
import com.franc.standard.dto.AccountSaveDTO;
import com.franc.standard.dto.AccountWithdrawalDTO;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ControllerExceptionHandler;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.service.AccountService;
import com.franc.standard.service.BankService;
import com.franc.standard.service.MemberService;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.BankVO;
import com.franc.standard.vo.MemberVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTests {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @Mock
    private MemberService memberService;

    @Mock
    private BankService bankService;

    @Spy
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


    private static final String URL = "/api/standard/accounts";
    private static final Long MEMBER_NO = 1L;
    private static final String ACCOUNT_NO = "1234111122290";
    private static final String BANK_CD = "102";
    private static final String PIN = "123456";


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    @DisplayName("계좌등록실패 - 유효성")
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
    public void save_exception_NoMember() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(99999999999999L)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .pin(PIN)
                .build();

        when(memberService.findAndCheckById(anyLong()))
                .thenThrow(new BizException(ExceptionResult.NOT_FOUND_MEMBER));

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
    public void save_exception_NoBank() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd("1")
                .pin(PIN)
                .build();

        when(memberService.findAndCheckById(anyLong()))
                .thenReturn(MemberVO.builder().build());

        when(bankService.findAndCheckById(anyString()))
                .thenThrow(new BizException(ExceptionResult.NOT_FOUND_BANK));

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
    public void save() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .pin(PIN)
                .build();

        when(memberService.findAndCheckById(anyLong()))
                .thenReturn(MemberVO.builder().build());

        when(bankService.findAndCheckById(anyString()))
                .thenReturn(BankVO.builder().build());

        doNothing()
                .when(accountService)
                .saveAccount(any(AccountVO.class));


        // #2. When
        ResultActions resultActions = mockMvc.perform(
                saveRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

        verify(memberService, times(1)).findAndCheckById(anyLong());
        verify(bankService, times(1)).findAndCheckById(anyString());
        verify(accountService, times(1)).saveAccount(any(AccountVO.class));
    }

    public RequestBuilder saveRequestBuilder(String content) {
        return MockMvcRequestBuilders.post(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }



    @Test
    @DisplayName("계좌해지실패 - 유효성")
    public void withdrawal_exception_valid() throws Exception {
        // #1. Given
        AccountWithdrawalDTO.Request request = AccountWithdrawalDTO.Request.builder().build();

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                withdrawalRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.PARAMETER_NOT_VALID.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.PARAMETER_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("계좌해지실패 - 회원X")
    public void withdrawal_exception_NoMember() throws Exception {
        // #1. Given
        AccountWithdrawalDTO.Request request = AccountWithdrawalDTO.Request.builder()
                .memberNo(99999999999999L)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .pin(PIN)
                .build();

        when(memberService.findAndCheckById(anyLong()))
                .thenThrow(new BizException(ExceptionResult.NOT_FOUND_MEMBER));

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
    @DisplayName("계좌해지성공")
    public void withdrawal() throws Exception {
        // #1. Given
        AccountWithdrawalDTO.Request request = AccountWithdrawalDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .bankCd(BANK_CD)
                .pin(PIN)
                .build();

        when(memberService.findAndCheckById(anyLong()))
                .thenReturn(MemberVO.builder().build());

        doNothing()
                .when(accountService)
                .withdrawalAccount(any(AccountVO.class));


        // #2. When
        ResultActions resultActions = mockMvc.perform(
                withdrawalRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isAccepted())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

        verify(memberService, times(1)).findAndCheckById(anyLong());
        verify(accountService, times(1)).withdrawalAccount(any(AccountVO.class));
    }

    public RequestBuilder withdrawalRequestBuilder(String content) {
        return MockMvcRequestBuilders.delete(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }



}
