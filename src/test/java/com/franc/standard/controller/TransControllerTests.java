package com.franc.standard.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.BaseCode;
import com.franc.standard.code.TransFg;
import com.franc.standard.dto.TransDTO;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ControllerExceptionHandler;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.service.MemberService;
import com.franc.standard.service.TransService;
import com.franc.standard.vo.MemberVO;
import com.franc.standard.vo.TransVO;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TransControllerTests {

    @InjectMocks
    private TransController transController;

    @Mock
    private TransService transService;

    @Mock
    private MemberService memberService;

    @Spy
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


    private static final String URL = "/api/standard/trans";
    private static final Long MEMBER_NO = 1L;
    private static final String ACCOUNT_NO = "1014111122290";
    private static final String TO_ACCOUNT_NO = "10200000000";


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(transController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    @DisplayName("거래실패 - 유효성")
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
    public void trans_exception_NoMember() throws Exception {
        // #1. Given
        TransDTO.Request request = TransDTO.Request.builder()
                .memberNo(99999999999999L)
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.DEPOSIT.code())
                .transAmt(1000)
                .build();

        when(memberService.findAndCheckById(anyLong()))
                .thenThrow(new BizException(ExceptionResult.NOT_FOUND_MEMBER));

        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_MEMBER.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_MEMBER.getMessage()));

        verify(memberService, times(1)).findAndCheckById(anyLong());
    }

    @Test
    @DisplayName("거래성공")
    public void trans() throws Exception {
        // #1. Given
        TransDTO.Request request = TransDTO.Request.builder()
                .memberNo(99999999999999L)
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.DEPOSIT.code())
                .transAmt(1000)
                .build();

        when(memberService.findAndCheckById(anyLong()))
                .thenReturn(MemberVO.builder().build());

        doNothing()
                .when(transService)
                .trans(any(TransVO.class));


        // #2. When
        ResultActions resultActions = mockMvc.perform(
                transRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isAccepted())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

        verify(memberService, times(1)).findAndCheckById(anyLong());
        verify(transService, times(1)).trans(any(TransVO.class));
    }


    public RequestBuilder transRequestBuilder(String content) {
        return MockMvcRequestBuilders.post(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }

}
