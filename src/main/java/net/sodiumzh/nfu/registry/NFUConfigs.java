package net.sodiumzh.nfu.registry;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.sodiumzh.nfu.NFULibrary;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = NFULibrary.MOD_ID)
public class NFUConfigs
{
	protected static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG;
	
	public static final ForgeConfigSpec.BooleanValue SPEC_ENABLES_SAVE_DATA_PORTER;
	public static final ForgeConfigSpec.BooleanValue SPEC_ENABLES_TAG_FIX;
	public static final ForgeConfigSpec.BooleanValue SPEC_DEBUG_MODE;

	static
	{
		BUILDER.push("common");
		SPEC_ENABLES_SAVE_DATA_PORTER = BUILDER.comment("If true, SaveDataLocationRedirector will take effect. Setting it false could improve the performance, "
				+ "but it may cause objects (entities, items, blocks etc.) to disappear if you're using save data from an old version.")
				.define("enablesSaveDataPorter", true);
		SPEC_ENABLES_TAG_FIX = BUILDER.comment("If true, it will try fixing broken tags caused by missing entries. " +
			"This may fix problems due to missing tags e.g. unable to dig blocks with pickaxes, but might cause unexpected issues.")
			.comment("Experimental. Use at your own risk.")
			.define("enablesTagFix", false);
		BUILDER.pop();

		BUILDER.push("debug");
		SPEC_DEBUG_MODE = BUILDER.comment("If true, it will enable debug actions defined in NFUDebugStatics, like debug output in the chatting box.")
				.define("debugMode", false);
		BUILDER.pop();
		CONFIG = BUILDER.build();
	}
	
	public static boolean CACHED_ENABLES_SAVE_DATA_PORTER = true;
	public static boolean CACHED_ENABLES_TAG_FIX = false;
	public static boolean CACHED_DEBUG_MODE = false;
	public static boolean CACHED_CRASHES_WHEN_ENTITY_LOAD_FAILED = false;
	
	public static void refresh()
	{
		CACHED_ENABLES_SAVE_DATA_PORTER = SPEC_ENABLES_SAVE_DATA_PORTER.get();
		CACHED_ENABLES_TAG_FIX = SPEC_ENABLES_TAG_FIX.get();
		CACHED_DEBUG_MODE = SPEC_DEBUG_MODE.get();
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
