package com.franc.standard.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.franc.standard.code.AccountStatus;
import com.franc.standard.code.BaseCode;
import com.franc.standard.dto.*;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.service.AccountService;
import com.franc.standard.service.MemberService;
import com.franc.standard.service.TransService;
import com.franc.standard.vo.AccountVO;
import com.franc.standard.vo.MemberVO;
import com.franc.standard.vo.TransVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/standard/trans")
@RequiredArgsConstructor
public class TransController {
    private static final Logger logger = LoggerFactory.getLogger(TransController.class);

    private final TransService transService;

    private final MemberService memberService;

    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> trans(@RequestBody @Valid TransDTO.Request request) throws Exception {
        TransDTO.Response response = new TransDTO.Response();

        logger.info("거래_Request => {}", request.toString());


        // #1. 회원정보 체크 및 가져오기
        MemberVO memberVO = memberService.findAndCheckById(request.getMemberNo());

        // #2. 거래
        TransVO transVO = objectMapper.convertValue(request, TransVO.class);
        transService.trans(transVO);

        // #3. 응답처리
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        logger.info("계좌등록_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
