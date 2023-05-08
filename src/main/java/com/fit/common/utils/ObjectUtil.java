package com.fit.common.utils;

import java.util.*;

/**
 * @AUTO 对象工具类
 * @DATE 2018-11-5 下午6:12:48
 * @Author AIM
 */
public class ObjectUtil {

    /**
     * 判断对象是不是空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof String) {
            if (!"".equals(obj))
                return false;
        } else if (obj instanceof StringBuffer) {
            return isEmpty(obj.toString());
        } else if (obj instanceof Map) {
            if (!isEmpty(((Map<?, ?>) obj).values()))
                return false;
        } else if (obj instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration<?>) obj;
            while (enumeration.hasMoreElements()) {
                if (!isEmpty(enumeration.nextElement()))
                    return false;
            }
        } else if (obj instanceof Iterable) {
            if (obj instanceof List && obj instanceof RandomAccess) {
                List<?> objList = (List<?>) obj;
                for (int i = 0; i < objList.size(); i++) {
                    if (!isEmpty(objList.get(i)))
                        return false;
                }
            } else if (!isEmpty(((Iterable<?>) obj).iterator()))
                return false;
        } else if (obj instanceof Iterator) {
            Iterator<?> it = (Iterator<?>) obj;
            while (it.hasNext()) {
                if (!isEmpty(it.next()))
                    return false;
            }
        } else if (obj instanceof Object[]) {
            Object[] objs = (Object[]) obj;
            for (Object elem : objs) {
                if (!isEmpty(elem))
                    return false;
            }
        } else if (obj instanceof int[]) {
            for (Object elem : (int[]) obj) {
                if (!isEmpty(elem))
                    return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断某字符串是否为空或长度为0或由空白符构成
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 判断对象或对象数组中每一个对象是否为空: 对象为null，字符序列长度为0，集合类、Map为empty
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null || "".equals(obj))
            return true;

        if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;

        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map<?, ?>) obj).isEmpty();

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    public static boolean isNullOrEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }
}
