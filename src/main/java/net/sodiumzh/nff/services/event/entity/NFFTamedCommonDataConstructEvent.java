package net.sodiumzh.nff.services.event.entity;

import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.taming.CNFFTamedCommonData;

/**
 * Posted on {@link CNFFTamedCommonData} construct (initialize) after adding common data.
 */
public class NFFTamedCommonDataConstructEvent extends Event
{
	private final CNFFTamedCommonData data;
	
	public NFFTamedCommonDataConstructEvent(CNFFTamedCommonData data)
	{
		this.data = data;
	}

	public CNFFTamedCommonData getData() {
		return data;
	}
}
