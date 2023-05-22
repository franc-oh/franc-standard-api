package com.franc.standard.repository;

import com.franc.standard.vo.TransVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TransMapper {

    String issueTransId() throws Exception;

    TransVO findById(@Param("transId") String transId) throws Exception;
    List<TransVO> findAll(Map<String, Object> map) throws Exception;

    void save(TransVO vo) throws Exception;

}
