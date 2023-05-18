package com.franc.standard.repository;

import com.franc.standard.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    MemberVO findById(@Param("memberNo") Long MemberNo) throws Exception;

}
