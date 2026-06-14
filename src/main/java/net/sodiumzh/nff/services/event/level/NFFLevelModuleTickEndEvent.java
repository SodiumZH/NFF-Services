package net.sodiumzh.nff.services.event.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nff.services.level.CNFFLevelModule;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;
import net.sodiumzh.nfu.util.NFUMiscStatics;

import javax.annotation.Nullable;

public class NFFLevelModuleTickEndEvent extends Event
{
	public final ServerLevel level;
	@Nullable public final CNFFLevelModule levelModule;
	
	public NFFLevelModuleTickEndEvent(ServerLevel level)
	{
		this.level = level;
		this.levelModule = level.getCapability(NFFCapRegistry.CAP_LEVEL).orElse(null);
	}
}
