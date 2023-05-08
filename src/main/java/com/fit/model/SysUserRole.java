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
public class SysUserRole extends BaseEntity<SysUserRole> {
    private static final long serialVersionUID = 1L;

    /** 角色ID (无默认值) */
    private Long roleId;

    /** 用户ID (无默认值) */
    private Long userId;
}