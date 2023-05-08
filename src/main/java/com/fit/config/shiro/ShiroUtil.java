package com.fit.config.shiro;

import com.fit.model.SysUser;
import com.fit.common.utils.ConverterUtils;
import org.apache.shiro.SecurityUtils;

import java.util.Map;

/**
 * @AUTO 权限工具类
 * @Author AIM
 * @DATE 2019/3/21
 */
public class ShiroUtil {

    /**
     * 获取用户信息
     */
    public static SysUser getUser() {
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            if (principal != null) {
                Map<String, Object> map = ConverterUtils.convertBean2Map(principal);
                SysUser user = ConverterUtils.converterMap2Bean(map, SysUser.class);
                return user;
            }
        } catch (Exception e) {
        }
        return null;
    }

}
