package net.sodiumstudio.nautils.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumstudio.nautils.NaUtils;
import net.sodiumstudio.nautils.item.debug.DebugAISwitchItem;
import net.sodiumstudio.nautils.item.debug.DebugTargetSetterItem;

public class NaUtilsItemRegistry
{
	
	// TODO: NaUtils.MOD_ID
	public static final DeferredRegister<Item> NAUTILS_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NaUtils.MOD_ID);
	
	public static final RegistryObject<DebugAISwitchItem> DEBUG_AI_SWITCH = NAUTILS_ITEMS.register("debug_ai_switch", () -> new DebugAISwitchItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
	public static final RegistryObject<DebugTargetSetterItem> DEBUG_TARGET_SETTER = NAUTILS_ITEMS.register("debug_target_setter", () -> new DebugTargetSetterItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
}
