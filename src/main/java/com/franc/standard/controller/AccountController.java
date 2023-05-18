package com.franc.standard.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.code.BaseCode;
import com.franc.standard.dto.AccountSaveDTO;
import com.franc.standard.service.AccountService;
import com.franc.standard.service.BankService;
import com.franc.standard.service.MemberService;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.BankVO;
import com.franc.standard.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/standard/account")
@RequiredArgsConstructor
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    private final MemberService memberService;

    private final BankService bankService;

    private final ObjectMapper objectMapper;


    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid AccountSaveDTO.Request request) throws Exception {
        AccountSaveDTO.Response response = new AccountSaveDTO.Response();

        logger.info("계좌등록_Request => {}", request.toString());

        // #1. 회원정보 체크 및 가져오기
        MemberVO memberVO = memberService.findAndCheckById(request.getMemberNo());

        // #2. 은행정보 체크 및 가져오기
        BankVO bankVO = bankService.findAndCheckById(request.getBankCd());

        // #3. 계좌등록
        AccountVO accountVO = objectMapper.convertValue(request, AccountVO.class);
        accountService.saveAccount(accountVO);

        // #4. 응답처리
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        logger.info("계좌등록_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
