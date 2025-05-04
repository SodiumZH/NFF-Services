package net.sodiumzh.nfu.capability;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashSet;
import java.util.Set;

/**
 * A base interface for entity capabilities that are automatically ticked. Needs registration.
 * <p>Use {@code registerTicking()} to register capability to the ticking list after creating, otherwise
 * it won't tick correctly.
 * <p>By default, it only ticks on server. Override {@code getTickingSide} to define on which side(s) it should tick.</>
 */
public interface CEntityTickingCapability<T extends Entity>
{
	static final Set<Capability<? extends CEntityTickingCapability<? extends Entity>>> ALL_CAPS = new HashSet<>();
	public void tick();
	public T getEntity();

	/**
	 * Get on which side(s) it should tick. Server by default.
	 * <p>Be careful to use {@code BOTH}, as it will tick independently on each side, and will not sync by default.</>
	 */
	public default TickingSide getTickingSide()
	{
		return TickingSide.SERVER;
	}
	/**
	 * Register a capability as ticking, so that it can be auto ticked.
	 */
	public static void registerTicking(Capability<? extends CEntityTickingCapability<? extends Entity>> cap)
	{
		ALL_CAPS.add(cap);
	}

	public static enum TickingSide {
		SERVER,
		CLIENT,
		BOTH;

		public boolean isCorrectSide(boolean isClientSide)
		{
			if (isClientSide)
				return this == CLIENT || this == BOTH;
			else return this == SERVER || this == BOTH;
		}
	}
}
