package com.ddai.lib.reflectiondb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 与反射有关的一些静态方法。
 * @author Daining daining@1000chi.com
 * 2012-5-14
 *
 */
public class ReflectUtils {
	
	/**
	 * 用Cursor数据填充Object中的属性。
	 * @param entity
	 * @param cursor
	 * @return 
	 * @author Daining daining@1000chi.com
	 * 2012-5-14
	 */
	public static Object readObject(Object entity, Cursor cursor) {
		Class<?> cls = entity.getClass();
		Field[] fields = cls.getFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				String typeName = field.getType().getSimpleName();
				// int --> Int,doble--->Double
				typeName = typeName.substring(0, 1).toUpperCase()
						+ typeName.substring(1);
				// Cursor' method name.
				String methodName = "get" + typeName;
				Method method = cursor.getClass().getMethod(methodName,
						int.class);
				Object retValue = method.invoke(cursor,
						cursor.getColumnIndex(field.getName()));
				field.set(entity, retValue);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return entity;
	}
	
	/**
	 * 获取此Class对应的表名。定义于Entity {@link Entity}}
	 * @param cls
	 * @return
	 * @author Daining daining@1000chi.com
	 * 2012-5-14
	 */
	public static String getTableName(Class<?> cls){
		String tableName = null;
		Annotation[] anns = cls.getAnnotations();
		if (anns.length > 0) {
			Entity en = (Entity) anns[0];
			tableName = en.name();
		}
		return tableName;
	}

	/**
	 * 获取ContentValues从一个Object。 将这个Object中的所有属性（除了标记为Transient的）转换成ContentValues
	 * @param object 需要保存到DB的Object。
	 * @return	ContentValues
	 * @author Daining daining@1000chi.com
	 * 2012-5-14
	 */
	public static ContentValues getContentValuesFromObject(Object object) {
		ContentValues cv = new ContentValues();
		Class<?> cls = object.getClass();
		Field[] fs1 = cls.getFields();
		for (Field field : fs1) {
			String declared = field.getName();
			try {
				if (!field.isAnnotationPresent(Transient.class)) {
					Class<?> type = field.getType();
					if (type == double.class) {
						double value = field.getDouble(object);
						cv.put(declared, value);
					} else if (type == int.class) {
						int value = field.getInt(object);
						cv.put(declared, value);
					} else {
						String value = String.valueOf(field.get(object));
						cv.put(declared, value);
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return cv;
	}
}
