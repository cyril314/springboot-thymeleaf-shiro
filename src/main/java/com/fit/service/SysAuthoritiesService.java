package com.fit.service;

import com.fit.dao.SysAuthoritiesDao;
import com.fit.model.SysAuthorities;
import com.fit.common.base.BaseCrudService;
import com.fit.common.utils.ObjectUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2018/10/24
 */
@Service
public class SysAuthoritiesService extends BaseCrudService<SysAuthoritiesDao, SysAuthorities> {

    /**
     * 根据角色获取权限列表
     *
     * @param id 角色ID
     */
    public List<String> getRoleAuth(String id) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT `auth_id` FROM `sys_role_auth` WHERE `role_id`=?");
        try {
            String authId = this.getJdbcTemplate().queryForObject(sb.toString(), new Object[]{id}, String.class);
            if (ObjectUtil.isNotEmpty(authId)) {
                sb.setLength(0);
                sb.append("SELECT `res_id` FROM `sys_authorities_res` WHERE `auth_id`=").append(authId);
                List<String> resList = this.getJdbcTemplate().queryForList(sb.toString(), String.class);
                return resList;
            }
        } catch (DataAccessException e) {
            return new ArrayList<String>();
        }
        return new ArrayList<String>();
    }

    public void setRescAuth(String authId, String[] list) {
        this.getJdbcTemplate().update("DELETE FROM `sys_authorities_res` WHERE `auth_id`=?", authId);
        this.getJdbcTemplate().batchUpdate("INSERT INTO `sys_authorities_res` (`auth_id`,`res_id`)VALUES(?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, authId);
                ps.setString(2, list[i]);
            }

            @Override
            public int getBatchSize() {
                return list.length;
            }
        });
    }

    /**
     * 根据ROLE_ID获取权限类型
     */
    public String getAuthByRoleId(String id) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT `auth_id` FROM `sys_role_auth` WHERE `role_id`=?");
            return this.getJdbcTemplate().queryForObject(sb.toString(), new Object[]{id}, String.class);
        } catch (DataAccessException e) {
        }
        return "";
    }

    public void setAuthRole(String authId, String roleId) {
        this.getJdbcTemplate().update("DELETE FROM `sys_role_auth` WHERE `role_id`=?", roleId);
        this.getJdbcTemplate().update("INSERT INTO `sys_role_auth` (`role_id`,`auth_id`) VALUES (?,?)", roleId, authId);
    }
}
