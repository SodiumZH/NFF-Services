package net.sodiumstudio.nautils;

import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class ReflectHelper
{
	
	/**
	 * Force get a non-public field value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to get. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param value New value to get.
	 * @return Value got.
	 */
	public static <T> Object forceGet(T obj, Class<? super T> declaredClass, String fieldNameSrg, boolean noStackTrace)
	{
		Object result = null;
		try
		{
			result = ObfuscationReflectionHelper.getPrivateValue(declaredClass, obj, fieldNameSrg);
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
	 * @param fieldNameSrg Field to get. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param value New value to get.
	 * @return Value got.
	 */
	public static <T> Object forceGet(T obj, Class<? super T> declaredClass, String fieldNameSrg)
	{
		return forceGet(obj, declaredClass, fieldNameSrg, false);
	}
	
	/**
	 * Force set a non-public field value
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to set. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param value New value to set.
	 */
	public static <T> void forceSet(T obj, Class<? super T> declaredClass, String fieldNameSrg, Object value, boolean noStackTrace)
	{
		try
		{
			ObfuscationReflectionHelper.setPrivateValue(declaredClass, obj, value, fieldNameSrg);
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
	 * @param fieldNameSrg Field to set. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param value New value to set.
	 */
	public static <T> void forceSet(T obj, Class<? super T> declaredClass, String fieldNameSrg, Object value)
	{
		forceSet(obj, declaredClass, fieldNameSrg, value, false);
	}
	
	/**
	 * Force invoke a non-public method without return value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvoke(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static <T> void forceInvoke(T obj, Class<? super T> declaredClass, boolean noStackTrace, String methodNameSrg, Object... paramTypesThenValues)
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
		ObfuscationReflectionHelper.findMethod(declaredClass, methodNameSrg, types).invoke(obj, vals);
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
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvoke(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static <T> void forceInvoke(T obj, Class<? super T> declaredClass, String methodNameSrg, Object... paramTypesThenValues)
	{
		forceInvoke(obj, declaredClass, false, methodNameSrg, paramTypesThenValues);
	}
	
	/**
	 * Force invoke a non-public method with return value.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * @return Returned value.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static <T> Object forceInvokeRetVal(T obj, Class<? super T> declaredClass, boolean noStackTrace, String methodNameSrg, Object... paramTypesThenValues)
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
			result = ObfuscationReflectionHelper.findMethod(declaredClass, methodNameSrg, types).invoke(obj, vals);
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
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.19.2&search=
	 * @param params Method parameters.
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * @return Returned value.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static <T> Object forceInvokeRetVal(T obj, Class<? super T> declaredClass, String methodNameSrg, Object... paramTypesThenValues)
	{
		return forceInvokeRetVal(obj, declaredClass, false, methodNameSrg, paramTypesThenValues);
	}
}
