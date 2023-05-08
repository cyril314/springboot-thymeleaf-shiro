package com.fit.dao;

import com.fit.model.SysUser;
import com.fit.common.base.BaseCrudDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2019/03/21
 */
@Mapper
public interface SysUserDao extends BaseCrudDao<SysUser> {
}