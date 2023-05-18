package com.franc.standard.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.AccountStatus;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.vo.AccountVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountMapper accountMapper;

    @Spy
    private ObjectMapper objectMapper;


    private static final Long MEMBER_NO = 1L;
    private static final String ACCOUNT_NO = "1234111122290";
    private static final String BANK_CD = "102";
    private static final String PIN = "123456";


    @Test
    @DisplayName("계좌등록실패 - 해지가 아닌 상태의 동일 계좌가 이미 등록된경우 예외처리")
    public void saveAccount_exception_aleadyExist() throws Exception {
        // #1. Given
        AccountVO mockAccountVO = AccountVO.builder()
                .memberNo(MEMBER_NO)
                .status(AccountStatus.USE.code())
                .build();

        when(accountMapper.findById(anyMap()))
                .thenReturn(mockAccountVO);

        // #2. When
        BizException exception
                = assertThrows(BizException.class, () -> accountService.saveAccount(getAccountVO()));

        // #3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.ALEADY_SAVE_ACCOUNT);
        verify(accountMapper, times(1)).findById(anyMap());
    }

    @Test
    @DisplayName("계좌등록성공 - 신규등록")
    public void saveAccount_new() throws Exception {
        // #1. Given
        when(accountMapper.findById(anyMap()))
                .thenReturn(null);

        doNothing().when(accountMapper).save(any(AccountVO.class));

        // #2. When
        accountService.saveAccount(getAccountVO());

        // #3. Then
        verify(accountMapper, times(1)).findById(anyMap());
        verify(accountMapper, times(1)).save(any(AccountVO.class));
    }

    @Test
    @DisplayName("계좌등록성공 - 해지계좌활성")
    public void saveAccount_re() throws Exception {
        // #1. Given
        AccountVO mockAccountVO = AccountVO.builder()
                .memberNo(MEMBER_NO)
                .status(AccountStatus.WITHDRAWAL.code())
                .build();

        doNothing().when(accountMapper).save(any(AccountVO.class));

        // #2. When
        accountService.saveAccount(getAccountVO());

        // #3. Then
        verify(accountMapper, times(1)).findById(anyMap());
        verify(accountMapper, times(1)).save(any(AccountVO.class));
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
