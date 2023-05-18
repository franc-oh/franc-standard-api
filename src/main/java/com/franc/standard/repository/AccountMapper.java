package com.franc.standard.repository;

import com.franc.standard.vo.AccountVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface AccountMapper {

    AccountVO findById(Map<String, Object> paramMap) throws Exception;

    void save(AccountVO vo) throws Exception;

}
