package com.franc.standard.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.TransFg;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.TransMapper;
import com.franc.standard.vo.TransVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransServiceTests {

    @InjectMocks
    private TransService transService;

    @Mock
    private TransMapper transMapper;

    @Mock
    private AccountService accountService;

    @Spy
    private ObjectMapper objectMapper;


    private static final Character TRANS_FG = TransFg.TRANSFER.code();
    private static final int TRANS_AMT = 10000;
    private static final long MEMBER_NO = 1L;
    private static final String BANK_CD = "101";
    private static final String ACCOUNT_NO = "1010000022";
    private static final long TO_MEMBER_NO = 2L;
    private static final String TO_BANK_CD = "103";
    private static final String TO_ACCOUNT_NO = "1035934839402";


    /**
     * 계좌번호 체크
     * 거래금액 (잔액체크) -> 동시성
     * [이체] TO_에 정보가 있는지 체크
     * [이체] 계좌번호 체크
     */
    @Test
    @DisplayName("계좌거래실패_해당계좌 없음")
    public void trans_fail_not_found_account() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .build();

        when(accountService.getAccount(anyString()))
                .thenReturn(null);

        // #2. When
        BizException exception
                = assertThrows(BizException.class, () -> transService.trans(transVO));

        // #3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_FOUND_ACCOUNT);
        verify(accountService, times(1)).getAccount(anyString());


    }



}
