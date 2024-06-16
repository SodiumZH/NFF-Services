package net.sodiumstudio.nautils.entity;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * A {@code RepeatableAttributeModifier} represents an attribute modifier which can be applied multiple times.
 * It's recommended to create a static instance for each {@code RepeatableAttributeModifier} in each class.
 * <p>Note: The complexity of the {@code apply} operation is O(times) because it must iterate through the whole modifier list
 * to find which modifier it's previously applying. Take care if you need to update on tick.
 */
public class RepeatableAttributeModifier
{
	protected double value;
	protected AttributeModifier.Operation operation;
	protected ArrayList<AttributeModifier> modifiers = new ArrayList<>();
	/** If the modifier count is larger than this value, it will throw an exception.
	 * This limitation is to prevent the ArrayList from getting too large because it will auto-expand and generate 
	 * more {@code AttributeModifier} instances when accessing the index larger than its size.
	 */
	protected int maxSize;
	
	public RepeatableAttributeModifier(double value, AttributeModifier.Operation operation, int maxRepeatTimes)
	{
		this.value = value;
		this.operation = operation;
		this.maxSize = maxRepeatTimes;
	}
	
	public RepeatableAttributeModifier(double value, AttributeModifier.Operation operation)
	{
		this(value, operation, 20);
	}
	
	public AttributeModifier get(int index)
	{
		if (index > this.maxSize)
			throw new IllegalArgumentException("Index is larger than the set max. Attempted index: " + Integer.toString(index) + "; Set max: " + Integer.toString(maxSize));
		// The first element (index == 0) is zero, so max length should be (max + 1).
		while (modifiers.size() <= index + 1)
		{
			modifiers.add(new AttributeModifier(UUID.randomUUID(), Integer.toString(modifiers.size()), this.value * modifiers.size(), this.operation));
		}
		return modifiers.get(index);
	}
	
	public void apply(LivingEntity target, Attribute attribute, int times, boolean isPermanent)
	{
		AttributeInstance inst = target.getAttribute(attribute);
		// Check if it's already applying the same modifier. This could prevent iteration on tick.
		if (inst.hasModifier(this.get(times)))
		{
			inst.removeModifier(this.get(times));
			if (isPermanent)
				inst.addPermanentModifier(this.get(times));
			else inst.addTransientModifier(this.get(times));
			return;
		}
		for (var modifier: modifiers)
		{
			inst.removeModifier(modifier);
		}
		if (isPermanent)
		{
			inst.addPermanentModifier(this.get(times));
		}
		else inst.addTransientModifier(this.get(times));
	}

	public void clear(LivingEntity target, Attribute attribute)
	{
		AttributeInstance inst = target.getAttribute(attribute);
		for (var modifier: modifiers)
		{
			inst.removeModifier(modifier);
		}
	}
	
}
