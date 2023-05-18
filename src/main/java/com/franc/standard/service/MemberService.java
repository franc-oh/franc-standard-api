package com.franc.standard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.standard.exception.BizException;
import com.franc.standard.exception.ExceptionResult;
import com.franc.standard.repository.MemberMapper;
import com.franc.standard.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    private final MemberMapper memberMapper;

    private final ObjectMapper objectMapper;


    public MemberVO findAndCheckById(Long memberNo) throws Exception {
        MemberVO vo = memberMapper.findById(memberNo);
        if(vo == null)
            throw new BizException(ExceptionResult.NOT_FOUND_MEMBER);

        return vo;
    }

}
