package net.sodiumstudio.nautils.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.sodiumstudio.nautils.annotation.DontCallManually;

/**
 * A {@code ConditionalAttributeModifier} is a wrapped {@link AttributeModifier} that updates on tick
 * to be added when condition is satisfied and removed otherwise.
 * <p>It needs only a single-instance for each operation and will be automatically updated.
 * <p>Note: For a given {@code ConditionalAttributeModifier}, it can only be applied to a given {@link Attribute} specified on construction. 
 * To apply the modifier to another {@link Attribute}, create another {@code ConditionalAttributeModifier} instance.
 * <p>Note: It's always <b>transient</b> i.e. not saved into entities' data. If needs saving, consider saving into the entity additional
 * data and manually apply on construction, deserialization or tick. 
 */
public final class ConditionalAttributeModifier {

	private static final HashSet<ConditionalAttributeModifier> ALL_MODIFIERS = new HashSet<>();
	
	private final HashSet<LivingEntity> usingLivings = new HashSet<>();
	
	private final Attribute attribute;
	private final AttributeModifier modifier;
	private final Predicate<LivingEntity> condition;
	
	
	
	public ConditionalAttributeModifier(Attribute attribute, AttributeModifier modifier, Predicate<LivingEntity> condition)
	{
		this.modifier = modifier;
		this.attribute = attribute;
		this.condition = condition;
		ALL_MODIFIERS.add(this);
	}
	
	public ConditionalAttributeModifier(Attribute attribute, double value, AttributeModifier.Operation operation, Predicate<LivingEntity> condition)
	{
		UUID uuid = UUID.randomUUID();
		AttributeModifier modifier = new AttributeModifier(uuid, uuid.toString(), value, operation);
		this.modifier = modifier;
		this.attribute = attribute;
		this.condition = condition;
		ALL_MODIFIERS.add(this);
	}
	
	@Deprecated
	public ConditionalAttributeModifier setPermanent(boolean val)
	{
		//this.isPermanent = val;
		return this;
	}
	
	public void apply(LivingEntity living)
	{
		if (living != null && living.isAlive())
			usingLivings.add(living);
	}
	
	public void remove(LivingEntity living)
	{
		usingLivings.remove(living);
	}
	
	/** Invoked before level tick */
	@DontCallManually
	public static void update()
	{
		for (ConditionalAttributeModifier cam: ALL_MODIFIERS)
		{
			cam.usingLivings.removeIf(l -> !l.isAlive());
			for (LivingEntity living: cam.usingLivings)
			{
				if (cam.condition.test(living))
				{
					if (!living.getAttribute(cam.attribute).hasModifier(cam.modifier))
					{
						living.getAttribute(cam.attribute).addTransientModifier(cam.modifier);
					}
				}
				else living.getAttribute(cam.attribute).removeModifier(cam.modifier);
			}
		}
	}
	
}
