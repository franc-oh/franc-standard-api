package com.franc.standard;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.AccountStatus;
import com.franc.standard.code.BaseCode;
import com.franc.standard.controller.AccountController;
import com.franc.standard.dto.AccountGetInfoDTO;
import com.franc.standard.dto.AccountGetListDTO;
import com.franc.standard.dto.AccountSaveDTO;
import com.franc.standard.dto.AccountWithdrawalDTO;
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
public class AccountApiTests {

    @Autowired
    private AccountController accountController;

    @Autowired
    private AccountMapper accountMapper;

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


    private static final String URL = "/api/standard/accounts";
    private static final Long MEMBER_NO = 1L;
    private static final String ACCOUNT_NO = "1014111122290";
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
    @DisplayName("계좌등록성공")
    @Transactional
    public void save() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
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


    @Test
    @DisplayName("계좌등록성공_재등록")
    @Transactional
    public void save_re() throws Exception {
        // #1. Given
        AccountSaveDTO.Request request = AccountSaveDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .pin(PIN)
                .build();

        AccountVO saveAccountVO = objectMapper.convertValue(request, AccountVO.class);
        accountMapper.save(saveAccountVO);

        saveAccountVO.withdrawal();
        accountMapper.save(saveAccountVO);

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





    @Test
    @DisplayName("계좌해지실패 - 유효성")
    @Transactional
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
    @Transactional
    public void withdrawal_exception_NoMember() throws Exception {
        // #1. Given
        AccountWithdrawalDTO.Request request = AccountWithdrawalDTO.Request.builder()
                .memberNo(99999999999999L)
                .accountNo(ACCOUNT_NO)
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
    @DisplayName("계좌해지성공")
    @Transactional
    public void withdrawal() throws Exception {
        // #1. Given
        AccountWithdrawalDTO.Request request = AccountWithdrawalDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .accountNo(ACCOUNT_NO)
                .pin(PIN)
                .build();

        AccountVO saveAccountVO = objectMapper.convertValue(request, AccountVO.class);
        accountMapper.save(saveAccountVO);


        // #2. When
        ResultActions resultActions = mockMvc.perform(
                withdrawalRequestBuilder(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // #3. Then
        resultActions.andExpect(status().isAccepted())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS));

    }

    public RequestBuilder withdrawalRequestBuilder(String content) {
        return MockMvcRequestBuilders.delete(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }


    @Test
    @DisplayName("내 계좌목록 조회 - 등록 후 케이스별 테스트")
    public void getList() throws Exception {
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

        // 전체조회
        AccountGetListDTO.Request request1 = AccountGetListDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .build();

        // bank1 계좌만 조회
        AccountGetListDTO.Request request2 = AccountGetListDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .bankCd(bankCd1)
                .build();

        // 전체조회 - 페이징 적용
        AccountGetListDTO.Request request3 = AccountGetListDTO.Request.builder()
                .memberNo(MEMBER_NO)
                .offset(2)
                .limit(1)
                .build();

        // #2. When
        ResultActions resultActions1 = mockMvc.perform(
                getListRequestBuilder(objectMapper.writeValueAsString(request1))
        ).andDo(print());
        ResultActions resultActions2 = mockMvc.perform(
                getListRequestBuilder(objectMapper.writeValueAsString(request2))
        ).andDo(print());
        ResultActions resultActions3 = mockMvc.perform(
                getListRequestBuilder(objectMapper.writeValueAsString(request3))
        ).andDo(print());

        // #3. Then
        resultActions1.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("dataCnt").value(3));

        resultActions2.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("dataCnt").value(2))
                .andExpect(jsonPath("$.dataList[0].bankName").value("기업은행"))
                .andExpect(jsonPath("$.dataList[1].bankName").value("기업은행"));

        resultActions3.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("dataCnt").value(1));
    }


    public RequestBuilder getListRequestBuilder(String content) {
        return MockMvcRequestBuilders.get(URL)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .content(content);
    }



    @Test
    @DisplayName("계좌조회 테스트")
    public void getInfo() throws Exception {
        // #1. Given
        String accountNo1 = "101441414";
        String accountNo2 = "103302030";
        String bankCd = "101";

        accountMapper.save(AccountVO.builder()
                .memberNo(MEMBER_NO)
                .accountNo(accountNo1)
                .bankCd(bankCd)
                .pin("123456")
                .build());


        // #2. When
        ResultActions resultActions1 = mockMvc.perform(
                getInfoRequestBuilder(objectMapper.writeValueAsString(accountNo1))
        ).andDo(print());
        ResultActions resultActions2 = mockMvc.perform(
                getListRequestBuilder(objectMapper.writeValueAsString(accountNo2))
        ).andDo(print());

        // #3. Then
        resultActions1.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(BaseCode.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(BaseCode.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("memberNo").value(MEMBER_NO))
                .andExpect(jsonPath("bankCd").value(bankCd))
                .andExpect(jsonPath("bankName").value("국민은행"))
                .andExpect(jsonPath("statusName").value(AccountStatus.USE.getName()))
                .andExpect(jsonPath("deposit").value(0))
                .andExpect(jsonPath("createDateStr").isNotEmpty());

        resultActions2.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_ACCOUNT.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_ACCOUNT.getMessage()));
    }

    public RequestBuilder getInfoRequestBuilder(String accountNo) {
        return MockMvcRequestBuilders.get(URL + "/" + accountNo)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
    }

}
