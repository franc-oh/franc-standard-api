package com.franc.standard.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.franc.standard.code.BaseCode;
import com.franc.standard.dto.TransDTO;
import com.franc.standard.dto.TransGetInfoDTO;
import com.franc.standard.dto.TransGetListDTO;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.service.MemberService;
import com.franc.standard.service.TransService;
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


    @GetMapping("/{transId}")
    public ResponseEntity<?> getInfo(@PathVariable("transId") String transId) throws Exception {
        TransGetInfoDTO.Response response = new TransGetInfoDTO.Response();

        logger.info("거래상세조회_Request => {}", transId);

        // #1. 거래정보 가져오기
        TransVO transVO = transService.getTransInfo(transId);
        if(transVO == null) {
            throw new BizException(ExceptionResult.NOT_FOUND_TRANS);
        }

        // #3. 응답
        response = objectMapper.convertValue(transVO, TransGetInfoDTO.Response.class);
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        logger.info("거래상세조회_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestBody @Valid TransGetListDTO.Request request) throws Exception {
        TransGetListDTO.Response response = new TransGetListDTO.Response();

        logger.info("거래내역조회_Request => {}", request.toString());

        // #1. 회원정보 체크 및 가져오기
        MemberVO memberVO = memberService.findAndCheckById(request.getMemberNo());

        // #2. 거래내역 조회
        Map<String, Object> paramMap = objectMapper.convertValue(request, Map.class);
        List<TransVO> transVOList = transService.getTransList(paramMap);

        // #3. 응답
        response.setResultCode(BaseCode.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(BaseCode.RESPONSE_MESSAGE_SUCCESS);

        if(!transVOList.isEmpty()) {
            response.setDataList(
                    objectMapper.convertValue(
                            transVOList,
                            TypeFactory.defaultInstance().constructCollectionType(
                                    List.class,
                                    TransGetListDTO.TransInfo.class
                            )
                    )
            );
        }

        logger.info("거래내역조회_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
