package com.fit.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @AUTO 自定义Json工具
 * @FILE JsonUtil.java
 * @DATE 2018-3-23 下午3:07:13
 * @Author AIM
 */
public class ConvertJson {

    private static Logger logger = LoggerFactory.getLogger(ConvertJson.class);

    /**
     * 将时间格式化为含时分秒的字符串
     */
    public static String dateTimeFormatString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 判断字符串是否符合基础类型名称
     */
    private static boolean isContains(String simpleName) {
        return simpleName.equals("Boolean") || simpleName.equals("Integer") || simpleName.equals("Double") || simpleName.equals("Float") || simpleName.equals("Long");
    }

    /**
     * 转为json格式的字符串
     */
    public static String toJson(Object obj) {
        try {
            //如果传入的对象为集合
            if (obj instanceof Collection) {
                //执行collectionTojson方法将集合类型转为json格式
                return collectionTojson(obj);
            }
            //如果获取的对象类型为一个Map集合
            else if (obj instanceof Map) {
                //执行mapTojson方法将Map类型转为json格式
                return mapTojson(obj);
            }
            //如果获取的对象类型为普通对象
            else {
                //执行classTojson方法将普通类型转为json格式
                return classTojson(obj);
            }
        } catch (IllegalAccessException e) {
            logger.debug("错误信息：{}", e);
            logger.info("对象转换Json字符串失败");
        }
        return obj.toString();
    }

