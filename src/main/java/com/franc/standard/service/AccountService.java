package com.franc.standard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.AccountStatus;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountMapper accountMapper;

    private final ObjectMapper objectMapper;


    /**
     * 계좌 등록(재등록)
     * @param paramVO
     * @throws Exception
     */
    public void saveAccount(AccountVO paramVO) throws Exception {
        boolean reUse = false;

        // #1. 기등록여부 체크 (해지의 경우 재등록 마킹)
        Map<String, Object> checkAccountParamMap = objectMapper.convertValue(paramVO, Map.class);
        AccountVO accountVO = accountMapper.findById(checkAccountParamMap);
        if(accountVO != null) {
           if(accountVO.getStatus() == AccountStatus.WITHDRAWAL.code()) {
               reUse = true;
           } else {
               throw new BizException(ExceptionResult.ALEADY_SAVE_ACCOUNT);
           }
        }

        // #2. 계좌 등록(재등록)
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
        Map<String, Object> checkAccountParamMap = objectMapper.convertValue(paramVO, Map.class);
        AccountVO accountVO = accountMapper.findById(checkAccountParamMap);
        
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

}
