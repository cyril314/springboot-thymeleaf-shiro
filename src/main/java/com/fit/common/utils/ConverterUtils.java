package com.fit.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 数据类型转换工具类
 * @DATE 2018-11-5 下午4:28:33
 * @Author AIM
 */
public class ConverterUtils {

	/**
	 * URL解码
	 */
	public static String decode(String s) {
		String ret = s;
		try {
			ret = URLDecoder.decode(s.trim(), "UTF-8");
		} catch (Exception localException) {
		}
		return ret;
	}

	/**
	 * URL编码
	 */
	public static String encode(String s) {
		String ret = s;
		try {
			ret = URLEncoder.encode(s.trim(), "UTF-8");
		} catch (Exception localException) {
		}
		return ret;
	}

	/**
	 * 请求参数URL转MAP对象
	 */
	public static Map<String, String> reqStr2Map(String s) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String[] splitByte = s.split("&");
			for (String str : splitByte) {
				map.put(str.substring(0, str.indexOf("=")), str.substring(str.indexOf("=") + 1));
			}
		} catch (Exception e) {
			throw new RuntimeException("系统异常");
		}
		return map;
	}

	/**
	 * 将MAP转换成URL
	 */
	public static String reqMap2Str(Map<String, Object> map) {
		try {
			if (map == null) {
				return "";
			}
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				sb.append(entry.getKey() + "=" + entry.getValue());
				sb.append("&");
			}
			if (sb.toString().endsWith("&")) {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("系统异常");
		}
	}

	/**
	 * <将obj转换为string，如果obj为null则返回defaultVal>
	 * 
	 * @param obj
	 *            需要转换为string的对象
	 * @param defaultVal
	 *            默认值
	 */
	public static String toString(Object obj, String defaultVal) {
		return (obj != null) ? obj.toString().trim() : defaultVal;
	}

	/**
	 * <将obj转换为string，默认为空>
	 * 
	 * @param obj
	 *            需要转换为string的对象
	 */
	public static String toString(Object obj) {
		return toString(obj, "");
	}

	/**
	 * <将对象转换为int>
	 * 
	 * @param obj
	 *            需要转换为int的对象
	 * @param defaultVal
	 *            默认值
	 */
	public static Integer toInt(Object obj, Integer defaultVal) {
		try {
			return (obj != null) ? Integer.parseInt(toString(obj, "0")) : defaultVal;
		} catch (Exception e) {
		}
		return defaultVal;
	}

	/**
	 * <将对象转换为int>
	 * 
	 * @param obj
	 *            需要转换为int的对象
	 */
	public static Integer toInt(Object obj) {
		return toInt(obj, 0);
	}

	/**
	 * <将对象转换为Integer>
	 * 
	 * @param obj
	 *            需要转换为Integer的对象
	 * @return obj转换成的Integer值
	 */
	public static Integer toInteger(Object obj) {
		return toInt(obj, null);
	}

	/**
	 * <将对象转换为int>
	 * 
	 * @param obj
	 *            需要转换为int的对象
	 * @param defaultVal
	 *            默认值
	 */
	public static Float toFloat(Object obj, float defaultVal) {
		return (obj != null) ? Float.parseFloat(toString(obj, "0")) : defaultVal;
	}

	/**
	 * <将对象转换为Float>
	 * 
	 * @param obj
	 *            需要转换为Float的对象
	 */
	public static Float toFloat(Object obj) {
		return toFloat(obj, 0);
	}

	/**
	 * <将obj转换为long>
	 * 
	 * @param obj
	 *            需要转换的对象
	 * @param defaultVal
	 *            默认值
	 */
	public static Long toLong(Object obj, long defaultVal) {
		return (obj != null) ? Long.parseLong(toString(obj)) : defaultVal;
	}

	/**
	 * <将obj转换为long>
	 * 
	 * @param obj
	 *            需要转换的对象
	 */
	public static Long toLong(Object obj) {
		return toLong(obj, 0l);
	}

	/**
	 * 将object转换为double类型，如果出错则返回 defaultVal
	 * 
	 * @param obj
	 *            需要转换的对象
	 * @param defaultVal
	 *            默认值
	 */
	public static Double toDouble(Object obj, Double defaultVal) {
		try {
			return Double.parseDouble(obj.toString());
		} catch (Exception e) {
			return defaultVal;
		}
	}

	/**
	 * 将object转换为double类型，如果出错则返回 0d
	 * 
	 * @param obj
	 *            需要转换的对象
	 */
	public static double toDouble(Object obj) {
		return toDouble(obj, 0d);
	}

	public static Map<String, Object> Obj2Map(Object obj) throws Exception {
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
	 * @param list
	 *            需要转换的list
	 */
	public static List<Map<String, Object>> converterForMapList(List<Object> list) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Object tempObj : list) {
			result.add(Obj2Map(tempObj));
		}
		return result;
	}

	/**
	 * 将 JavaBean对象转化为 Map
	 * 
	 * @param bean
	 *            要转化的类型
	 * @return Map对象
	 */
	public static Map<String, Object> convertBean2Map(Object bean) throws Exception {
		if (bean == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(bean);
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("JavaBean对象转化为 Map异常");
		}

		return map;
	}

	/**
	 * Map转Bean
	 * 
	 * @param map
	 * @param clz
	 */
	public static <T> T converterMap2Bean(Map<?, ?> map, Class<T> clz) {
		T obj = null;
		try {
			obj = clz.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(clz);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (map.containsKey(key)) {
					Object value = map.get(key);
					if (null != value) {
						String v = value.toString();// 值
						// property对应的setter方法
						Method setter = property.getWriteMethod();
						Class<?>[] clazz = setter.getParameterTypes();
						String type = clazz[0].getName();
						if ("java.lang.Byte".equals(type) || "byte".equals(type)) {
							setter.invoke(obj, Byte.parseByte(v));
						} else if ("java.lang.Short".equals(type) || "short".equals(type)) {
							setter.invoke(obj, Short.parseShort(v));
						} else if ("java.lang.Integer".equals(type) || "int".equals(type)) {
							setter.invoke(obj, Integer.parseInt(v));
						} else if ("java.lang.Long".equals(type) || "long".equals(type)) {
							setter.invoke(obj, Long.parseLong(v));
						} else if ("java.lang.Float".equals(type) || "float".equals(type)) {
							setter.invoke(obj, Float.parseFloat(v));
						} else if ("java.lang.Double".equals(type) || "double".equals(type)) {
							setter.invoke(obj, Double.parseDouble(v));
						} else if ("java.lang.String".equals(type)) {
							setter.invoke(obj, v.toString());
						} else if ("java.lang.Character".equals(type) || "char".equals(type)) {
							setter.invoke(obj, (Character) value);
						} else if ("java.util.Date".equals(type)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							setter.invoke(obj, sdf.parse(v));
						} else if ("java.lang.Date".equals(type)) {
							setter.invoke(obj, new Date(((java.sql.Date) value).getTime()));
						} else if ("java.lang.Timer".equals(type)) {
							setter.invoke(obj, new Time(((Time) value).getTime()));
						} else if ("java.sql.Timestamp".equals(type)) {
							setter.invoke(obj, (java.sql.Timestamp) value);
						} else {
							setter.invoke(obj, value);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("map转{" + clz.getName() + "}异常");
		}
		return obj;
	}

	/**
	 * 将 List<JavaBean>对象转化为List<Map>
	 */
	public static List<Map<String, Object>> convertListBean2ListMap(List<?> beanList) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0, n = beanList.size(); i < n; i++) {
				Object bean = beanList.get(i);
				Map<String, Object> map = convertBean2Map(bean);
				mapList.add(map);
			}
		} catch (Exception e) {
			throw new RuntimeException("List<JavaBean>对象转化为List<Map>异常");
		}
		return mapList;
	}

	/**
	 * 任意类型转换成Map
	 */
	public static Map<String, Object> objectToMap(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				field.setAccessible(true);
				if (field.get(obj) != null) {
					map.put(field.getName(), field.get(obj));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("任意类型转换成Map异常");
		}

		return map;
	}

	/**
	 * 对象转换为字节
	 */
	public static byte[] convertToByte(Object obj) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			// 取内存中保存的数据
			return bo.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("任意类型转换成字节异常");
		}
	}

	/**
	 * 对象转换为字符串
	 * 
	 * @param obj
	 *            转换对象
	 * @param charset
	 *            字符集
	 */
	public static String convertToString(Object obj, String charset) {
		try {
			return new String(convertToByte(obj), charset);
		} catch (IOException e) {
			throw new RuntimeException("任意类型转换成字符串异常");
		}
	}

	/**
	 * 字节转换为对象
	 * 
	 * @param bytes
	 *            字节数组
	 */
	public static Object convertToByte(byte[] bytes) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(in);
			in.close();
			ois.close();
			Object obj = ois.readObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("字节转换为对象异常");
		}
	}
}
