package net.sodiumzh.nfu.item.bauble;

import java.util.function.Predicate;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.sodiumzh.nfu.util.NFUFunctionStatics;
import net.sodiumzh.nfu.util.NFUInfoStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.function.Predicate;

/**
 * A checker for if a bauble item can be equipped in a given bauble slot of a given living entity.
 */
public class BaubleEquippingCondition implements Predicate<BaubleProcessingArgs>
{

	/**
	 * Predicate for directly checking the condition.
	 */
	private Predicate<BaubleProcessingArgs> conditionPredicate;
	// A string name for display in debug page. Optional.
	@Nullable
	private String name = null;
	@Nullable
	private String translationKey = null;
	@Nullable
	private Object[] translationArgs = new Object[]{};
	private BaubleEquippingCondition(Predicate<BaubleProcessingArgs> condition)
	{
		this.conditionPredicate = condition;
	}
	
	/**
	 * Create a condition with an existing predicate. 
	 */
	public static BaubleEquippingCondition of(Predicate<BaubleProcessingArgs> predicate)
	{
		return new BaubleEquippingCondition(predicate);
	}

	/**
	 * Create a new condition that is always true.
	 */
	private static BaubleEquippingCondition always()
	{
		return of(args -> true);
	}

	/**
	 * Create a condition only for a given mob type.
	 */
	public static BaubleEquippingCondition forMob(EntityType<? extends Mob> type)
	{
		return always().onlyForType(type);
	}
	
	/**
	 * Create a condition only for a given mob type.
	 */
	@SafeVarargs
	public static BaubleEquippingCondition forMobs(EntityType<? extends Mob>... types)
	{
		return always().onlyForTypes(types);
	}
	
	/**
	 * Create a condition only for subclass of a given class/interface.
	 */
	public static BaubleEquippingCondition forSubclassOf(Class<?> clazz)
	{
		return always().onlyForSubclassOf(clazz);
	}
	
	/**
	 * Create a depending copy of given condition without direct access of the internal predicate.
	 * <p>Note: If this condition is modified, the created dependent will also change;
	 * but modifying the dependent will not affect this condition.
	 */
	public BaubleEquippingCondition createDependent()
	{
		return new BaubleEquippingCondition(args -> this.test(args));
	}
	
	/**
	 * Create a depending copy of given condition without direct access of the internal predicate.
	 * <p>Note: If the source condition is modified, the created dependent will also change;
	 * but modifying the dependent will not affect the source condition.
	 */
	public static BaubleEquippingCondition createDependent(BaubleEquippingCondition from)
	{
		return new BaubleEquippingCondition(args -> from.test(args));
	}
	
	/**
	 * Add an AND condition to THIS condition instance.
	 * <p>Note: this will modify current predicate. To make OR condition, create a new condition instance.
	 */
	public BaubleEquippingCondition setAnd(Predicate<BaubleProcessingArgs> other)
	{
		var old = conditionPredicate;
		conditionPredicate = (args) -> (old.test(args) && other.test(args));
		return this;
	}

	/**
	 * Create a NEW condition instance with AND operation of this and input conditions.
	 */
	@Nonnull
	@Override
	public BaubleEquippingCondition and(@Nonnull Predicate<? super BaubleProcessingArgs> other) {
		return BaubleEquippingCondition.of(args -> this.test(args) && other.test(args));
	}

	/**
	 * Create a NEW condition instance with OR operation of this and input conditions.
	 */
	@Nonnull
	@Override
	public BaubleEquippingCondition or(Predicate<? super BaubleProcessingArgs> other) {
		return BaubleEquippingCondition.of(args -> this.test(args) || other.test(args));
	}

	/**
	 * Create a NEW condition instance negating this condition.
	 */
	@Nonnull
	@Override
	public BaubleEquippingCondition negate() {
		return BaubleEquippingCondition.of(args -> !this.test(args));
	}

