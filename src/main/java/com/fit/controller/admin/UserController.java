package com.fit.controller.admin;

import com.fit.common.bean.Page;
import com.fit.model.SysRole;
import com.fit.model.SysUser;
import com.fit.service.SysRoleService;
import com.fit.service.SysUserService;
import com.fit.common.base.BaseController;
import com.fit.common.base.PatternAndView;
import com.fit.common.bean.AjaxResult;
import com.fit.common.utils.BeanUtils;
import com.fit.common.utils.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @AUTO 用户控制器
 * @Author AIM
 * @DATE 2019/3/5
 */
@Controller
@RequestMapping("/admin/user")
public class UserController extends BaseController {

    private String prefix = "/admin/sys_user";

    @Autowired
    private SysUserService userService;
    @Autowired
    private SysRoleService roleService;

    @GetMapping("/list")
    public ModelAndView view() {
        ModelAndView mv = new PatternAndView(prefix + "/list");
        List<SysRole> list = this.roleService.findList();
        mv.addObject("roles", list);
        return mv;
    }

    @PostMapping("/list")
    @ResponseBody
    public Object list(HttpServletResponse response, Integer page, Integer limit) {
        List<SysUser> list = userService.findList();
        Page<SysUser> pageInfo = new Page<SysUser>(list);
        return AjaxResult.success((int) pageInfo.getTotalPage(), list);
    }

    /**
     * @AUTO 用户弹出页面
     * @DATE 2019/3/6
     */
    @GetMapping("/edit")
    public ModelAndView edit(String id) {
        ModelAndView mv = new PatternAndView(prefix + "/edit");
        if (isNotEmpty(id)) {
            SysUser sysUser = userService.get(ConverterUtils.toLong(id));
            mv.addObject("user", sysUser);
        }
        return mv;
    }

    @PostMapping("/save")
    @ResponseBody
    public Object save(SysUser user) {
        SysUser sysUser = this.userService.get(user);
        if (null == sysUser) {
            this.userService.save(user);
        } else {
            BeanUtils.copyProperties(user, sysUser);
            this.userService.update(sysUser);
        }
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public Object del(String ids) {
        if (isNotEmpty(ids)) {
            this.userService.batchDelete(ids.split(","));
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * 根据用户ID获取已有的角色
     */
    @GetMapping("/role")
    public ModelAndView getRole(String id) {
        ModelAndView mv = new PatternAndView(prefix + "/role");
        if (isNotEmpty(id)) {
            String roleId = roleService.getUserByUserId(id);
            mv.addObject("roleid", roleId);
        }
        List<SysRole> list = roleService.findList();
        mv.addObject("roles", list);
        mv.addObject("userId", id);
        return mv;
    }

    @PostMapping("/setRole")
    @ResponseBody
    public Object setRole(String setRole, String userId) {
        this.roleService.setUserRole(setRole, userId);
        return AjaxResult.success();
    }
}
