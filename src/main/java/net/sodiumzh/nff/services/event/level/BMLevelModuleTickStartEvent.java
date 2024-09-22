package net.sodiumzh.nff.services.event.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;
import net.sodiumzh.nff.services.level.CNFFLevelModule;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;

public class BMLevelModuleTickStartEvent extends Event
{
	public final ServerLevel level;
	public final CNFFLevelModule levelModule;
	
	public BMLevelModuleTickStartEvent(ServerLevel level)
	{
		this.level = level;
		var cap = NaUtilsMiscStatics.getValue(level.getCapability(NFFCapRegistry.CAP_BM_LEVEL));
		this.levelModule = cap != null ? cap : null;
	}
}