	/**
	 * Create a NEW condition instance with XOR operation of this and input conditions.
	 */
	@Nonnull
	public BaubleEquippingCondition xor(Predicate<? super BaubleProcessingArgs> other) {
		return BaubleEquippingCondition.of(args -> this.test(args) != other.test(args));
	}

	/**
	 * Exclude a specific entity type.
	 */
	public BaubleEquippingCondition excludeLivingType(EntityType<? extends Mob> toExclude)
	{
		return this.setAnd((args) -> (args.user().getType() != toExclude));
	}

	/**
	 * Exclude multiple entity types.
	 */
	public BaubleEquippingCondition excludeLivingTypes(EntityType<? extends Mob>... toExclude)
	{
		return this.setAnd(args -> NFUFunctionStatics.and((EntityType<? extends Mob> type) -> args.user().getType() != type, toExclude));
	}
	
	/**
	 * Exclude specific class, not including subclasses.
	 */
	public BaubleEquippingCondition excludeClass(Class<?> toExclude)
	{
		return this.setAnd((args) -> args.user().getClass() != toExclude);
	}
	
	/**
	 * Exclude a class and its subclasses. Accepting interfaces.
	 */
	public BaubleEquippingCondition excludeSubclassesOf(Class<?> toExclude)
	{
		return this.setAnd((args) -> !toExclude.getClass().isAssignableFrom(args.user().getClass()));
	}
	
	/**
	 * Add a condition only checked for a specific type but skipped for other types.
	 */
	public BaubleEquippingCondition addConditonForType(EntityType<? extends Mob> type, Predicate<BaubleProcessingArgs> condition)
	{
		return this.setAnd((args) -> (args.user().getType() != type || condition.test(args)));
	}
	
	/**
	 * Exclude a bauble slot key for a specific type.
	 */
	public BaubleEquippingCondition excludeSlotForType(EntityType<? extends Mob> type, String toExclude)
	{
		return this.setAnd((args) -> (args.user().getType() != type || args.slotKey() != toExclude));
	}
	
	/**
	 * Make this condition only allow given type.
	 */
	public BaubleEquippingCondition onlyForType(EntityType<? extends Mob> type)
	{
		return this.setAnd((args) -> args.user().getType() == type);
	}
	
	/**
	 * Make this condition only allow given types.
	 */
	public BaubleEquippingCondition onlyForTypes(EntityType<? extends Mob>... types)
	{
		return this.setAnd((args) -> NFUFunctionStatics.or((EntityType<? extends LivingEntity> entitytype) -> args.user().getType() == entitytype, types));
	}
	
	/**
	 * Make this condition only allow subclasses of given class.
	 */
	public BaubleEquippingCondition onlyForSubclassOf(Class<?> clazz)
	{
		return this.setAnd(args -> clazz.isAssignableFrom(args.user().getClass()));
	}
	
	/**
	 * Make this condition only allow a given slot of a given type.
	 */
	public BaubleEquippingCondition onlyForSlotOfType(EntityType<? extends Mob> type, String key)
	{
		return this.setAnd((args) -> args.user().getType() == type && args.slotKey() == key);
	}

	public Predicate<BaubleProcessingArgs> asPredicate() {
		return this.conditionPredicate;
	}

	/**
	 * Set the name displayed in debug mode. It only impacts toString().
	 */
	public BaubleEquippingCondition setName(String name) {
		this.name = name;
		return this;
	}

	public BaubleEquippingCondition setTranslation(@Nonnull String key, Object... args) {
		this.translationKey = key;
		this.translationArgs = args == null ? new Object[]{} : args;
		return this;
	}

	@Nullable
	public MutableComponent getTranslation() {
		if (translationKey == null) return null;
		return NFUInfoStatics.createTranslatable(translationKey, translationArgs);
	}

	/**
	 * Finally check if an equipping attempt is allowed.
	 */
	public boolean test(BaubleProcessingArgs args)
	{
		return this.conditionPredicate.test(args);
	}

	public String toString() {
		if (this.name != null)
			return "BaubleEquippingCondition '" + this.name + "'";
		else return super.toString();
	}

}
