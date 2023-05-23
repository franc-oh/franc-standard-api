package com.franc.standard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.TransFg;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.AccountMapper;
import com.franc.standard.repository.TransMapper;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.TransVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransService {
    private static final Logger logger = LoggerFactory.getLogger(TransService.class);

    private final TransMapper transMapper;

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    private final ObjectMapper objectMapper;


    /**
     * 거래
     * @param transVO
     * @throws Exception
     */
    @Transactional
    public void trans(TransVO transVO) throws Exception {
        char transFg = transVO.getTransFg();

        // #1. 계좌조회 및 체크
        String accountNo = transVO.getAccountNo();
        int transAmt = transVO.getTransAmt();
        AccountVO accountVO = accountService.getAccountAndLocking(accountNo);
        if(accountVO == null) {
            throw new BizException(ExceptionResult.NOT_FOUND_ACCOUNT);
        }

        transVO.setMemberNo(accountVO.getMemberNo());
        transVO.setBankCd(accountVO.getBankCd());


        // #2. 거래구분 별 유효성 체크 및 저장값 셋팅
        int deposit = accountVO.getDeposit();
        int toDeposit = 0;
        String toAccountNo = transVO.getToAccountNo();
        int afterDeposit = 0;
        AccountVO toAccountVO = null;
        int toAfterDeposit = transAmt;

        if(TransFg.WITHDRAW.code() == transFg) {
            afterDeposit = deposit - transAmt;

            // 출금 시 잔액조회
            if(afterDeposit < 0)
                throw new BizException(ExceptionResult.INSUFFICIENT_CACH);


        } else if(TransFg.TRANSFER.code() == transFg) {
            afterDeposit = deposit - transAmt;

            // 이체 시 잔액조회
            if(afterDeposit < 0)
                throw new BizException(ExceptionResult.INSUFFICIENT_CACH);

            // 이체 시 계좌 유효성 체크 및 값 세팅
            if (!StringUtils.hasText(toAccountNo))
                throw new BizException(ExceptionResult.TRANSFER_NOT_ACCOUNT);

            toAccountVO = accountService.getAccountAndLocking(toAccountNo);
            if (toAccountVO == null) {
                throw new BizException(ExceptionResult.TRANSFER_NOT_ACCOUNT);
            }

            transVO.setToMemberNo(toAccountVO.getMemberNo());
            transVO.setToBankCd(toAccountVO.getBankCd());

            toAfterDeposit += toAccountVO.getDeposit();

        } else {
            afterDeposit = deposit + transAmt;
        }

        // #3. 거래처리
        transVO.setTransId(transMapper.issueTransId());
        transMapper.save(transVO);

        // #5. 잔액 갱신
        accountVO.setDeposit(afterDeposit);
        accountMapper.save(accountVO);

        // #6. 이체인 경우 상대방 잔액 갱신
        if(TransFg.TRANSFER.code() == transFg) {
            accountMapper.save(AccountVO.builder()
                    .accountNo(toAccountNo)
                    .memberNo(toAccountVO.getMemberNo())
                    .bankCd(toAccountVO.getBankCd())
                    .pin(toAccountVO.getPin())
                    .deposit(toAfterDeposit)
                    .build());
        }

    }


    public List<TransVO> getTransList(Map<String, Object> paramMap) throws Exception {
        return transMapper.findAll(paramMap);
    }


    public TransVO getTransInfo(String transId) throws Exception {
        return transMapper.findById(transId);
    }

}
