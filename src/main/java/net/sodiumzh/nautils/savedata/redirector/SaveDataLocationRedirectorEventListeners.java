package net.sodiumzh.nautils.savedata.redirector;

import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.annotation.DontCallManually;
import net.sodiumzh.nautils.mixin.event.entity.EntityLoadEvent;
import net.sodiumzh.nautils.mixin.event.level.WorldCapabilityDataLoadEvent;
import net.sodiumzh.nautils.registries.NaUtilsConfigs;
import net.sodiumzh.nautils.statics.NaUtilsContainerStatics;
import net.sodiumzh.nautils.statics.NaUtilsNBTStatics;

@Mod.EventBusSubscriber(modid = NaUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SaveDataLocationRedirectorEventListeners
{
	@SubscribeEvent
	@DontCallManually
	public static void doPortEntityCapabilities(EntityLoadEvent event)
	{
		if (SaveDataLocationRedirectorRegistries.ENTITY_CAPABILITY_MAPPING.isEmpty() && SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.isEmpty()) return;
		CompoundTag capTags = event.getNBT().getCompound("ForgeCaps");
		if (capTags.isEmpty()) return;
		Set<String> keys = NaUtilsContainerStatics.iterableToSet(capTags.getAllKeys());
		for (String key: keys)
		{
			ResourceLocation newKey = new ResourceLocation(key);
			if (SaveDataLocationRedirectorRegistries.ENTITY_CAPABILITY_MAPPING.containsKey(newKey))
				newKey = SaveDataLocationRedirectorRegistries.ENTITY_CAPABILITY_MAPPING.get(newKey);
			if (SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.containsKey(newKey.getNamespace()))
				newKey = new ResourceLocation(SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.get(newKey.getNamespace()), newKey.getPath());
			if (!newKey.toString().equals(key))
			{
				NaUtilsNBTStatics.resetKey(capTags, key, newKey.toString());
			}
		}
	}
	
	@SubscribeEvent
	public static void doPortLevelCapabilities(WorldCapabilityDataLoadEvent event)
	{
		if (!NaUtilsConfigs.CACHED_ENABLES_SAVE_DATA_PORTER) return;
		if (SaveDataLocationRedirectorRegistries.LEVEL_CAPABILITY_MAPPING.isEmpty() && SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.isEmpty()) return;
		Set<String> keys = NaUtilsContainerStatics.iterableToSet(event.getNbt().getAllKeys());
		for (String key: keys)
		{
			ResourceLocation newKey = new ResourceLocation(key);
			if (SaveDataLocationRedirectorRegistries.LEVEL_CAPABILITY_MAPPING.containsKey(newKey))
				newKey = SaveDataLocationRedirectorRegistries.LEVEL_CAPABILITY_MAPPING.get(newKey);
			if (SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.containsKey(newKey.getNamespace()))
				newKey = new ResourceLocation(SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.get(newKey.getNamespace()), newKey.getPath());
			if (!newKey.toString().equals(key))
			{
				NaUtilsNBTStatics.resetKey(event.getNbt(), key, newKey.toString());
			}
		}
	}
	
	/**
	 * This listener is directly called in mixin instead of by event listener. ItemStack loading is too frequent and it will not
	 * post event to prevent resource waste
	 */
	public static void doPortItems(CompoundTag originalItemNBT)
	{
		if (!NaUtilsConfigs.CACHED_ENABLES_SAVE_DATA_PORTER) return;
		if (SaveDataLocationRedirectorRegistries.ITEM_MAPPING.isEmpty() && SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.isEmpty()) return;
		if (!originalItemNBT.contains("id", Tag.TAG_STRING)) return;
		ResourceLocation key = new ResourceLocation(originalItemNBT.getString("id"));
		if (SaveDataLocationRedirectorRegistries.ITEM_MAPPING.containsKey(key))
			key = SaveDataLocationRedirectorRegistries.ITEM_MAPPING.get(key);
		if (SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.containsKey(key.getNamespace()))
			key = new ResourceLocation(SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.get(key.getNamespace()), key.getPath());
		if (!key.toString().equals(originalItemNBT.getString("id")))
			originalItemNBT.putString("id", key.toString());
	}

	/**
	 * Directly called from mixin.
	 */
	public static void doPortEntityTypes(CompoundTag original)
	{
		if (!NaUtilsConfigs.CACHED_ENABLES_SAVE_DATA_PORTER) return;
		if (SaveDataLocationRedirectorRegistries.ENTITY_TYPE_MAPPING.isEmpty() && SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.isEmpty()) return;
		if (!original.contains("id", Tag.TAG_STRING)) return;
		ResourceLocation key = new ResourceLocation(original.getString("id"));
		if (SaveDataLocationRedirectorRegistries.ENTITY_TYPE_MAPPING.containsKey(key))
			key = SaveDataLocationRedirectorRegistries.ENTITY_TYPE_MAPPING.get(key);
		if (SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.containsKey(key.getNamespace()))
			key = new ResourceLocation(SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.get(key.getNamespace()), key.getPath());
		if (!key.toString().equals(original.getString("id")))
			original.putString("id", key.toString());
	}
	
}
