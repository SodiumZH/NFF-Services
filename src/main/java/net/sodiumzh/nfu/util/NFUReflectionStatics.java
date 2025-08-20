package net.sodiumzh.nfu.util;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.sodiumzh.nfu.exception.ReflectionFailedException;
import net.sodiumzh.nfu.object.CastableObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class NFUReflectionStatics
{

	/**
	 * Force get a non-public field value.
	 * @deprecated Use {@code getFieldValue} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to get. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @return Value got.
	 */
	@Deprecated
	public static <T> CastableObject forceGet(T obj, Class<? super T> declaredClass, String fieldNameSrg, boolean noStackTrace)
	{
		return getFieldValue(obj, declaredClass, fieldNameSrg);
	}
	
	/**
	 * Force get a non-public field value.
	 * @deprecated Use {@code getFieldValue} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to get. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @return Value got.
	 */
	@Deprecated
	public static <T> CastableObject forceGet(T obj, Class<? super T> declaredClass, String fieldNameSrg)
	{
		return getFieldValue(obj, declaredClass, fieldNameSrg);
	}
	
	/**
	 * Force set a non-public field value
	 * @deprecated Use {@code getFieldValue} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to set. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param value New value to set.
	 */
	@Deprecated
	public static <T> void forceSet(T obj, Class<? super T> declaredClass, String fieldNameSrg, Object value, boolean noStackTrace)
	{
		setFieldValue(obj, declaredClass, fieldNameSrg, value);
	}
	
	/**
	 * Force set a non-public field value
	 * @deprecated Use {@code getFieldValue} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to set. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param value New value to set.
	 */
	@Deprecated
	public static <T> void forceSet(T obj, Class<? super T> declaredClass, String fieldNameSrg, Object value)
	{
		setFieldValue(obj, declaredClass, fieldNameSrg, value);
	}
	
	/**
	 * Force invoke a non-public method without return value.
	 * @deprecated Use {@code invokeDeclaredMethod} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvoke(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	@Deprecated
	public static <T> void forceInvoke(T obj, Class<? super T> declaredClass, boolean noStackTrace, String methodNameSrg, Object... paramTypesThenValues)
	{
		invokeDeclaredMethod(obj, declaredClass, methodNameSrg, paramTypesThenValues);
	}
	
	/**
	 * Force invoke a non-public method without return value.
	 * @deprecated Use {@code invokeDeclaredMethod} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvoke(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	@Deprecated
	public static <T> void forceInvoke(T obj, Class<? super T> declaredClass, String methodNameSrg, Object... paramTypesThenValues)
	{
		invokeDeclaredMethod(obj, declaredClass, methodNameSrg, paramTypesThenValues);
	}
	
	/**
	 * Force invoke a non-public method with return value.
	 * @deprecated Use {@code invokeDeclaredMethod} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param noStackTrace If true, it will not print stack trace if exception thrown.
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : String.class, Integer.class, "str", 0
	 * @return Returned value as a {@link CastableObject}. NOTE: DO NOT FORCE TYPE-CONVERT THE RESULT! Use {@link CastableObject#cast()} instead.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	@Deprecated
	public static <T> CastableObject forceInvokeRetVal(T obj, Class<? super T> declaredClass, boolean noStackTrace, String methodNameSrg, Object... paramTypesThenValues)
	{
		return invokeDeclaredMethod(obj, declaredClass, methodNameSrg, paramTypesThenValues);
	}

	/**
	 * Force invoke a non-public method with return value.
	 * @deprecated Use {@code invokeDeclaredMethod} instead.
	 * @param obj Target object.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : {@code String.class, Integer.class, "str", 0
	 * @return Returned value.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	@Deprecated
	public static <T> CastableObject forceInvokeRetVal(T obj, Class<? super T> declaredClass, String methodNameSrg, Object... paramTypesThenValues)
	{
		return invokeDeclaredMethod(obj, declaredClass, methodNameSrg, paramTypesThenValues);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Get the remapped name of a SRG method name. Returns as-is if it's not a valid SRG method name.
	 */
	public static String remapMethodName(String srg) {
		return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, srg);
	}

	/**
	 * Get the remapped name of a SRG class name. Returns as-is if it's not a valid SRG class name.
	 */
	public static String remapClassName(String srg) {
		return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.CLASS, srg);
	}

	/**
	 * Get the remapped name of a SRG field name. Returns as-is if it's not a valid SRG field name.
	 */
	public static String remapFieldName(String srg) {
		return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, srg);
	}

	/**
	 * Get the remapped name of a SRG method name. Returns {@link Optional#empty()} if it's not a valid SRG method name.
	 */
	public static Optional<String> remapOptionalMethodName(String name)
	{
		return FMLLoader.getNameFunction("srg")
			.map(f->f.apply(INameMappingService.Domain.METHOD, name));
	}

	/**
	 * Get the remapped name of a SRG class name. Returns {@link Optional#empty()} if it's not a valid SRG class name.
	 */
	public static Optional<String> remapOptionalClassName(String name)
	{
		return FMLLoader.getNameFunction("srg")
			.map(f->f.apply(INameMappingService.Domain.CLASS, name));
	}

	/**
	 * Get the remapped name of a SRG field name. Returns {@link Optional#empty()} if it's not a valid SRG field name.
	 */
	public static Optional<String> remapOptionalFieldName(String name)
	{
		return FMLLoader.getNameFunction("srg")
			.map(f->f.apply(INameMappingService.Domain.FIELD, name));
	}

	/**
	 * Find a field if declared in a given class (not including superclasses).
	 * <p>Note: this action may be costly if called frequently. If a specific field is accessed frequently, it's recommended
	 * to cache the result.
	 * @param declaredClass Class in which the field is declared.
	 * @param fieldNameSrg Field to find. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @return An {@link Optional} of the result field, or empty if absent.
	 * @param <T>
	 */
	public static <T> Optional<Field> findFieldIfDeclared(Class<?> declaredClass, String fieldNameSrg) {
		try {
			Field f = declaredClass.getDeclaredField(remapFieldName(fieldNameSrg));
			f.setAccessible(true);
			return Optional.of(f);
		} catch (NoSuchFieldException e) {
            return Optional.empty();
        } catch (Exception t) {
			throw new ReflectionFailedException(t);
		}
    }

	/**
	 * Find public non-static field if present in the given class or superclasses. Including protected/private fields
	 * in this class, but not in superclasses.
	 * <p>Note: this action may be costly if called frequently. If a specific field is accessed frequently, it's recommended
	 * to cache the result.
	 * @param clazz Class to find.
	 * @param fieldNameSrg Field to set. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @return An {@link Optional} of the result field, or empty if absent.
	 */
	public static <T> Optional<Field> findPublicFieldIfInherited(Class<?> clazz, String fieldNameSrg) {
		try {
			Field f = clazz.getField(remapFieldName(fieldNameSrg));
			f.setAccessible(true);
			return Optional.of(f);
		} catch (NoSuchFieldException e) {
			return Optional.empty();
		} catch (Exception t) {
			throw new ReflectionFailedException(t);
		}
	}

	/**
	 * Get a field value no matter if it's public.
	 * <p>Note: this action may be costly if called frequently. If a specific field is accessed frequently, it's recommended
	 * to cache the field by calling {@link NFUReflectionStatics#findFieldIfDeclared}.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is defined. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to get. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @return Value as a {@link CastableObject}.
	 */
	public static <T> CastableObject getFieldValue(T obj, Class<? super T> declaredClass, String fieldNameSrg)
	{
		Object result = null;
		try
		{
			result = ObfuscationReflectionHelper.getPrivateValue(declaredClass, obj, fieldNameSrg);
		}
		catch(Exception e)
		{
			throw new ReflectionFailedException(e);
		}
		return new CastableObject(result);
	}

	/**
	 * Set a field value.
	 * <p>Note: this action may be costly if called frequently. If a specific field is accessed frequently, it's recommended
	 * to cache the field by calling {@link NFUReflectionStatics#findFieldIfDeclared}.
	 * @param obj Target object.
	 * @param declaredClass Class in which the field is declared. (Not always equals to {code obj.class}!)
	 * @param fieldNameSrg Field to set. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param value New value to set.
	 */
	public static <T> void setFieldValue(T obj, Class<? super T> declaredClass, String fieldNameSrg, Object value)
	{
		try
		{
			ObfuscationReflectionHelper.setPrivateValue(declaredClass, obj, value, fieldNameSrg);
		}
		catch(Exception e)
		{
			throw new ReflectionFailedException(e);
		}
	}

	/**
	 * Invoke a method of a given object, including superclasses, NOT including static or superclass non-public methods.
	 * @param obj Target object. Non-null.
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : {@code String.class, int.class, "str", 0}
	 * @return Returned value.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static <T> CastableObject invokeInheritedPublicMethod(T obj, String methodNameSrg, Object... paramTypesThenValues) {
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
			result = obj.getClass().getMethod(remapMethodName(methodNameSrg), types).invoke(obj, vals);
		}
		catch(Exception e)
		{
			throw new ReflectionFailedException(e);
		}
		return new CastableObject(result);
	}

	/**
	 * Invoke a method declared in a given class, including private and static methods, NOT including superclasses.
	 * @param obj Target object. Nullable only if the method is static.
	 * @param declaredClass Class in which the method is defined. (Not always equals to {@code obj.class}!)
	 * @param methodNameSrg Method to run. Use SRG name which can be looked up at: <a href="https://linkie.shedaniel.dev/mappings?namespace=mojang_srg&version=1.20.1&search=">...</a>
	 * @param paramTypesThenValues Parameter names followed by values. For example, if a method is foo(String, int), then use : {@code String.class, int.class, "str", 0}
	 * @return Returned value.
	 * <p>Usage example: for method {@code foo(String str, int integer)} in class {@code Clazz}, call:
	 * <p>{@code forceInvokeRetVal(object, Clazz.class, noStackTrace, "foo", String.class, Integer.class, "str", 0);}
	 */
	public static <T> CastableObject invokeDeclaredMethod(T obj, Class<? super T> declaredClass, String methodNameSrg, Object... paramTypesThenValues) {
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
			throw new ReflectionFailedException(e);
		}
		return new CastableObject(result);
	}

	/**
	 * Find a method if it's present in the object's class, including superclasses, NOT including static or superclass non-public methods.
	 * @param clazz Target class. Non-null.
	 * @param methodNameSrg SRG name if the method is remapped (i.e. from vanilla MC).
	 *                         Or original name if not (i.e. from Forge or other mods).
	 * @param argTypes Method argument types.
	 * @return an {@link Optional} of the method if present. Or {@link Optional#empty()} if not.
	 */
	public static Optional<Method> findPublicMethodIfInherited(Class<?> clazz, String methodNameSrg, Class<?>... argTypes) {
		String remappedMethodName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, methodNameSrg);
		try {
			return Optional.of(clazz.getMethod(remappedMethodName, argTypes));
		} catch (NoSuchMethodException e) {
            return Optional.empty();
        } catch (RuntimeException e) {
			throw new ReflectionFailedException(e);
		}
    }

	/**
	 * Invoke a method if it's present in the object's class, including superclasses, NOT including static or superclass non-public methods.
	 * @param obj Target object. Non-null.
	 * @param methodNameSrg SRG name if the method is remapped (i.e. from vanilla MC). Or original name if not (i.e. from Forge or other mods).
	 * @param argsThenValues Parameter types followed by values. For example, if a method is foo(String, int), then use : {@code String.class, int.class, "str", 0}
	 * @return an {@link Optional} of the return value if the method is present. Or {@link Optional#empty()} if not.
	 * If the method invoked successfully but the return value is {@code null}, return an {@link Optional} containing an empty {@link CastableObject}.
	 */
	public static Optional<CastableObject> invokePublicMethodIfInherited(Object obj, String methodNameSrg, Object... argsThenValues) {
		try {
			return Optional.of(invokeInheritedPublicMethod(obj, methodNameSrg, argsThenValues));
		} catch (ReflectionFailedException e) {
			if (e.getCause() instanceof NoSuchMethodException)
				return Optional.empty();
			else throw e;
		} catch (RuntimeException e) {
			throw new ReflectionFailedException(e);
		}
	}

	/**
	 * Find a method declared in a given class, including private and static methods, NOT including superclasses.
	 * @param clazz Target class. Nullable only if the method is static.
	 * @param methodNameSrg SRG name if the method is remapped (i.e. from vanilla MC).
	 *                         Or original name if not (i.e. from Forge or other mods).
	 * @param argTypes Method argument types.
	 * @return an {@link Optional} of the method if present. Or {@link Optional#empty()} if not.
	 */
	public static Optional<Method> findMethodIfDeclared(Class<?> clazz, String methodNameSrg, Class<?>... argTypes) {
		String remappedMethodName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, methodNameSrg);
		try {
			return Optional.of(clazz.getDeclaredMethod(remappedMethodName, argTypes));
		} catch (NoSuchMethodException e) {
			return Optional.empty();
		} catch (RuntimeException e) {
			throw new ReflectionFailedException(e);
		}
	}

	/**
	 * Invoke a method declared in a given class, including private and static methods, NOT including superclasses.
	 * @param obj Target object. Nullable only if the method is static.
	 * @param methodNameSrg SRG name if the method is remapped (i.e. from vanilla MC). Or original name if not (i.e. from Forge or other mods).
	 * @param argsThenValues Parameter types followed by values. For example, if a method is foo(String, int), then use : {@code String.class, int.class, "str", 0}
	 * @return an {@link Optional} of the return value if the method is present. Or {@link Optional#empty()} if not.
	 * If the method invoked successfully but the return value is {@code null}, return an {@link Optional} containing an empty {@link CastableObject}.
	 */
	public static <T> Optional<CastableObject> invokeMethodIfDeclared(T obj, Class<? super T> declaredClass, String methodNameSrg, Object... argsThenValues) {
		try {
			return Optional.of(invokeDeclaredMethod(obj, declaredClass, methodNameSrg, argsThenValues));
		} catch (ReflectionFailedException e) {
			if (e.getCause() instanceof NoSuchMethodException)
				return Optional.empty();
			else throw e;
		} catch (RuntimeException e) {
			throw new ReflectionFailedException(e);
		}
	}

	/**
	 * Get all fields (including fields in parent classes, ignoring accessibility, no setting accessible)
	 */
	public static List<Field> getAllFields(Object obj, boolean includesStatic)
	{
		List<Field> allFlds = new ArrayList<>();
		Class<?> currentClz = obj.getClass();
		try {
			do {
				List<Field> flds = Arrays.asList(currentClz.getDeclaredFields());
				allFlds.addAll(flds);
				if (currentClz != Object.class)
					currentClz = currentClz.getSuperclass();
			}
			while (currentClz != Object.class);
		} catch (Throwable e)
		{
			throw new ReflectionFailedException(e);
		}
		if (!includesStatic)
			allFlds = allFlds.stream().filter(f -> !Modifier.isStatic(f.getModifiers())).toList();
		return allFlds;
	}
	
	/**
	 * Do an operation to all fields (including fields in parent classes, ignoring accessibility) of an object.
	 */
	public static void forAllFields(Object obj, Consumer<Object> operation, boolean includesStatic, boolean allowsFailure)
	{
		List<Field> flds = getAllFields(obj, includesStatic);
		for (Field fld: flds)
		{
			try {
				fld.setAccessible(true);
				operation.accept(fld.get(obj));
			} catch (Exception e) {
                if (!allowsFailure) throw new ReflectionFailedException(e);
            } finally {
				fld.setAccessible(false);
			}
		} 
	}

	/**
	 * Check if the program is currently running inside a specified method call
	 * of a specified class.
	 * <p>Note: Use this method only when the class doesn't need to be remapped (i.e. not vanilla-internal). For vanilla-internal
	 * (remapping-requiring) classes, always use {@link NFUReflectionStatics#isRunningInMethod(Class, String)} instead,
	 * otherwise it may encounter remapping issues.
	 * <p>Note: it cannot distinguish methods with same name.
	 * @param className <b>Fully qualified name</b> of the class, like {@code package.name.ClassName}.
	 */
	public static boolean isRunningInMethod(String className, String methodNameSrg) {
		String methodName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, methodNameSrg);
		return StackWalker.getInstance().walk(frames ->
			frames.anyMatch(frame -> frame.getClassName().equals(className)
				&& frame.getMethodName().equals(methodName)));
	}

	/**
	 * Check if the program is currently running inside a specified method call
	 * of a specified class.
	 * <p>Note: Use this version only when the class is vanilla-internal or present in a required dependency. For classes
	 * in optional dependency or compatible modules, always use {@link NFUReflectionStatics#isRunningInMethod(String, String)} instead,
	 * otherwise it may produce {@link NoClassDefFoundError}.
	 * <p>Note: it cannot distinguish methods with same name.
	 */
	public static boolean isRunningInMethod(Class<?> clazz, String methodNameSrg) {
		return isRunningInMethod(clazz.getName(), methodNameSrg);
	}

	/**
	 * Check if the program is currently running inside a specific method call.
	 * <p>Note: it cannot distinguish methods with same name.
	 */
	public static boolean isRunningInMethod(Method method) {
		return isRunningInMethod(method.getDeclaringClass(), method.getName());
	}

	public static boolean isRunningInClass(String className) {
		return StackWalker.getInstance().walk(frames ->
			frames.anyMatch(frame -> frame.getClassName().equals(className)));
	}

	public static boolean isRunningInClass(Class<?> clazz) {
		return isRunningInClass(clazz.getName());
	}

	/**
	 * Invoke a method without need of try-catch block.
	 */
	public static CastableObject invokeMethod(Method m, Object obj, Object... args) {
		try {
			m.setAccessible(true);
			return new CastableObject(m.invoke(obj, args));
		} catch (Exception e) {
			throw new ReflectionFailedException(e);
		}
    }

	/**
	 * Get a field value without need of try-catch block.
	 */
	public static CastableObject getValue(Field f, Object obj) {
		try {
			f.setAccessible(true);
			return new CastableObject(f.get(obj));
		} catch (Exception e) {
			throw new ReflectionFailedException(e);
		}
	}

	/**
	 * Set a field value without need of try-catch block.
	 */
	public static void setValue(Field f, Object obj, Object val) {
		try {
			f.setAccessible(true);
			f.set(obj, val);
		} catch (Exception e) {
			throw new ReflectionFailedException(e);
		}
	}
}
