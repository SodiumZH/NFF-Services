package net.sodiumzh.nff.services.entity.capability;

import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nautils.statics.NaUtilsItemStatics;
import net.sodiumzh.nautils.statics.NaUtilsParticleStatics;

import javax.annotation.Nullable;

public interface CHealingHandler extends INBTSerializable<IntTag>
{

	public LivingEntity getOwner();

	public void setOwner(LivingEntity owner);
	
	@Override
	public default IntTag serializeNBT() 
	{
		IntTag tag = IntTag.valueOf(getCooldown());
		return tag;
	}
	
	@Override
	public default void deserializeNBT(IntTag tag)
	{
		this.setCooldown(tag.getAsInt());
	}

	public default boolean applyHealingItem(ItemStack stack, float value, boolean consume, int cooldown, @Nullable Player player)
	{
		if (getOwner().getHealth() < getOwner().getMaxHealth() && getCooldown() == 0)
		{
			ApplyHealingItemEvent event = new ApplyHealingItemEvent(getOwner(), stack, value, cooldown);
			ItemStack cpy = stack.copy();
			float oldHP = getOwner().getHealth();
			boolean canceled = MinecraftForge.EVENT_BUS.post(event);
			if (!canceled)
			{
				if (consume)
				{
					ItemStack remaining = stack.getItem().getContainerItem(stack);
					stack.shrink(1);
					if (player != null)
						NaUtilsItemStatics.giveOrDrop(player, remaining);
				}
				getOwner().heal(event.healValue);
				if (event.sendDefaultParticles)
					sendParticlesOnSuccess();
				if (event.cooldown > 0)
					setCooldown(event.cooldown);
				MinecraftForge.EVENT_BUS.post(new HealingSucceededEvent(getOwner(), cpy, getOwner().getHealth() - oldHP, player));
				return true;
			}
		}
		HealingFailedEvent event = new HealingFailedEvent(getOwner(), stack, player);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.sendDefaultParticles)
			sendParticlesOnFailure();
		return false;
	}
	
	public default void sendParticlesOnSuccess()
	{
		//NaUtilsEntityStatics.sendGlintParticlesToLivingDefault(getOwner());
		NaUtilsParticleStatics.sendGlintParticlesToEntityDefault(getOwner());
	}
	
	public default void sendParticlesOnFailure()
	{
		//NaUtilsEntityStatics.sendSmokeParticlesToLivingDefault(getOwner());
		NaUtilsParticleStatics.sendSmokeParticlesToEntityDefault(getOwner());
	}
	
	// Get expected cooldown time each healing 
	/**
	 * @deprecated Now input in {@code applyHealingItem}
	 */
	@Deprecated
	public int getHealingCooldownTicks();
	
	// Get the current cooldown ticks
	public int getCooldown();
	
	public void setCooldown(int value);
	
	public default void updateCooldown()
	{
		if (getCooldown() > 0)
			setCooldown(getCooldown() - 1);
	}

	@Cancelable
	public static class ApplyHealingItemEvent extends Event
	{
		/** Target living entity. */
		public final LivingEntity living;
		/** Item stack to apply. */
		public final ItemStack stack;
		/** HP value to heal. Settable. */
		public float healValue;
		/** Whether it should send default particles (glint). Settable. */
		public boolean sendDefaultParticles = true;
		/** Cooldown ticks. Settable. */
		public int cooldown;
		
		public ApplyHealingItemEvent(LivingEntity living, ItemStack stack, float healValue, int cooldown)
		{
			this.living = living;
			this.stack = stack;
			this.healValue = healValue;
			this.cooldown = cooldown;
		}
	}
	
	/**
	 * Fired when applying healing item to living succeeded.
	 */
	public static class HealingSucceededEvent extends Event
	{
		/** Target living entity. */
		public final LivingEntity living;
		/** Item stack applied. It's a copy of the item stack before applying. */
		public final ItemStack stack;
		/** HP value actually applied. */
		public final float healedValue;
		/** Player performing this healing action. **/
		@Nullable
		public final Player player;

		public HealingSucceededEvent(LivingEntity living, ItemStack stack, float healedValue, @Nullable Player player)
		{
			this.living = living;
			this.stack = stack;
			this.healedValue = healedValue;
			this.player = player;
		}
	}
	
	/**
	 * Fired when applying healing item to living failed, including because of cancellation of {@link CHealingHandler.ApplyHealingItemEvent}.
	 */
	public static class HealingFailedEvent extends Event
	{
		/** Target living entity. */
		public final LivingEntity living;
		/** Item stack to apply. */
		public final ItemStack stack;
		/** Whether it should send default particles (smoke). Settable. */
		public boolean sendDefaultParticles = true;
		/** Player performing this healing action. **/
		@Nullable
		public final Player player;
		
		public HealingFailedEvent(LivingEntity living, ItemStack stack, @Nullable Player player)
		{
			this.living = living;
			this.stack = stack;
			this.player = player;
		}
	}
}
