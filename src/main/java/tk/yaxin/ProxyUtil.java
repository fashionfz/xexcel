/**
 * Project Name:topx
 * @Title: ProxyUtil.java
 * @Package com.helome.monitor.utils
 * @Description: TODO
 * Copyright: Copyright (c) 2014 All Rights Reserved. 
 * Company:helome.com
 * 
 * @author bin.deng@helome.com
 * @date 2014年3月28日 上午10:03:20
 * @version V1.0
 */
package tk.yaxin;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;




/**
 * @ClassName: ProxyUtil
 * @Description: 动态代理工具类，反射获取类的相关字段，
 * 以及反射调用类的setter或getter方法
 * @author bin.deng
 * @date 2014年3月28日 上午10:03:20
 *
 */
public class ProxyUtil {


	
	public static String toString(Object value, Class<?> type) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		if (type == java.util.Date.class) {
			return df.format((java.util.Date)value);
		} else if (type == java.sql.Timestamp.class) {
			return df.format((java.sql.Timestamp)value);
        } else
        	return String.valueOf(value);
	}
	
	/**
	 * 
	 * @param obj
	 * @param att
	 * @param value
	 * @param type
	 * @throws ParseException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static void setter(Object obj, String att, Object value, Class<?> type) throws ParseException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		if (type == String.class)
			setFieldValue(obj, att, (String)value);
		else if (type == Integer.class || type == int.class)
			setFieldValue(obj, att, toInteger(value));
		else if (type == double.class || type == Double.class)
			setFieldValue(obj, att, toDouble(value));
		else if (type == char.class || type == Character.class)
			setFieldValue(obj, att, toCharacter(value));
		else if (type == long.class || type == Long.class)
			setFieldValue(obj, att, toLong(value));
		else if (type == float.class || type == Float.class)
			setFieldValue(obj, att, toFloat(value));
		else if (type == byte.class || type == Byte.class)
			setFieldValue(obj, att, toByte(value));
		else if (type == boolean.class || type == Boolean.class)
			setFieldValue(obj, att, toBoolean(value));
		else if (type == short.class || type == Short.class)
			setFieldValue(obj, att, toShort(value));
		else if (type == java.util.Date.class) {
			setFieldValue(obj, att, df.parse(String.valueOf(value)));
		} else if (type == java.sql.Timestamp.class) {
			setFieldValue(obj, att, 
                    new Timestamp(
                            df.parse(String.valueOf(value)).getTime()));
        } else
        	setFieldValue(obj, att, value);
	}	

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Integer toInteger(Object object) {
		return Integer.parseInt(String.valueOf(object));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Double toDouble(Object object) {
		return Double.parseDouble(String.valueOf(object));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Float toFloat(Object object) {
		return Float.parseFloat(String.valueOf(object));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Long toLong(Object object) {
		return Long.parseLong(String.valueOf(object));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Boolean toBoolean(Object object) {
		return Boolean.parseBoolean(String.valueOf(object));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Short toShort(Object object) {
		return Short.parseShort(String.valueOf(object));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Byte toByte(Object object) {
		return Byte.parseByte(String.valueOf(object));
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Character toCharacter(Object object) {
		if (object == null)
			return '\u0beb';
		else
			return (Character) object;
	}	
	
	
	/**
	 * 
	 * @Title: getFields 
	 * @Description: 根据实例获取类的字段属性 
	 * @param obj
	 * @return
	 */
	public static Field[] getFields(Object obj){
		Field[] fields = obj.getClass().getDeclaredFields();
		Field[] parent = obj.getClass().getSuperclass().getDeclaredFields();
		Field[] result = new Field[fields.length+parent.length];
		System.arraycopy(fields,0,result, 0, fields.length);
		System.arraycopy(parent,0,result, fields.length, parent.length);
		return result;
	}
	
	
	/**
	 * 
	 * @Title: getFields 
	 * @Description: 根据实例获取类的字段属性 
	 * @param obj
	 * @return
	 */
	public static Field[] getFields(Class<?> clazz){
		Field[] fields = clazz.getDeclaredFields();
		Field[] parent = clazz.getSuperclass().getDeclaredFields();
		Field[] result = new Field[fields.length+parent.length];
		System.arraycopy(fields,0,result, 0, fields.length);
		System.arraycopy(parent,0,result, fields.length, parent.length);
		return result;
	}
	
	/**
	 * 
	 * @Title: getObject 
	 * @Description: 根据类名称字符串获取实例 
	 * @param name
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Object getObject(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		return Class.forName(name).newInstance();
	}
	/**
	 * 
	 * @Title: getFields 
	 * @Description: 根据类型名称字符串获取类字段属性
	 * @param name
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 */
	public static Field[] getFields(String name) throws SecurityException, ClassNotFoundException{
		Field[] fields = Class.forName(name).getDeclaredFields();
		Field[] child = Class.forName(name).getSuperclass().getDeclaredFields();
		Field[] result = new Field[fields.length+child.length];
		System.arraycopy(fields,0,result, 0, fields.length);
		System.arraycopy(child,0,result, fields.length, child.length);
		return result;
	}
	
	/**
	 * 
	 * @Title: getFields 
	 * @Description: 获取一个calss的字段名称和字段type的对应关系
	 * @param clazz
	 * @return
	 */
	public static Map<String,Class<?>> getFieldMap(Class<?> clazz){
		Map<String,Class<?>> map = new HashMap<String,Class<?>>();
		Field[] fields = clazz.getDeclaredFields();
		Field[] child = clazz.getSuperclass().getDeclaredFields();
		for(Field field : fields){
			map.put(field.getName(), field.getType());
		}
		for(Field field : child){
			map.put(field.getName(), field.getType());
		}
		return map;
	}
	
	
	public static Object getFieldValue(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		boolean acc = field.isAccessible();
		field.setAccessible(true);
		Object res = field.get(obj);
		field.setAccessible(acc);
		return res;

	}
	
	
	public static void setFieldValue(Object obj, Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
		boolean acc = field.isAccessible();
		field.setAccessible(true);
		field.set(obj, value);
		field.setAccessible(acc);
	}
	
	public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		boolean acc = field.isAccessible();
		field.setAccessible(true);
		field.set(obj, value);
		field.setAccessible(acc);
	}

}
