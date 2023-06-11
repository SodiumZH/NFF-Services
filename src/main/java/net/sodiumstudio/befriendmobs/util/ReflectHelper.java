package net.sodiumstudio.befriendmobs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectHelper
{
	
	/**
	 * Force get a non-public field value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to get.
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param value New value to get.
	 * @return Value got.
	 */
	public static Object forceGet(Object obj, Class<?> declaredClass, String fieldName, boolean noStackTrace)
	{
		Object result = null;
		try
		{
		Field fld = declaredClass.getDeclaredField(fieldName);
		fld.setAccessible(true);
		result = fld.get(obj);
		fld.setAccessible(false);
		}
		catch(Exception e)
		{
			if (!noStackTrace)
				e.printStackTrace();
			return null;
		}
		return result;
	}
	
	/**
	 * Force get a non-public field value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to get.
	 * @param value New value to get.
	 * @return Value got.
	 */
	public static Object forceGet(Object obj, Class<?> declaredClass, String fieldName)
	{
		return forceGet(obj, declaredClass, fieldName, false);
	}
	
	/**
	 * Force get a non-public field value.
	 * @deprecated use {@code forceGet} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to get.
	 * @param value New value to get.
	 * @return Value got.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T forceGetCasted(Object obj, Class<?> declaredClass, String fieldName)
	{
		Object result = null;
		T resultCast = null;
		try
		{
		Field fld = declaredClass.getDeclaredField(fieldName);
		fld.setAccessible(true);
		result = fld.get(obj);
		fld.setAccessible(false);
		resultCast = (T) result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return resultCast;
	}
	
	/**
	 * Force set a non-public field value
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to set.
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param value New value to set.
	 */
	public static void forceSet(Object obj, Class<?> declaredClass, String fieldName, Object value, boolean noStackTrace)
	{
		try
		{
		Field fld = declaredClass.getDeclaredField(fieldName);
		fld.setAccessible(true);
		fld.set(obj, value);
		fld.setAccessible(false);
		}
		catch(Exception e)
		{
			if (!noStackTrace)
				e.printStackTrace();
		}
	}
	
	/**
	 * Force set a non-public field value
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to set.
	 * @param value New value to set.
	 */
	public static void forceSet(Object obj, Class<?> declaredClass, String fieldName, Object value)
	{
		forceSet(obj, declaredClass, fieldName, value, false);
	}
	
	/**
	 * Force invoke a non-public method without return value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param methodName Method to run.
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvoke(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static void forceInvoke(Object obj, Class<?> declaredClass, boolean noStackTrace, String methodName, Object... paramTypesThenValues)
	{
		try
		{
		// Parse varargs
		int paramCount = paramTypesThenValues.length / 2;
		Class<?>[] types = new Class<?>[paramCount];
		Object[] vals = new Object[paramCount];
		for (int i = 0; i < paramCount; ++i)
		{
			types[i] = (Class<?>) paramTypesThenValues[i];
			vals[i] = paramTypesThenValues[i + paramCount];
		}			
		// Invoke
		Method method = declaredClass.getDeclaredMethod(methodName, types);
		method.setAccessible(true);
		method.invoke(obj, vals);
		method.setAccessible(false);
		}
		catch(Exception e)
		{
			if (!noStackTrace)
				e.printStackTrace();
		}
	}
	
	/**
	 * Force invoke a non-public method without return value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param methodName Method to run.
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvoke(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static void forceInvoke(Object obj, Class<?> declaredClass, String methodName, Object... paramTypesThenValues)
	{
		forceInvoke(obj, declaredClass, false, methodName, paramTypesThenValues);
	}
	
	/**
	 * Force invoke a non-public method with return value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param methodName Method to run.
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * @return Returned value.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static Object forceInvokeRetVal(Object obj, Class<?> declaredClass, boolean noStackTrace, String methodName, Object... paramTypesThenValues)
	{
		Object result = null;
		try
		{
			// Parse varargs
			int paramCount = paramTypesThenValues.length / 2;
			Class<?>[] types = new Class<?>[paramCount];
			Object[] vals = new Object[paramCount];
			for (int i = 0; i < paramCount; ++i)
			{
				types[i] = (Class<?>) paramTypesThenValues[i];
				vals[i] = paramTypesThenValues[i + paramCount];
			}			
			// Invoke
			Method method = declaredClass.getDeclaredMethod(methodName, types);
			method.setAccessible(true);
			result = method.invoke(obj, vals);
			method.setAccessible(false);
		}
		catch(Exception e)
		{
			if (!noStackTrace)
				e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * Force invoke a non-public method with return value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param methodName Method to run.
	 * @param params Method parameters.
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * @return Returned value.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static Object forceInvokeRetVal(Object obj, Class<?> declaredClass, String methodName, Object... paramTypesThenValues)
	{
		return forceInvokeRetVal(obj, declaredClass, false, methodName, paramTypesThenValues);
	}
}
