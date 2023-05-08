package com.fit.common.utils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @version v1.0
 * @AUTO JSON工具类
 * @DATE 2018-12-26 下午6:05:40
 * @Author AIM
 */
public class JsonUtils {

    @SuppressWarnings("unchecked")
    public static String toJson(Object o) {
        try {
            if (o == null)
                return "null";
            else if (o instanceof String)
                return string2Json((String) o);
            else if (o instanceof Boolean)
                return boolean2Json((Boolean) o);
            else if (o instanceof Number)
                return number2Json((Number) o);
            else if (o instanceof Map)
                return map2Json((Map<String, Object>) o);
            else if (o instanceof List)
                return list2Json((List<?>) o);
            else if (o instanceof Set)
                return set2json((Set<?>) o);
            else if (o instanceof Object[])
                return array2Json((Object[]) o);
            else if (o instanceof int[])
                return intArray2Json((int[]) o);
            else if (o instanceof boolean[])
                return booleanArray2Json((boolean[]) o);
            else if (o instanceof long[])
                return longArray2Json((long[]) o);
            else if (o instanceof float[])
                return floatArray2Json((float[]) o);
            else if (o instanceof double[])
                return doubleArray2Json((double[]) o);
            else if (o instanceof short[])
                return shortArray2Json((short[]) o);
            else if (o instanceof byte[])
                return byteArray2Json((byte[]) o);
            else if (o.getClass().isArray())
                return list2Json((List<?>) o);
            else
                return bean2json(o);
        } catch (Exception e) {
            throw new RuntimeException("Unsupported type: " + o.getClass().getName());
        }
    }

    static String array2Json(Object[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (Object o : array) {
            sb.append(toJson(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String intArray2Json(int[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (int o : array) {
            sb.append(Integer.toString(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String longArray2Json(long[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (long o : array) {
            sb.append(Long.toString(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String booleanArray2Json(boolean[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (boolean o : array) {
            sb.append(Boolean.toString(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String floatArray2Json(float[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (float o : array) {
            sb.append(Float.toString(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String doubleArray2Json(double[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (double o : array) {
            sb.append(Double.toString(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String shortArray2Json(short[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (short o : array) {
            sb.append(Short.toString(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String byteArray2Json(byte[] array) {
        if (array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        for (byte o : array) {
            sb.append(Byte.toString(o));
            sb.append(',');
        }
        // set last ',' to ']':
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    static String map2Json(Map<String, Object> map) {
        if (map.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder(map.size() << 4);
        sb.append('{');
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Object value = map.get(key);
            sb.append('\"');
            sb.append(key);
            sb.append('\"');
            sb.append(':');
            sb.append(toJson(value));
            sb.append(',');
        }
        // set last ',' to '}':
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }

    static String list2Json(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                json.append(toJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    static String boolean2Json(Boolean bool) {
        return bool.toString();
    }

    static String number2Json(Number number) {
        return number.toString();
    }

    static String string2Json(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 20);
        sb.append('\"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        sb.append('\"');
        return sb.toString();
    }

    public static String set2json(Set<?> set) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (set != null && set.size() > 0) {
            Iterator<?> it = set.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                json.append(toJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String bean2json(Object bean) throws Exception {
        StringBuilder json = new StringBuilder();
        json.append("{");
        PropertyDescriptor[] props = null;
        props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
        if (props != null) {
            for (int i = 0; i < props.length; i++) {
                try {
                    String name = toJson(props[i].getName());
                    String value = toJson(props[i].getReadMethod().invoke(bean));
                    json.append(name).append(":").append(value);
                    json.append(",");
                } catch (Exception e) {
                }
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }
}