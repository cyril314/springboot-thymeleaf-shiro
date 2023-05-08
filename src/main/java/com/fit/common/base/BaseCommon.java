package com.fit.common.base;

import com.fit.common.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2018/5/21
 */
public class BaseCommon {

    private static Logger logger = LoggerFactory.getLogger(BaseCommon.class);

    /**
     * 判断对象是不是不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !ObjectUtil.isEmpty(obj);
    }

    /**
     * 判断对象或对象数组中每一个对象是否不为空
     */
    public static boolean isNotNullOrEmpty(Object obj) {
        return !ObjectUtil.isNullOrEmpty(obj);
    }

    /**
     * <将Object转换为Map<String, Object>>
     *
     * @param obj 需要转换的对象
     */
    protected Map<String, Object> Obj2Map(Object obj) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

    /**
     * <将List<Object>转换为List<Map<String, Object>>>
     *
     * @param list 需要转换的list
     */
    protected List<Map<String, Object>> converterForMapList(List<Object> list) throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Object tempObj : list) {
            result.add(Obj2Map(tempObj));
        }
        return result;
    }

    /**
     * 对象转换为字符串
     *
     * @param obj     转换对象
     * @param charset 字符集
     */
    protected String convertToString(Object obj, String charset) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            byte[] data = bo.toByteArray(); // 取内存中保存的数据
            String str = new String(data, charset);
            bo.close();
            oo.close();
            return str;
        } catch (IOException e) {
            throw new RuntimeException("任意类型转换成字符串异常");
        }
    }

}
