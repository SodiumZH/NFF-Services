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
		if (index >= this.maxSize)
			throw new IllegalArgumentException("Index is larger than the set max. Attempted index: " + Integer.toString(index) + "; Set max: " + Integer.toString(maxSize));
		while (modifiers.size() <= index)
		{
			modifiers.add(new AttributeModifier(UUID.randomUUID(), Integer.toString(modifiers.size()), this.value, this.operation));
		}
		return modifiers.get(index);
	}
	
	public void apply(LivingEntity target, Attribute attribute, int times, boolean isPermanent)
	{
		AttributeInstance inst = target.getAttribute(attribute);
		int i = 0;
		for (i = 0; i < times; ++i)
		{
			inst.removeModifier(this.get(i));
			if (isPermanent)
			{
				inst.addPermanentModifier(this.get(i));
			}
			else inst.addTransientModifier(this.get(i));
		}
		for (i = times; i < this.modifiers.size(); ++i)
		{
			inst.removeModifier(this.get(i));
		}
	}
	
	
	public void clear(LivingEntity target, Attribute attribute)
	{
		AttributeInstance inst = target.getAttribute(attribute);
		for (int i = 0; i < modifiers.size(); ++i)
		{
			inst.removeModifier(modifiers.get(i));
		}
	}
	
}
