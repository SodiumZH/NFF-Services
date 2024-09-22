package net.sodiumzh.nff.services.event.entity.ai;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;

@Cancelable
public class NFFTamedChangeAiStateEvent extends Event
{

	protected INFFTamed mob;
	protected NFFTamedMobAIState stateBefore;
	protected NFFTamedMobAIState stateAfter;
	public NFFTamedChangeAiStateEvent(INFFTamed mob, NFFTamedMobAIState stateBefore, NFFTamedMobAIState stateAfter)
	{
		this.mob = mob;
		this.stateBefore = stateBefore;
		this.stateAfter = stateAfter;
	}
	public INFFTamed getMob()
	{
		return mob;
	}
	public NFFTamedMobAIState getStateBefore()
	{
		return stateBefore;
	}
	public NFFTamedMobAIState getStateAfter()
	{
		return stateAfter;
	}
	
}
