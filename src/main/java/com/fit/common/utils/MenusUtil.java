package com.fit.common.utils;

import com.fit.model.SysMenus;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @AUTO 递归目录工具类
 * @Author AIM
 * @DATE 2019/3/11
 */
public class MenusUtil {

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     */
    public static List<SysMenus> getChildPerms(List<SysMenus> list, int parentId) {
        List<SysMenus> returnList = new LinkedList<SysMenus>();
        for (int i = 0; i < list.size(); i++) {
            SysMenus t = list.get(i);
            if (t.getPid() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private static void recursionFn(List<SysMenus> list, SysMenus t) {
        // 得到子节点列表
        List<SysMenus> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenus tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<SysMenus> it = childList.iterator();
                while (it.hasNext()) {
                    SysMenus n = (SysMenus) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private static List<SysMenus> getChildList(List<SysMenus> list, SysMenus t) {
        List<SysMenus> tlist = new LinkedList<SysMenus>();
        Iterator<SysMenus> it = list.iterator();
        while (it.hasNext()) {
            SysMenus n = (SysMenus) it.next();
            if (n.getPid().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private static boolean hasChild(List<SysMenus> list, SysMenus t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

}
