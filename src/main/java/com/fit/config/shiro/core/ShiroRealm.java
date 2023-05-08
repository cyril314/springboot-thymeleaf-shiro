package com.fit.config.shiro.core;

import com.fit.model.SysUser;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * @AUTO 自定义授权
 * @Author AIM
 * @DATE 2019/3/20
 */
public class ShiroRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(ShiroRealm.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("##################执行Shiro权限认证##################");
        SysUser user = (SysUser) principalCollection.getPrimaryPrincipal();
        if (user != null) {
            //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            StringBuffer sb = new StringBuffer("SELECT * FROM `sys_user_role` WHERE `user_id`=?");
            List<String> roles = this.jdbcTemplate.queryForList(sb.toString(), new Object[]{user.getId()}, String.class);
            //用户的角色集合
            info.addRoles(roles);
            sb.setLength(0);
            sb.append("SELECT * FROM `sys_role_auth` WHERE `role_id` IN (");
            sb.append(StringUtils.join(roles.iterator(), ","));
            sb.append(")");
            List<String> auths = this.jdbcTemplate.queryForList(sb.toString(), String.class);
            //用户的权限集合
            info.addStringPermissions(auths);

            return info;
        }
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }

    /**
     * 登录验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        logger.info("##################执行Shiro登录验证##################");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String name = token.getUsername();
        String password = String.valueOf(token.getPassword());
        // 从数据库获取对应用户名密码的用户
        StringBuffer sb = new StringBuffer("SELECT * FROM `sys_user` WHERE `username`=?");
        SysUser user = null;
        try {
            RowMapper<SysUser> rm = BeanPropertyRowMapper.newInstance(SysUser.class);
            user = this.jdbcTemplate.queryForObject(sb.toString(), new Object[]{name}, rm);
            if ("0".equals(user.getEnabled())) {
                //如果用户为禁用。那么就抛出<code>DisabledAccountException</code>
                throw new DisabledAccountException("此帐号已经设置为禁止登录！");
            }
        } catch (DataAccessException e) {
            throw new UnknownAccountException("帐号不存在！");
        }
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

    /**
     * 重写方法,清除当前用户的的 授权缓存
     */
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 重写方法，清除当前用户的 认证缓存
     */
    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    /**
     * 清除当前用户的 身份 和 权限 缓存信息
     */
    public void clearCurrentUserCachedInfo(PrincipalCollection principals) {
        clearCachedAuthorizationInfo(principals);
        clearCachedAuthenticationInfo(principals);
    }

    /**
     * 自定义方法：清除所有 授权缓存
     */
    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    /**
     * 自定义方法：清除所有 认证缓存
     */
    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    /**
     * 自定义方法：清除所有的  认证缓存  和 授权缓存
     */
    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
