package com.fit.model;

import com.fit.common.base.BaseEntity;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2019/03/21
 */
@Data
@Builder
@NoArgsConstructor //无参数的构造方法
@AllArgsConstructor //包含所有变量构造方法
public class SysRole extends BaseEntity<SysRole> {
    private static final long serialVersionUID = 1L;

    /** 角色名字 (无默认值) */
    private String roleName;

    /** 角色说明 (无默认值) */
    private String roleDesc;

    /** 是否被禁用 0禁用1正常  (默认值为: 0) */
    private Boolean enabled;

    /** 是否是超级权限 0非1是  (默认值为: 0) */
    private Integer isys;
}