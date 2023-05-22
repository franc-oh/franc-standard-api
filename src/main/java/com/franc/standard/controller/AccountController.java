package com.franc.standard.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.franc.standard.code.AccountStatus;
import com.franc.standard.code.BaseCode;
import com.franc.standard.dto.AccountGetInfoDTO;
import com.franc.standard.dto.AccountGetListDTO;
import com.franc.standard.dto.AccountSaveDTO;
import com.franc.standard.dto.AccountWithdrawalDTO;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.service.AccountService;
import com.franc.standard.service.MemberService;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/standard/accounts")
@RequiredArgsConstructor
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    private final MemberService memberService;

    private final ObjectMapper objectMapper;

    @GetMapping("/{accountNo}")
    public ResponseEntity<?> getInfo(@PathVariable("accountNo") String accountNo) throws Exception {
        AccountGetInfoDTO.Response response = new AccountGetInfoDTO.Response();

        logger.info("계좌조회_Request => {}", accountNo);

        // #1. 계좌정보 가져오기
        AccountVO accountVO = accountService.getAccount(accountNo);
        if(accountVO == null) {
            throw new BizException(ExceptionResult.NOT_FOUND_ACCOUNT);
        }

        // #3. 응답
        response = objectMapper.convertValue(accountVO, AccountGetInfoDTO.Response.class);
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        logger.info("계좌조회_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestBody @Valid AccountGetListDTO.Request request) throws Exception {
        AccountGetListDTO.Response response = new AccountGetListDTO.Response();

        logger.info("계좌목록조회_Request => {}", request.toString());

        // #1. 회원정보 체크 및 가져오기
        MemberVO memberVO = memberService.findAndCheckById(request.getMemberNo());

        // #2. 내 계좌리스트 가져오기
        Map<String, Object> paramMap = objectMapper.convertValue(request, Map.class);
        paramMap.put("status", AccountStatus.USE.code()); // '사용'중인 계좌만 조회
        List<AccountVO> accountVOList = accountService.getAccounts(paramMap);

        // #3. 응답
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        if(!accountVOList.isEmpty()) {
            response.setDataList(
                    objectMapper.convertValue(
                            accountVOList,
                            TypeFactory.defaultInstance().constructCollectionType(
                                    List.class,
                                    AccountGetListDTO.AccountInfo.class
                            )
                    )
            );
        }

        logger.info("계좌목록조회_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid AccountSaveDTO.Request request) throws Exception {
        AccountSaveDTO.Response response = new AccountSaveDTO.Response();

        logger.info("계좌등록_Request => {}", request.toString());

        // #1. 회원정보 체크 및 가져오기
        MemberVO memberVO = memberService.findAndCheckById(request.getMemberNo());

        // #2. 계좌등록
        AccountVO accountVO = objectMapper.convertValue(request, AccountVO.class);
        accountService.saveAccount(accountVO);

        // #3. 응답처리
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        logger.info("계좌등록_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @DeleteMapping
    public ResponseEntity<?> withdrawal(@RequestBody @Valid AccountWithdrawalDTO.Request request) throws Exception {
        AccountWithdrawalDTO.Response response = new AccountWithdrawalDTO.Response();

        logger.info("계좌해지_Request => {}", request.toString());

        // #1. 회원정보 체크 및 가져오기
        MemberVO memberVO = memberService.findAndCheckById(request.getMemberNo());

        // #2. 계좌해지
        AccountVO accountVO = objectMapper.convertValue(request, AccountVO.class);
        accountService.withdrawalAccount(accountVO);

        // #3. 응답처리
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        logger.info("계좌해지_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
