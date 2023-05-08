package com.fit.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTO Jackson工具类
 * @Author AIM
 * @DATE 2019/4/26
 */
public class JacksonUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private JacksonUtils() {
    }

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    /**
     * javaBean、列表数组转换为json字符串
     */
    public static String obj2json(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * javaBean、列表数组转换为json字符串,忽略空值
     */
    public static String obj2jsonIgnoreNull(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json 转JavaBean
     */
    public static <T> T json2pojo(String jsonString, Class<T> clazz) {
        try {
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json字符串转换为map
     */
    public static <T> Map<String, Object> json2map(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.readValue(jsonString, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json字符串转换为map
     */
    public static <T> Map<String, T> json2map(String jsonString, Class<T> clazz) {
        Map<String, T> result = new HashMap<String, T>();
        try {
            Map<String, T> map = objectMapper.readValue(jsonString, new TypeReference<Map<String, T>>() {
            });
            for (Map.Entry<String, T> entry : map.entrySet()) {
                result.put(entry.getKey(), map2pojo((Map) entry.getValue(), clazz));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 深度转换json成map
     */
    public static Map<String, Object> json2mapDeeply(String json) {
        return json2MapRecursion(json, objectMapper);
    }

    /**
     * 把json解析成list，如果list内部的元素存在jsonString，继续解析
     *
     * @param json
     * @param mapper 解析工具
     */
    private static List<Object> json2ListRecursion(String json, ObjectMapper mapper) throws Exception {
        if (json == null) {
            return null;
        }

        List<Object> list = mapper.readValue(json, List.class);

        for (Object obj : list) {
            if (obj != null && obj instanceof String) {
                String str = (String) obj;
                if (str.startsWith("[")) {
                    json2ListRecursion(str, mapper);
                } else if (obj.toString().startsWith("{")) {
                    json2MapRecursion(str, mapper);
                }
            }
        }

        return list;
    }

    /**
     * 把json解析成map，如果map内部的value存在jsonString，继续解析
     */
    private static Map<String, Object> json2MapRecursion(String json, ObjectMapper mapper) {
        if (json == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = mapper.readValue(json, Map.class);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object obj = entry.getValue();
                if (obj != null && obj instanceof String) {
                    String str = ((String) obj);

                    if (str.startsWith("[")) {
                        List<?> list = json2ListRecursion(str, mapper);
                        map.put(entry.getKey(), list);
                    } else if (str.startsWith("{")) {
                        Map<String, Object> mapRecursion = json2MapRecursion(str, mapper);
                        map.put(entry.getKey(), mapRecursion);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 与javaBean json数组字符串转换为列表
     */
    public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz) throws Exception {
        JavaType javaType = getCollectionType(ArrayList.class, clazz);
        List<T> lst = (List<T>) objectMapper.readValue(jsonArrayStr, javaType);
        return lst;
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * map  转JavaBean
     */
    public static <T> T map2pojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * map  转JavaBean
     */
    public static <T> T obj2pojo(Object obj, Class<T> clazz) {
        return objectMapper.convertValue(obj, clazz);
    }
}
