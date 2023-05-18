package com.franc.standard.repository;

import com.franc.standard.vo.BankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BankMapper {

    List<BankVO> findAll() throws Exception;
    BankVO findById(@Param("bankCd") String bankCd) throws Exception;

}
