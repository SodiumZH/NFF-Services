package net.sodiumstudio.nautils.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeModifierSwitch
{
	protected HashMap<Integer, HashSet<Entry>> map = new HashMap<>();
	
	/**
	 * @param phase Integer indicating the mob's state.
	 * @param mod Attribute modifier.
	 */
	public AttributeModifierSwitch put(int phase, Attribute attr, AttributeModifier mod)
	{
		if (mod == null)
			return this;
		if (!map.containsKey(phase))
		{
			map.put(phase, new HashSet<>());
		}
		map.get(phase).add(new Entry(attr, mod));
		return this;
	}
	
	/**
	 * Put an automatically-generated modifier with random name and UUID.
	 */
	public AttributeModifierSwitch putGenerated(int phase, Attribute attr, double value, AttributeModifier.Operation operation)
	{
		UUID uuid = UUID.randomUUID();
		return put(phase, attr, new AttributeModifier(uuid, uuid.toString(), value, operation));
	}
	
	public AttributeModifierSwitch remove(int phase)
	{
		map.remove(phase);
		return this;
	}
	
	public void apply(Mob mob, int phase, boolean isPermanent)
	{
		for (int i: map.keySet())
		{
			if (i == phase)
			{
				for (var entry: map.get(i))
				{
					mob.getAttribute(entry.attr).removeModifier(entry.mod);
					if (isPermanent)
						mob.getAttribute(entry.attr).addPermanentModifier(entry.mod);
					else mob.getAttribute(entry.attr).addTransientModifier(entry.mod);
				}
			}
			else
			{
				for (var entry: map.get(i))
				{
					mob.getAttribute(entry.attr).removeModifier(entry.mod);
				}
			}
		}
	}
	
	public void apply(Mob mob, int phase)
	{
		apply(mob, phase, true);
	}
	
	protected static record Entry(Attribute attr, AttributeModifier mod)
	{
	}
	
}
