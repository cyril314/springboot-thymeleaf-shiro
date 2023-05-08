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
public class SysResources extends BaseEntity<SysResources> {
    private static final long serialVersionUID = 1L;

    /** 资源名称 (无默认值) */
    private String name;

    /** 资源类型(M:目录 C:菜单 A:按钮) (无默认值) */
    private String type;

    /** 资源图标 (无默认值) */
    private String icon;

    /** 资源优先权 (无默认值) */
    private Integer priority;

    /** 资源链接 (无默认值) */
    private String url;

    /** 资源描述 (无默认值) */
    private String description;

    /** 是否被禁用 0禁用1正常  (默认值为: 0) */
    private Boolean enabled;

    /** 是否是超级权限 0非1是  (默认值为: 0) */
    private Integer isys;

    /** 父级ID  (默认值为: 0) */
    private Integer pid;

    /** 父级名称 (无默认值) */
    private String pname;
}