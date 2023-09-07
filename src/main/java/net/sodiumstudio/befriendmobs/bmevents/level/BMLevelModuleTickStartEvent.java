package net.sodiumstudio.befriendmobs.bmevents.level;

import net.minecraftforge.eventbus.api.Event;

public class BMLevelModuleTickStartEvent extends Event
{

	public final ServerLevel level;
	public final CBMLevelModule levelModule;
}
