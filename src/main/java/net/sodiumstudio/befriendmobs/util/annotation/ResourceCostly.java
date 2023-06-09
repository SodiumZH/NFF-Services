package net.sodiumstudio.befriendmobs.util.annotation;

/**
 * A label for methods costly on resource and possible to cause performance down if called too frequently.
 * <p> Such methods may include level object iteration, multi-dimensional iteration, large amount of reflect calls or other code that may be costly for performance.
 * <p> 表明该方法资源占用较大，如果过于频繁调用可能导致游戏性能下降。
 * <p> 这些方法可能包含：迭代世界中所有某类对象，多维迭代，大量使用的反射或其他可能占用大量资源的代码。
 */
public @interface ResourceCostly
{
	public String reason() default "Unspecified";
	
	/** The method is iteration on all certain objects in the level e.g. all entities. 
	 * p方法包含迭代世界中所有某类对象，如所有实体。 
	 */
	public static final String LEVEL_ITERACTION = "Level Iteration";
	/** The method is doing multi-dimensional iteration. 
	 * <p> 方法包含多维迭代。
	 */
	public static final String MULTIDIM_ITERATION = "Multi-dimensional Iteration";
	/** The method is calling a large amount of reflect code.
	 * <p>方法大量使用反射代码。
	 */
	public static final String REFLECT = "Massive Reflect Code";
	/** The method is doing a large amount of strict numerical calculation. 
	 * <p>方法大量使用严格数值计算。
	 */
	public static final String NUMERICAL_CALCULATION = "Massive Numerical Calculation";
}
