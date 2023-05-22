package com.franc.standard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.TransFg;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.TransVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TransService {
    private static final Logger logger = LoggerFactory.getLogger(TransService.class);

    private final TransService transService;

    private final AccountService accountService;

    private final BankService bankService;

    private final ObjectMapper objectMapper;


    public void trans(TransVO transVO) throws Exception {

        // #1. 계좌조회 및 체크
        String accountNo = transVO.getAccountNo();
        AccountVO accountVO = accountService.getAccount(accountNo);
        if(accountVO == null) {
            throw new BizException(ExceptionResult.NOT_FOUND_ACCOUNT);
        }

    }

}
