package net.sodiumstudio.befriendmobs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectHelper
{
	
	/**
	 * Force get a non-public field value.
	 * @param obj Target object.
	 * @param objClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to get.
	 * @param value New value to get.
	 * @return Value got.
	 */
	public static Object forceGet(Object obj, Class<?> objClass, String fieldName)
	{
		Object result = null;
		try
		{
		Field fld = objClass.getDeclaredField(fieldName);
		fld.setAccessible(true);
		result = fld.get(obj);
		fld.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Force get a non-public field value.
	 * @param obj Target object.
	 * @param objClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to get.
	 * @param value New value to get.
	 * @return Value got.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T forceGetCasted(Object obj, Class<?> objClass, String fieldName)
	{
		Object result = null;
		T resultCast = null;
		try
		{
		Field fld = objClass.getDeclaredField(fieldName);
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
	 * @param objClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to set.
	 * @param value New value to set.
	 */
	public static void forceSet(Object obj, Class<?> objClass, String fieldName, Object value)
	{
		try
		{
		Field fld = objClass.getDeclaredField(fieldName);
		fld.setAccessible(true);
		fld.set(obj, value);
		fld.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Force invoke a non-public method without return value.
	 * @param obj Target object.
	 * @param objClass Class in which the method is defined. (Not always equals to {code obj.class}!)
	 * @param methodName Method to run.
	 * @param params Method parameters.
	 */
	public static void forceInvoke(Object obj, Class<?> objClass, String methodName, Object... params)
	{
		try
		{
		Method method = objClass.getDeclaredMethod(methodName);
		method.setAccessible(true);
		method.invoke(obj, params);
		method.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Force invoke a non-public method with return value.
	 * @param obj Target object.
	 * @param objClass Class in which the method is defined. (Not always equals to {code obj.class}!)
	 * @param fieldName Field to set.
	 * @param params Method parameters.
	 * @return Returned value.
	 */
	public static Object forceInvokeRetVal(Object obj, Class<?> objClass, String methodName, Object... params)
	{
		Object result = null;
		try
		{
		Method method = objClass.getDeclaredMethod(methodName);
		method.setAccessible(true);
		result = method.invoke(obj, params);
		method.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
}
