package net.sodiumzh.nautils.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeModifierSwitch
{
	protected HashMap<Integer, HashSet<Entry>> map = new HashMap<>();
	private boolean hasGenerated = false;
	
	/**
	 * Put an {@code AttributeModifier} with a <b>certain</b> UUID.
	 * <p>Note: <b>DO NOT use random UUID by this!</b> Otherwise modifier duplication will occur on restarting
	 * the game if permanent attributes are applied, as the random UUIDs change on each launch, the old modifiers will
	 * be left in entity nbt on close which cannot be removed after restarting, causing duplicate application of the same modifiers.
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
	 * <p>Note: If randomized UUID is added by this method, this will not allow permanent modifiers anymore as the UUIDs
	 * will change on each launch. Modifier duplication will occur on restarting
	 * the game if permanent attributes are applied, as the random UUIDs change on each launch, the old modifiers will
	 * be left in entity nbt on close which cannot be removed after restarting, causing duplicate application of the same modifiers.
	 */
	public AttributeModifierSwitch putGenerated(int phase, Attribute attr, double value, AttributeModifier.Operation operation)
	{
		UUID uuid = UUID.randomUUID();
		this.hasGenerated = true;
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
					if (isPermanent && !this.hasGenerated)
						mob.getAttribute(entry.attr).addPermanentModifier(entry.mod);
					else if (isPermanent)
					{
						throw new UnsupportedOperationException("AttributeModifierSwitch: Permanent modifier is not allowed if generated random UUID is used.");
					}
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
		apply(mob, phase, false);
	}
	
	protected static record Entry(Attribute attr, AttributeModifier mod)
	{
	}
	
}