    /**
     * 将集合转为json格式的字符串
     */
    private static String collectionTojson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        //定义一个StringBuffer类型的字符串
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        Class<? extends Object> clazz = obj.getClass();
        //获取类中所有的字段
        Field[] declaredFields = clazz.getDeclaredFields();
        //设置可以获得私有字段的value
        Field.setAccessible(declaredFields, true);
        //定义全局变量
        boolean listf = false;
        boolean setf = false;
        Set<Object> set = null;
        List<Object> list = null;
        //遍历获取到的所有字段
        for (Field field : declaredFields) {
            //getDeclaringClass()同getClasses()，但不局限于public修饰，只要是目标类中声明的内部类和接口均可
            String simpleName = clazz.getSimpleName();
            //判断获取到的类型
            if (simpleName.equals("ArrayList") || simpleName.equals("LinkedList")) {
                list = (List<Object>) obj;
                listf = true;
            }
            if (simpleName.equals("HashSet") || simpleName.equals("TreeSet")) {
                set = (Set<Object>) obj;
                setf = true;
            }
        }
        //如果获取的对象类型为一个List集合
        if (listf == true) {
            return listTojson(buffer, list).toString();
        }
        //如果获取的对象类型为一个Set集合
        if (setf == true) {
            buffer = setTojson(set, buffer);
        }
        buffer.append("]");
        return buffer.toString();
    }

    /**
     * 描述 将基本类转为json格式
     */
    private static String classTojson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        //通过反射获取到类
        Class<? extends Object> clazz = obj.getClass();
        //获取类中所有的字段
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer buffer = new StringBuffer();
        buffer.append("[{ ");
        //设置setAccessible方法能获取到类中的私有属性和方法
        Field.setAccessible(fields, true);
        //遍历所有的方法和属性
        for (Field field : fields) {
            if (field.getName().equals("serialVersionUID")) {
                continue;
            }
            Object object = field.get(obj);
            //获取到该属性对应类型名称
            String fieldName = field.getType().getSimpleName();
            //如果该属性的值为空
            if (object == null) {
                //根据类型判断追加的值
                if (isContains(fieldName)) {
                    buffer.append("\"" + field.getName() + "\":0,");
                }
            } else {
                //获取到该属性的值对应的类
                Class fieldclass = object.getClass();
                String simpleName = fieldclass.getSimpleName();
                if (simpleName.equals("String")) {
                    buffer.append("\"" + field.getName() + "\":\"" + field.get(obj) + "\",");
                } else if (isContains(simpleName)) {
                    buffer.append("\"" + field.getName() + "\":" + field.get(obj) + ",");
                } else if (simpleName.equals("Date")) {
                    Date date = (Date) object;
                    buffer.append("\"" + field.getName() + "\":\"" + dateTimeFormatString(date) + "\",");
                } else if (fieldclass.isArray()) {
                    List<Object> list = Arrays.asList(object);
                    buffer.append("\"" + field.getName() + "\":[");
                    //执行listTojson方法将获取到的list转为json格式
                    buffer = listTojson(buffer, list).append(",");
                } else if (simpleName.equals("HashSet") || simpleName.equals("TreeSet")) {
                    //将获取到的值强转为set集合
                    buffer.append("\"" + field.getName() + "\":[");
                    Set<Object> set = (Set<Object>) object;
                    //执行setTojson方法将获取到的set转为json格式
                    buffer = setTojson(set, buffer).append("]");
                } else if (simpleName.equals("HashTable") || simpleName.equals("HashMap")) {
                    buffer.append("\"" + field.getName() + "\":");
                    //执行mapTojson方法将获取到的map对象转为json格式
                    StringBuffer mapbuffer = new StringBuffer(mapTojson(object));
                    mapbuffer.deleteCharAt(0);
                    buffer.append(mapbuffer);
                } else {
                    buffer = beanTojson(object, buffer).append(",");
                }
            }
        }
        buffer = new StringBuffer(buffer.substring(0, buffer.length() - 1));
        buffer.append("}]");
        return buffer.toString();
    }

    /**
     * 将不是基本类型的字段转为json格式
     */
    private static StringBuffer beanTojson(Object obj, StringBuffer buffer) throws IllegalArgumentException, IllegalAccessException {
        Class<? extends Object> clazz = obj.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Field.setAccessible(declaredFields, true);
        buffer.append("\"" + clazz.getSimpleName() + "\":{");
        for (Field field : declaredFields) {
            if (field.getName().equals("serialVersionUID")) {
                continue;
            }
            Object object = field.get(obj);
            String fieldName = field.getType().getSimpleName();
            if (object == null) {
                if (isContains(fieldName)) {
                    buffer.append("\"" + field.getName() + "\":0,");
                }
            } else {
                Class fieldclass = object.getClass();
                String simpleName = fieldclass.getSimpleName();
                if (simpleName.equals("String")) {
                    buffer.append("\"" + field.getName() + "\":\"" + field.get(obj) + "\",");
                } else if (isContains(simpleName)) {
                    buffer.append("\"" + field.getName() + "\":" + field.get(obj) + ",");
                } else if (simpleName.equals("Date")) {
                    Date date = (Date) object;
                    buffer.append("\"" + field.getName() + "\":\"" + dateTimeFormatString(date) + "\",");
                } else if (simpleName.equals("ArrayList") || simpleName.equals("LinkedList")) {
                    List<Object> list = (List<Object>) object;
                    buffer = listTojson(buffer, list);
                } else if (simpleName.equals("HashSet") || simpleName.equals("TreeSet")) {
                    Set<Object> set = (Set<Object>) object;
                    buffer = setTojson(set, buffer);
                } else if (simpleName.equals("HashMap") || simpleName.equals("HashTable")) {
                    buffer.append("\"" + field.getName() + "\":");
                    StringBuffer mapbuffer = new StringBuffer(mapTojson(object));
                    mapbuffer.deleteCharAt(0);
                    buffer.append(mapbuffer);
                } else {
                    buffer = beanTojson(object, buffer).append("}");
                }
            }
        }
        buffer = new StringBuffer("" + buffer.substring(0, buffer.length() - 1) + "");
        buffer.append("}");
        return buffer;
    }

    /**
     * 将map集合转为json格式
     */
    private static String mapTojson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        StringBuffer buffer = new StringBuffer();
        Class<? extends Object> clazz = obj.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Field.setAccessible(declaredFields, true);
        buffer.append("{");
        Map<Object, Object> map = (Map<Object, Object>) obj;
        //通过Map.entrySet使用iterator(迭代器)遍历key和value
        Set<Map.Entry<Object, Object>> set = map.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            //使用Map.Entry接到通过迭代器循环出的set的值
            Map.Entry mapentry = (Map.Entry) iterator.next();
            Object value = mapentry.getValue();
            if (value == null) {
                buffer.append("\"" + mapentry.getKey() + "\":\"\",");
            } else {
                //使用getKey()获取map的键，getValue()获取键对应的值
                String valuename = value.getClass().getSimpleName();
                if (valuename == null) {
                    buffer.append("\"" + mapentry.getKey() + "\":\"" + mapentry.getValue() + "\",");
                } else if (valuename.equals("String")) {
                    buffer.append("\"" + mapentry.getKey() + "\":\"" + mapentry.getValue() + "\",");
                } else if (isContains(valuename)) {
                    buffer.append("\"" + mapentry.getKey() + "\":" + mapentry.getValue() + ",");
                } else if (valuename.equals("Date")) {
                    Date date = (Date) value;
                    buffer.append("\"" + mapentry.getKey() + "\":\"" + dateTimeFormatString(date) + "\",");
                } else if (valuename.equals("ArrayList") || valuename.equals("LinkedList")) {
                    List<Object> list = (List<Object>) value;
                    buffer.append("\"" + mapentry.getKey() + "\":[");
                    buffer = listTojson(buffer, list).append("]");
                } else if (valuename.equals("HashSet") || valuename.equals("TreeSet")) {
                    buffer.append("\"" + mapentry.getKey() + "\":[");
                    Set<Object> sets = (Set<Object>) value;
                    buffer = setTojson(sets, buffer).append("]");
                } else if (valuename.equals("HashMap") || valuename.equals("HashTable")) {
                    buffer.append("\"" + mapentry.getKey() + "\":");
                    StringBuffer mapbuffer = new StringBuffer(mapTojson(value));
                    mapbuffer.deleteCharAt(0);
                    buffer.append(mapbuffer);
                } else {
                    buffer.append("\"" + mapentry.getKey() + "\":");
                    buffer.append("{");
                    Class<? extends Object> class1 = value.getClass();
                    Field[] fields = class1.getDeclaredFields();
                    Field.setAccessible(fields, true);
                    for (Field field : fields) {
                        Object object = field.get(value);
                        String fieldName = field.getType().getSimpleName();
                        if (object == null) {
                            if (fieldName.equals("String")) {
                                buffer.append("\"" + field.getName() + "\":\"\",");
                            } else {
                                buffer.append("\"" + field.getName() + "\":null,");
                            }
                        } else {
                            Class<? extends Object> fieldclass = field.get(value).getClass();
                            String simpleName = fieldclass.getSimpleName();
                            if (simpleName.equals("String")) {
                                buffer.append("\"" + field.getName() + "\":\"" + field.get(value) + "\",");
                            } else if (isContains(simpleName)) {
                                buffer.append("\"" + field.getName() + "\":" + field.get(value) + ",");
                            } else if (simpleName.equals("Date")) {
                                Date date = (Date) object;
                                buffer.append("\"" + field.getName() + "\":\"" + dateTimeFormatString(date) + "\",");
                            } else if (simpleName.equals("ArrayList") || simpleName.equals("LinkedList")) {
                                List<Object> list = (List<Object>) object;
                                buffer.append("\"" + field.getName() + "\":[");
                                StringBuffer append = listTojson(buffer, list).append("]");
                                buffer.append(append);
                            } else if (simpleName.equals("HashSet") || simpleName.equals("TreeSet")) {
                                buffer.append("\"" + field.getName() + "\":[");
                                Set<Object> sets = (Set<Object>) object;
                                buffer = setTojson(sets, buffer).append("]");
                            } else if (simpleName.equals("HashMap") || simpleName.equals("HashTable")) {
                                buffer.append("\"" + field.getName() + "\":");
                                StringBuffer mapbuffer = new StringBuffer(mapTojson(object));
                                mapbuffer.deleteCharAt(0);
                                buffer.append(mapbuffer);
                            } else {
                                buffer = beanTojson(object, buffer).append(",");
                            }
                        }
                    }
                    buffer = new StringBuffer("" + buffer.substring(0, buffer.length() - 1) + "");
                    buffer.append("},");
                }
            }
        }
        buffer = new StringBuffer("" + buffer.substring(0, buffer.length() - 1) + "");
        return buffer.toString() + "}";
    }

    /**
     * 获取类的所有参数(包括父类)
     */
    private static Field[] getAllFields(Class clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        try {
            while (clazz != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
                fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass(); //得到父类,然后赋给自己 }
            }
        } catch (SecurityException e) {
            logger.error("=====JSON Exception=====", e);
        }
        return fieldList.toArray(new Field[0]);
    }

    /**
     * 将list数组转为json格式
     */
    private static StringBuffer listTojson(StringBuffer buffer, List list) throws IllegalArgumentException, IllegalAccessException {
        //遍历传过来的list数组
        for (Object object : list) {
            //判断遍历出的值是否为空
            if (object != null) {
                Class fieldclass = object.getClass();
                String simpleName = fieldclass.getSimpleName();
                if (simpleName.equals("String")) {
                    buffer.append("\"" + object.toString() + "\",");
                } else if (isContains(simpleName)) {
                    buffer.append("" + object.toString() + ",");
                } else if (simpleName.equals("Date")) {
                    Date date = (Date) object;
                    buffer.append("" + dateTimeFormatString(date) + ",");
                } else {
                    buffer.append("{ ");
                    Field[] fields = getAllFields(object.getClass());
                    //遍历对象中的所有字段获取字段值和字段名称拼成json字符串
                    for (Field field : fields) {
                        if (field.getName().equals("serialVersionUID")) {
                            continue;
                        }
                        field.setAccessible(true);
                        Object fieldobj = field.get(object);
                        String fieldName = field.getType().getSimpleName();
                        if (fieldobj != null) {
                            String fsimpleName = fieldobj.getClass().getSimpleName();
                            if (fsimpleName.equals("String")) {
                                buffer.append("\"" + field.getName() + "\":\"" + field.get(object) + "\",");
                            } else if (fsimpleName.equals("Date")) {
                                Date date = (Date) fieldobj;
                                buffer.append("\"" + field.getName() + "\":\"" + dateTimeFormatString(date) + "\",");
                            } else if (isContains(fsimpleName)) {
                                buffer.append("\"" + field.getName() + "\":\"" + field.get(object) + "\",");
                            } else {
                                buffer = beanTojson(fieldobj, buffer).append(",");
                            }
                        } else {
                            if (isContains(fieldName)) {
                                buffer.append("\"" + field.getName() + "\":0,");
                            }
                        }
                    }
                    buffer = new StringBuffer("" + buffer.substring(0, buffer.length() - 1) + "");
                    buffer.append("},");
                }
            }
        }
        buffer = new StringBuffer("" + buffer.substring(0, buffer.length() - 1) + "");
        buffer.append("]");
        return buffer;
    }

    /**
     * 将set数组转为json格式
     *
     * @param set
     * @param buffer
     */
    private static StringBuffer setTojson(Set set, StringBuffer buffer) throws IllegalArgumentException, IllegalAccessException {
        for (Object object : set) {
            if (object == null) {
                buffer.append("" + "null" + ",");
            } else {
                Class fieldclass = object.getClass();
                //判断集合中的值是否为java基本类型
                String simpleName = fieldclass.getSimpleName();
                if (simpleName.equals("String")) {
                    buffer.append("\"" + object.toString() + "\",");
                } else if (isContains(simpleName)) {
                    buffer.append("" + object.toString() + ",");
                } else if (simpleName.equals("Date")) {
                    Date date = (Date) object;
                    buffer.append("" + dateTimeFormatString(date) + ",");
                } else {
                    Class<? extends Object> class2 = object.getClass();
                    Field[] fields = class2.getDeclaredFields();
                    Field.setAccessible(fields, true);
                    buffer.append("{ ");
                    //遍历对象中的所有字段获取字段值和字段名称拼成json字符串
                    for (Field field : fields) {
                        if (field.getName().equals("serialVersionUID")) {
                            continue;
                        }
                        Object fieldobj = field.get(object);
                        String fieldName = field.getType().getSimpleName();
                        if (object == null) {
                            if (fieldName.equals("String")) {
                                buffer.append("\"" + field.getName() + "\":\"\",");
                            } else {
                                buffer.append("\"" + field.getName() + "\":null,");
                            }
                        } else {
                            String fsimpleName = fieldobj.getClass().getSimpleName();
                            if (fsimpleName.equals("String")) {
                                buffer.append("\"" + field.getName() + "\":\"" + field.get(object) + "\",");
                            } else if (isContains(fsimpleName)) {
                                buffer.append("\"" + field.getName() + "\":" + field.get(object) + ",");
                            } else if (fsimpleName.equals("Date")) {
                                Date date = (Date) object;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String simdate = simpleDateFormat.format(date);
                                buffer.append("\"" + field.getName() + "\":" + simdate + ",");
                            } else {
                                buffer = beanTojson(fieldobj, buffer).append(",");
                            }
                        }
                    }
                    buffer = new StringBuffer("" + buffer.substring(0, buffer.length() - 1) + "");
                    buffer.append("},");
                }
            }
        }
        buffer = new StringBuffer("" + buffer.substring(0, buffer.length() - 1) + "");
        return buffer;
    }
}
