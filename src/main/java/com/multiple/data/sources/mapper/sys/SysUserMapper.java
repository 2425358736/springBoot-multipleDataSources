package com.multiple.data.sources.mapper.sys;

import com.multiple.data.sources.domain.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author admin
 */
@Mapper
public interface SysUserMapper {

    /**
     * 插入
     * @param sysUser
     * @return
     */
    int insertSelective(SysUser sysUser);
}
