package net.sodiumstudio.befriendmobs.bmevents.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.level.CBMLevelModule;
import net.sodiumstudio.befriendmobs.registry.BMCaps;
import net.sodiumstudio.nautils.MiscUtil;

public class BMLevelModuleTickEndEvent extends Event
{
	public final ServerLevel level;
	public final CBMLevelModule levelModule;
	
	public BMLevelModuleTickEndEvent(ServerLevel level)
	{
		this.level = level;
		var cap = MiscUtil.getValue(level.getCapability(BMCaps.CAP_BM_LEVEL));
		this.levelModule = cap != null ? cap : null;
	}
}
