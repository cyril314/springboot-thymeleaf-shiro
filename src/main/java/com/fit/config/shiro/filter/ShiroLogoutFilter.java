package com.fit.config.shiro.filter;

import com.fit.config.shiro.core.ShiroRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @AUTO 登出拦截器
 * @Author AIM
 * @DATE 2019/3/21
 */
public class ShiroLogoutFilter extends LogoutFilter {

    private static final Logger logger = LoggerFactory.getLogger(ShiroLogoutFilter.class);

    /**
     * 自定义登出,登出之后,清理当前用户redis部分缓存信息
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        logger.info("##################执行Shiro用户登出##################");
        //登出操作 清除缓存  subject.logout() 可以自动清理缓存信息, 这些代码是可以省略的  这里只是做个笔记 表示这种方式也可以清除
        Subject subject = getSubject(request, response);
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        ShiroRealm shiroRealm = (ShiroRealm) securityManager.getRealms().iterator().next();
        PrincipalCollection principals = subject.getPrincipals();
        shiroRealm.clearCache(principals);
        //登出
        subject.logout();
        //获取登出后重定向到的地址
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String redirectUrl = servletRequest.getRequestURI();
        if (redirectUrl.indexOf("admin") != -1) {
            //未登录而访问后台受控资源时，跳转到后台登录页面
            redirectUrl = "admin/login";
        }
        logger.info("##################用户登出跳转的路径：###### -->" + redirectUrl);
        //重定向
        issueRedirect(request, response, redirectUrl);
        return false;
    }

}
