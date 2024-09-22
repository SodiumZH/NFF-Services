package net.sodiumzh.nautils.capability;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;

/**
 * A base interface for entity capabilities that are automatically ticked.
 * <p>Use {@code registerTicking()} to register capability to the ticking list after creating, otherwise
 * it won't tick correctly.
 */
public interface CEntityTickingCapability<T extends Entity>
{
	static final Set<Capability<? extends CEntityTickingCapability<? extends Entity>>> ALL_CAPS = new HashSet<>();
	public void tick();
	public T getEntity();
	
	public static void registerTicking(Capability<? extends CEntityTickingCapability<? extends Entity>> cap)
	{
		ALL_CAPS.add(cap);
	}
	
}
