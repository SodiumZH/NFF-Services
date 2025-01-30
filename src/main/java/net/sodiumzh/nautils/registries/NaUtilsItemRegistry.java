package net.sodiumzh.nautils.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.item.debug.DebugAISwitchItem;
import net.sodiumzh.nautils.item.debug.DebugMobRemoverItem;
import net.sodiumzh.nautils.item.debug.DebugTargetSetterItem;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;

public class NaUtilsItemRegistry
{
	public static final DeferredRegister<Item> NAUTILS_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NaUtils.MOD_ID);
	
	public static final RegistryObject<DebugAISwitchItem> DEBUG_AI_SWITCH = NAUTILS_ITEMS.register("debug_ai_switch",
			() -> new DebugAISwitchItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))
					.descTranslatable("info.nautils.item.debug_ai_switch_desc")
					.cast());

	public static final RegistryObject<DebugTargetSetterItem> DEBUG_TARGET_SETTER = NAUTILS_ITEMS.register("debug_target_setter",
			() -> new DebugTargetSetterItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))
					.descTranslatable("info.nautils.item.debug_target_setter_desc")
					.cast());

	public static final RegistryObject<DebugTargetSetterItem> DEBUG_MOB_REMOVER = NAUTILS_ITEMS.register("debug_mob_remover",
			() -> new DebugMobRemoverItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))
					.descTranslatable("info.nautils.item.debug_mob_remover_desc")
					.description(DebugMobRemoverItem::getModeInfo)
					.description(DebugMobRemoverItem::getModeDesc)
					.descTranslatable("info.nautils.item.debug_mob_remover_switch_mode")
					.cast());
}
