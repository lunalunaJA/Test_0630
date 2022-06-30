package com.zenithst.core.status.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.status.vo.ZappApmExtend;

public interface ZappApmMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappApm vo);
    int insertu(ZappApm vo);
    int insertb(Map<String, Object> params);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("qryopt") ZappQryOpt qryopt, @Param("dynamic") ZappDynamic dynamic);
    ZappApm selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappApm vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappApm vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
    List<ZappApmExtend> selectDbStatus(ZappApm vo);
    
    List selectLockList(ZappApm vo);
    
}