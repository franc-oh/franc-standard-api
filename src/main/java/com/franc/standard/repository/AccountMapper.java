package com.franc.standard.repository;

import com.franc.standard.vo.AccountVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper {

    /**
     * 단건조회
     * @param accountNo
     * @return
     * @throws Exception
     */
    AccountVO findById(String accountNo) throws Exception;
    AccountVO findByIdAndLocking(String accountNo) throws Exception;

    /**
     * 리스트조회
     * @param paramMap {memberNo, {bankCd, status, offset, limit}}
     * @return
     * @throws Exception
     */
    List<AccountVO> findAll(Map<String, Object> paramMap) throws Exception;

    void save(AccountVO vo) throws Exception;

}
