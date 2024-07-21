package net.sodiumstudio.nautils.capability;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * A base interface for entity capabilities that are automatically ticked.
 * <p>Use {@code CEntityTickingCapability#createHolder()} to create {@code Capability} instead of {@code CapabilityManager#get}, otherwise
 * it won't tick correctly.
 */
public interface CEntityTickingCapability<T extends Entity>
{
	static final Set<Capability<? extends CEntityTickingCapability<? extends Entity>>> ALL_CAPS = new HashSet<>();
	public void tick();
	public T getEntity();
	
	public static <E extends Entity, C extends CEntityTickingCapability<E>> Capability<C> createHolder()
	{
		Capability<C> res = CapabilityManager.get(new CapabilityToken<>(){});
		ALL_CAPS.add(res);
		return res;
	}
	
}
