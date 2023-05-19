package com.franc.standard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.AccountStatus;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.BankVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountMapper accountMapper;

    private final BankService bankService;

    private final ObjectMapper objectMapper;


    /**
     * 계좌 등록(재등록)
     * @param paramVO
     * @throws Exception
     */
    public void saveAccount(AccountVO paramVO) throws Exception {
        boolean reUse = false;
        String accountNo = paramVO.getAccountNo();

        // #1. 계좌번호 유효성 체크 및 은행정보 가져오기
        BankVO bankVO = bankService.findAndCheckByIdToAccountNo(accountNo);

        // 은행코드가 없으면 넣어주기
        if(!StringUtils.hasText(paramVO.getBankCd()))
            paramVO.setBankCd(bankVO.getBankCd());


        // #2. 기등록여부 체크 (해지의 경우 재등록 마킹)
        AccountVO accountVO = accountMapper.findById(accountNo);
        if(accountVO != null) {
           if(accountVO.getStatus() == AccountStatus.WITHDRAWAL.code()) {
               reUse = true;
           } else {
               throw new BizException(ExceptionResult.ALEADY_SAVE_ACCOUNT);
           }
        }

        // #3. 계좌 등록(재등록)
        if(reUse) {
            accountVO.reUse();
        } else {
            accountVO = paramVO;
        }

        accountMapper.save(accountVO);
        logger.info("계좌등록 성공 : " + accountVO.getAccountNo());
    }


    /**
     * 계좌 해지
     * @param paramVO
     * @throws Exception
     */
    public void withdrawalAccount(AccountVO paramVO) throws Exception {

        // #1. 계좌정보 가져오기
        String accountNo = paramVO.getAccountNo();
        AccountVO accountVO = accountMapper.findById(accountNo);
        
        // #2. 유효성 체크
        if(accountVO == null) {
            throw new BizException(ExceptionResult.NOT_FOUND_ACCOUNT);
        }
        if(AccountStatus.USE.code() != accountVO.getStatus()) {
            throw new BizException(ExceptionResult.NOT_ACTIVE_ACCOUNT);
        }
        if(!paramVO.getPin().equals(accountVO.getPin())) {
            throw new BizException(ExceptionResult.WRONG_PIN_NUMBER);
        }
        
        // #3. 해지
        accountVO.withdrawal();
        accountMapper.save(accountVO);
        logger.info("계좌해지 성공 : " + accountVO.getAccountNo());
        
    }


    public AccountVO getAccount(String accountNo) throws Exception {
        return accountMapper.findById(accountNo);
    }

    public List<AccountVO> getAccounts(Map<String, Object> paramMap) throws Exception {
        return accountMapper.findAll(paramMap);
    }

}
