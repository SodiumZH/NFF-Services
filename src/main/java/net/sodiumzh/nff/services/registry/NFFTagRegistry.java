package net.sodiumzh.nff.services.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sodiumzh.nautils.statics.NaUtilsTagStatics;
import net.sodiumzh.nff.services.NFFServices;

public class NFFTagRegistry
{
	
	public static final TagKey<EntityType<?>> NEUTRAL_TO_NFF_MOBS = NaUtilsTagStatics.createEntityTypeTag(NFFServices.MOD_ID, "neutral_to_nff_mobs");
}
