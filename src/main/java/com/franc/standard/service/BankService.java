package com.franc.standard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.BaseCode;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.BankMapper;
import com.franc.standard.repository.MemberMapper;
import com.franc.standard.vo.BankVO;
import com.franc.standard.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankService {
    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    private final BankMapper bankMapper;

    private final ObjectMapper objectMapper;


    /**
     * 계좌번호로 은행코드 추출하여 은행정보 찾기
     * @param accountNo
     * @return
     * @throws Exception
     */
    public BankVO findAndCheckByIdToAccountNo(String accountNo) throws Exception {

        if(accountNo == null || accountNo.length() < BaseCode.BANK_CD_LENGTH)
            throw new BizException(ExceptionResult.WRONG_ACCOUNT_NO);

        String bankCd = accountNo.substring(0, BaseCode.BANK_CD_LENGTH);
        BankVO vo = bankMapper.findById(bankCd);

        if(vo == null)
            throw new BizException(ExceptionResult.NOT_FOUND_BANK);

        return vo;
    }

}
