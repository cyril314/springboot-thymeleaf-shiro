package com.fit.service;

import com.fit.dao.SysRoleDao;
import com.fit.model.SysRole;
import com.fit.common.base.BaseCrudService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2018/10/24
 */
@Service
public class SysRoleService extends BaseCrudService<SysRoleDao, SysRole> {

    public String getUserByUserId(String id) {
        String roleId = "";
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT `role_id` FROM `sys_user_role` WHERE `user_id`=?");
            roleId = this.getJdbcTemplate().queryForObject(sb.toString(), new Object[]{id}, String.class);
        } catch (DataAccessException e) {
        }
        return roleId;
    }

    public void setUserRole(String roleId, String userId) {
        this.getJdbcTemplate().update("DELETE FROM `sys_user_role` WHERE `user_id`=?", userId);
        this.getJdbcTemplate().update("INSERT INTO `sys_user_role`(`role_id`,`user_id`) VALUES (?,?)", roleId, userId);
    }

    public String getAuthByRoleId(String id) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT `auth_id` FROM `sys_role_auth` WHERE `role_id`=?");
            return this.getJdbcTemplate().queryForObject(sb.toString(), new Object[]{id}, String.class);
        } catch (DataAccessException e) {
        }
        return "";
    }

    public void setAuthRole(String roleId, String authId) {
        this.getJdbcTemplate().update("DELETE FROM `sys_role_auth` WHERE `role_id`=?", roleId);
        this.getJdbcTemplate().update("INSERT INTO `sys_role_auth` (`role_id`,`auth_id`) VALUES (?,?)", roleId, authId);
    }
}
