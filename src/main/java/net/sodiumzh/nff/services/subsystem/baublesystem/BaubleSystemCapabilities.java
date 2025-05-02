package net.sodiumzh.nff.services.subsystem.baublesystem;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

class BaubleSystemCapabilities
{
	/**
	 * Capability on mobs to handle baubles. 
	 * <p>This capability isn't API-visible to prevent accident manual attachment. Register the mob type in
	 * {@link RegisterEaubleEvent} to attach.
	 * <p>To access, use {@link BaubleSystem#getCapability}, 
	 * {@link BaubleSystem#isCapabilityPresent} and {@link BaubleSystem#ifCapabilityPresent} instead.
	 */
	static Capability<CBaubleEquippableMob> CAP_BAUBLE_EQUIPPABLE_MOB = CapabilityManager.get(new CapabilityToken<>(){});

}
