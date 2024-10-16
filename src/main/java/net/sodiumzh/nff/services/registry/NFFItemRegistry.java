 package net.sodiumzh.nff.services.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.item.NFFInstantTamerItem;

public class NFFItemRegistry {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NFFServices.MOD_ID);
	
	public static final RegistryObject<Item> DEBUG_BEFRIENDER = ITEMS.register("instant_taming_tool", () -> new NFFInstantTamerItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
	//public static final RegistryObject<Item> DEBUG_AI_SWITCH = ITEMS.register("debug_ai_switch", () -> new Item(new Item.Properties().stacksTo(1)));
	//public static final RegistryObject<Item> EXAMPLE_ZOMBIE_BEFRIENDING_ITEM = ITEMS.register("example_zombie_befriending_item", () -> new Item(new Item.Properties()));
	//public static final RegistryObject<Item> DEBUG_ARMOR_GIVER = ITEMS.register("debug_armor_giver", () -> new Item(new Item.Properties().stacksTo(1)));
	//public static final RegistryObject<Item> DEBUG_TARGET_SETTER = ITEMS.register("debug_target_setter", () -> new Item(new Item.Properties().stacksTo(1)));
	//public static final RegistryObject<Item> DEBUG_MOB_CONVERTER = ITEMS.register("debug_mob_converter", () -> new Item(new Item.Properties().stacksTo(1)));
	//public static final RegistryObject<Item> DEBUG_ATTRIBUTE_CHECKER = ITEMS.register("debug_attribute_checker", () -> new Item(new Item.Properties().stacksTo(1)));

	// A fake item for occupying a stack to enable/block something
	/*@Deprecated
	public static final RegistryObject<Item> DUMMY_ITEM = ITEMS.register("dummy_item", () -> new Item(new Item.Properties()));
	@Deprecated
	public static final RegistryObject<Item> MOB_RESPAWNER = ITEMS.register("mob_respawner", () -> new NFFMobRespawnerItem(new Item.Properties().stacksTo(1)));*/
}
