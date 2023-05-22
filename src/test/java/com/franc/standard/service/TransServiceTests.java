package com.franc.standard.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.TransFg;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.repository.TransMapper;
import com.franc.standard.vo.AccountVO;
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

    @Mock
    private AccountMapper accountMapper;

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
    private static final String TRANS_ID = "20230522000000000001";



    @Test
    @DisplayName("계좌거래실패_해당계좌 없음")
    public void trans_fail_not_found_account() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .transAmt(1000)
                .transFg(TransFg.DEPOSIT.code())
                .build();

        when(accountService.getAccountAndLocking(anyString()))
                .thenReturn(null);

        // #2. When
        BizException exception
                = assertThrows(BizException.class, () -> transService.trans(transVO));

        // #3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_FOUND_ACCOUNT);
        verify(accountService, times(1)).getAccountAndLocking(anyString());

    }


    @Test
    @DisplayName("계좌거래실패_출금/이체 시 잔액부족")
    public void trans_fail_insufficient_cach() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.WITHDRAW.code())
                .transAmt(1000)
                .toAccountNo(TO_ACCOUNT_NO)
                .build();

        when(accountService.getAccountAndLocking(anyString()))
                .thenReturn(AccountVO.builder().deposit(100).build());

        // #2. When
        BizException exception
                = assertThrows(BizException.class, () -> transService.trans(transVO));

        // #3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.INSUFFICIENT_CACH);
        verify(accountService, times(1)).getAccountAndLocking(anyString());

    }


    @Test
    @DisplayName("계좌거래실패_이체 시 TO_계좌 없음")
    public void trans_fail_transfer_not_toAccountNo() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.TRANSFER.code())
                .transAmt(10)
                .build();

        when(accountService.getAccountAndLocking(anyString()))
                .thenReturn(AccountVO.builder().deposit(100).build());

        // #2. When
        BizException exception
                = assertThrows(BizException.class, () -> transService.trans(transVO));

        // #3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.TRANSFER_NOT_ACCOUNT);
        verify(accountService, times(1)).getAccountAndLocking(anyString());

    }


    @Test
    @DisplayName("계좌거래실패_이체 시 TO_계좌 찾을 수 없음")
    public void trans_fail_transfer_not_found_toAccountNo() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.TRANSFER.code())
                .transAmt(10)
                .toAccountNo(TO_ACCOUNT_NO)
                .build();

        when(accountService.getAccountAndLocking(anyString()))
                .thenReturn(AccountVO.builder().deposit(100).build())
                .thenReturn(null);

        // #2. When
        BizException exception
                = assertThrows(BizException.class, () -> transService.trans(transVO));

        // #3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.TRANSFER_NOT_ACCOUNT);
        verify(accountService, times(2)).getAccountAndLocking(anyString());

    }


    @Test
    @DisplayName("계좌거래성공_입금")
    public void trans_deposit_deposit() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.DEPOSIT.code())
                .transAmt(1000)
                .build();

        when(accountService.getAccountAndLocking(anyString()))
                .thenReturn(AccountVO.builder().deposit(0).build());

        when(transMapper.issueTransId())
                .thenReturn(TRANS_ID);

        doNothing().when(transMapper).save(any(TransVO.class));
        doNothing().when(accountMapper).save(any(AccountVO.class));

        // #2. When
        transService.trans(transVO);

        // #3. Then
        verify(accountService, times(1)).getAccountAndLocking(anyString());
        verify(transMapper, times(1)).issueTransId();
        verify(transMapper, times(1)).save(any(TransVO.class));
        verify(accountMapper, times(1)).save(any(AccountVO.class));

    }


    @Test
    @DisplayName("계좌거래성공_출금")
    public void trans_deposit_withdraw() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .transFg(TransFg.WITHDRAW.code())
                .transAmt(1000)
                .build();

        when(accountService.getAccountAndLocking(anyString()))
                .thenReturn(AccountVO.builder().deposit(10000).build());

        when(transMapper.issueTransId())
                .thenReturn(TRANS_ID);

        doNothing().when(transMapper).save(any(TransVO.class));
        doNothing().when(accountMapper).save(any(AccountVO.class));

        // #2. When
        transService.trans(transVO);

        // #3. Then
        verify(accountService, times(1)).getAccountAndLocking(anyString());
        verify(transMapper, times(1)).issueTransId();
        verify(transMapper, times(1)).save(any(TransVO.class));
        verify(accountMapper, times(1)).save(any(AccountVO.class));

    }


    @Test
    @DisplayName("계좌거래성공_이체")
    public void trans_deposit_transfer() throws Exception {
        // #1. Given
        TransVO transVO = TransVO.builder()
                .accountNo(ACCOUNT_NO)
                .toAccountNo(TO_ACCOUNT_NO)
                .transFg(TransFg.TRANSFER.code())
                .transAmt(1000)
                .build();

        when(accountService.getAccountAndLocking(anyString()))
                .thenReturn(AccountVO.builder().deposit(10000).build())
                .thenReturn(AccountVO.builder().accountNo(TO_ACCOUNT_NO).build());

        when(transMapper.issueTransId())
                .thenReturn(TRANS_ID);

        doNothing().when(transMapper).save(any(TransVO.class));
        doNothing().when(accountMapper).save(any(AccountVO.class));

        // #2. When
        transService.trans(transVO);

        // #3. Then
        verify(accountService, times(2)).getAccountAndLocking(anyString());
        verify(transMapper, times(1)).issueTransId();
        verify(transMapper, times(1)).save(any(TransVO.class));
        verify(accountMapper, times(2)).save(any(AccountVO.class));

    }
}
