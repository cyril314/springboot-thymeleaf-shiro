package com.fit.service;

import com.fit.common.utils.MenusUtil;
import com.fit.dao.*;
import com.fit.model.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 菜单权限服务
 * @Author AIM
 * @DATE 2019/3/5
 */
@Service
public class SysMenuService {

    @Resource
    private SysUserRoleDao userRoleDao;
    @Resource
    private SysRoleAuthDao roleAuthDao;
    @Resource
    private SysAuthoritiesResDao authoritiesResDao;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * @AUTO 获取栏目列表
     * @DATE 2019/3/5
     */
    public List<SysMenus> getMenuList(SysUser user) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", user.getId());
        List<SysUserRole> sysUserRoles = userRoleDao.findList(map);
        if (sysUserRoles.size() > 0) {
            SysUserRole sysUserRole = sysUserRoles.get(0);
            map.clear();
            map.put("roleId", sysUserRole.getRoleId());
            List<SysRoleAuth> sysRoleAuths = roleAuthDao.findList(map);
            if (sysRoleAuths.size() > 0) {
                SysRoleAuth sysRoleAuth = sysRoleAuths.get(0);
                map.clear();
                map.put("authId", sysRoleAuth.getAuthId());
                List<SysAuthoritiesRes> authoritiesResList = authoritiesResDao.findList(map);
                StringBuffer sb = new StringBuffer();
                sb.append("select * from `sys_resources` where `type` != 'A' AND id in (");
                for (SysAuthoritiesRes sysAuthoritiesRes : authoritiesResList) {
                    sb.append(sysAuthoritiesRes.getResId()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
                List<SysMenus> list = jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<SysMenus>(SysMenus.class));
                return getChildPerms(list, 0);
            }
        }

        return null;
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     */
    public List<SysMenus> getChildPerms(List<SysMenus> list, int parentId) {
        return MenusUtil.getChildPerms(list, parentId);
    }

}
