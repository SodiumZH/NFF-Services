package net.sodiumstudio.nautils.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * Posted when a {@code LivingEntity} dies, about to drop loot and check if it's killed by a player.
 * If it's considered as killed by a player, it will drop player-only loot.
 * <p>This event has result. Default = no change; Allow = always yes; Deny = always no.
 */
@HasResult
public class LootCheckPlayerKillEvent extends LivingEvent
{
	public final DamageSource damageSource;
	public final boolean original;
	public LootCheckPlayerKillEvent(LivingEntity entity, DamageSource dmg, boolean original)
	{
		super(entity);
		this.damageSource = dmg;
		this.original = original;
	}
}
