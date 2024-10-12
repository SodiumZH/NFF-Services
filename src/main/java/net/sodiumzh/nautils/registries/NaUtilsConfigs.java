package net.sodiumzh.nautils.registries;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class NaUtilsConfigs
{
	protected static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG;
	
	public static final ForgeConfigSpec.BooleanValue SPEC_ENABLES_SAVE_DATA_PORTER;
	public static final ForgeConfigSpec.BooleanValue SPEC_DEBUG_MODE;
	public static final ForgeConfigSpec.BooleanValue SPEC_CRASHES_WHEN_ENTITY_LOAD_FAILED;

	static
	{
		BUILDER.push("common");
		SPEC_ENABLES_SAVE_DATA_PORTER = BUILDER.comment("If true, SaveDataLocationRedirector will take effect. Setting it false could improve the performance, "
				+ "but it may cause objects (entities, items, blocks etc.) to disappear if you're using save data from an old version.")
				.define("enablesSaveDataPorter", true);
		BUILDER.pop();
		BUILDER.push("debug");
		SPEC_DEBUG_MODE = BUILDER.comment("If true, it will enable debug actions defined in NaUtilsDebugStatics, like debug output in the chatting box.")
				.define("debugMode", false);
		SPEC_CRASHES_WHEN_ENTITY_LOAD_FAILED = BUILDER.comment("If true, the game will crash if entity load failed instead of simply delete the entity.")
				.define("crashesWhenEntityLoadFailed", false);
		BUILDER.pop();
		CONFIG = BUILDER.build();
	}
	
	public static boolean CACHED_ENABLES_SAVE_DATA_PORTER = true;
	public static boolean CACHED_DEBUG_MODE = false;
	public static boolean CACHED_CRASHES_WHEN_ENTITY_LOAD_FAILED = false;
	
	public static void refresh()
	{
		CACHED_ENABLES_SAVE_DATA_PORTER = SPEC_ENABLES_SAVE_DATA_PORTER.get();
		CACHED_DEBUG_MODE = SPEC_DEBUG_MODE.get();
		CACHED_CRASHES_WHEN_ENTITY_LOAD_FAILED = SPEC_CRASHES_WHEN_ENTITY_LOAD_FAILED.get(); 
	}
	
	@SubscribeEvent
	public static void loadConfig(final ModConfigEvent event)
	{
		if (event.getConfig().getSpec() == CONFIG)
		{
			refresh();
		}
	}
}
