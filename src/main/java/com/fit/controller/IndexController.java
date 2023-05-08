package com.fit.controller;

import com.fit.config.shiro.ShiroUtil;
import com.fit.controller.admin.UserController;
import com.fit.model.SysMenus;
import com.fit.model.SysUser;
import com.fit.service.SysMenuService;
import com.fit.common.bean.AjaxResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @AUTO 首页管理器
 * @Author AIM
 * @DATE 2019/3/19
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private SysMenuService menuService;

    /**
     * 后台管理系统首页
     */
    @GetMapping({"/admin", "/admin/", "/admin/index", "/admin/index.html"})
    public String adminIndex(Model model) {
        SysUser user = ShiroUtil.getUser();
        List<SysMenus> menuList = menuService.getMenuList(user);
        model.addAttribute("menus", menuList);
        model.addAttribute("username", user.getUsername());
        return "admin/index";
    }

    @GetMapping("/admin/welcome.html")
    public String adminWelcome() {
        return "admin/welcome";
    }

    @GetMapping({"/admin/login", "/admin/login.html"})
    public String adminLogin() {
        return "login";
    }

    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "login";
    }

    //    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        String targetUrl = "login";
        String url = request.getRequestURI();
        if (url.indexOf("admin") != -1) {
            //未登录而访问后台受控资源时，跳转到后台登录页面
            targetUrl = "admin/login";
        }
        return targetUrl;
    }

    //登陆验证，这里方便url测试，正式上线需要使用POST方式提交
    @RequestMapping(value = "/ajaxLogin", method = RequestMethod.GET)
    public String index(String username, String password, boolean rememberMe) {
        if (username != null && password != null) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe, "login");
            Subject currentUser = SecurityUtils.getSubject();
            logger.info("对用户[" + username + "]进行登录验证..验证开始");
            try {
                currentUser.login(token);
                //验证是否登录成功
                if (currentUser.isAuthenticated()) {
                    logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
                    System.out.println("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
                    return "redirect:/";
                } else {
                    token.clear();
                    System.out.println("用户[" + username + "]登录认证失败,重新登陆");
                    return "redirect:/login";
                }
            } catch (UnknownAccountException uae) {
                logger.info("对用户[" + username + "]进行登录验证..验证失败-username wasn't in the system");
            } catch (IncorrectCredentialsException ice) {
                logger.info("对用户[" + username + "]进行登录验证..验证失败-password didn't match");
            } catch (LockedAccountException lae) {
                logger.info("对用户[" + username + "]进行登录验证..验证失败-account is locked in the system");
            } catch (AuthenticationException ae) {
                logger.error(ae.getMessage());
            }
        }
        return "login";
    }

    /**
     * ajax登录请求接口方式登陆
     */
    @RequestMapping(value = "/ajaxLogin", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult submitLogin(ServletRequest request, ServletResponse response, String username, String password, boolean rememberMe) {
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
            SecurityUtils.getSubject().login(token);
            return AjaxResult.success(200, "登录成功");
        } catch (Exception e) {
            return AjaxResult.error(500, e.getMessage());
        }
    }
}
