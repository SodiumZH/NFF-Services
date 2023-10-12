package net.sodiumstudio.befriendmobs.entity.capability;

import java.util.ArrayList;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.sodiumstudio.befriendmobs.registry.BMCaps;

public interface CLivingEntityDelayActionHandler
{
	
	public HashSet<DelayActionEntry> getActions();
	
	public void tick();
	
	public LivingEntity getEntity();
	
	public void addAction(int delayTicks, Runnable action);
	
	public static class DelayActionEntry
	{
		protected int countdownTicks;
		protected final Runnable action;
		public DelayActionEntry(int delayTicks, Runnable action)
		{
			this.countdownTicks = delayTicks;
			this.action = action;
		}
	}
	
	public static class Impl implements CLivingEntityDelayActionHandler
	{
		protected final LivingEntity living;
		protected final HashSet<DelayActionEntry> actions;
		
		public Impl(LivingEntity living)
		{
			this.living = living;
			this.actions = new HashSet<>();
		}

		@Override
		public HashSet<DelayActionEntry> getActions() {
			return actions;
		}

		@Override
		public void tick() {
			ArrayList<DelayActionEntry> toRemove = new ArrayList<>();
			for (var entry: actions)
			{
				if (entry.countdownTicks <= 0)
				{
					entry.action.run();
					toRemove.add(entry);
				}
				else entry.countdownTicks--;
			}
			for (var entry: toRemove)
			{
				actions.remove(entry);
			}
		}

		@Override
		public LivingEntity getEntity() {
			return living;
		}

		@Override
		public void addAction(int delayTicks, Runnable action) {
			actions.add(new DelayActionEntry(delayTicks, action));
		}
	}
	
	public static class Prvd implements ICapabilityProvider
	{

		protected CLivingEntityDelayActionHandler cap;
		
		public Prvd(LivingEntity living)
		{
			cap = new CLivingEntityDelayActionHandler.Impl(living);
		}
		
		@Override
		public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
			if(cap == BMCaps.CAP_DELAY_ACTION_HANDLER && cap != null)
				return LazyOptional.of(() -> {return this.cap;}).cast();
			else
				return LazyOptional.empty();
		}
		
	}
}
