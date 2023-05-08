package com.fit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2019/3/5
 */
@Data
@NoArgsConstructor //无参数的构造方法
@AllArgsConstructor //包含所有变量构造方法
public class SysMenus extends SysResources {

    private List<SysMenus> children = new ArrayList<SysMenus>();

}
