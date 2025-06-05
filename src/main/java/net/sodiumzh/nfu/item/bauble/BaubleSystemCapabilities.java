package net.sodiumzh.nfu.item.bauble;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

class BaubleSystemCapabilities
{
	/**
	 * Capability on mobs to handle baubles. 
	 * <p>This capability isn't API-visible to prevent accident manual attachment. Register the mob type in
	 * {@link RegisterEaubleEvent} to attach.
	 * <p>To access, use {@link NFUBaubleAPI#getCapability},
	 * {@link NFUBaubleAPI#isCapabilityPresent} and {@link NFUBaubleAPI#ifCapabilityPresent} instead.
	 */
	static Capability<CBaubleEquippableMob> CAP_BAUBLE_EQUIPPABLE_MOB = CapabilityManager.get(new CapabilityToken<>(){});

}
