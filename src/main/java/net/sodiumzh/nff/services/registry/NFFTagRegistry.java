package net.sodiumzh.nff.services.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nfu.util.NFUTagStatics;

public class NFFTagRegistry
{
	/**
	 * Mobs with this tag will not attack NFF-tamed mobs unless attacked.
	 */
	public static final TagKey<EntityType<?>> NEUTRAL_TO_NFF_MOBS =
		NFUTagStatics.createEntityTypeTag(NFFServices.MOD_ID, "neutral_to_nff_mobs");

	/**
	 * Mobs with this tag will never attack NFF-tamed mobs, even if attacked.
	 */
	public static final TagKey<EntityType<?>> PASSIVE_TO_NFF_MOBS =
		NFUTagStatics.createEntityTypeTag(NFFServices.MOD_ID, "passive_to_nff_mobs");

	/**
	 * Labels that the mob which extends {@link AbstractGolem} should not be affected by
	 * {@link INFFTamed#golemAttitude()}. For mobs not designed as golems but extend golem class.
	 */
	public static final TagKey<EntityType<?>> IGNORES_GOLEM_ATTITUDE =
		NFUTagStatics.createEntityTypeTag(NFFServices.MOD_ID, "ignores_golem_attitude");

	/**
	 * This only affects NFF mob initial type check and labels that
	 * this mob could be converted from a pig with capability of INFFTamed. As INFFTamed
	 * capability is not available now, this tag is totally useless now.
	 */
	public static final TagKey<EntityType<?>> COULD_BE_FROM_PIG =
		NFUTagStatics.createEntityTypeTag(NFFServices.MOD_ID, "could_be_from_pig");
}
