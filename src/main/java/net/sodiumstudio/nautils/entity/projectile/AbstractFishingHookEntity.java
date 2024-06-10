package net.sodiumstudio.nautils.entity.projectile;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.sodiumstudio.nautils.NaReflectionUtils;

/**
 * A wrapped vanilla FishingHook in 1.18.2 for support of subclasses.
 * In 1.18.2 vanilla FishingHook uses a private check function for stopping fishing that only supports vanilla FishingRod.
 */
public abstract class AbstractFishingHookEntity extends FishingHook
{
	
	/**
	 * Same as {@link FishingHook#FishingHookState}.
	 * Redefined as this parameter is only called in tick.
	 */
	protected FishHookState currentState = FishHookState.FLYING;
	
	/**
	 * Same as {@link FishingHook#syncronizedRandom}.
	 * Redefined as this parameter is just a Random.
	 */
	protected Random syncronizedRandom = new Random();
	
	/**
	 * Same as {@link FishingHook#outOfWaterTime}.
	 * Redefined as this parameter is only called in tick.
	 */
	protected int outOfWaterTime = 0;
	
	
	public AbstractFishingHookEntity(Player pPlayer, Level pLevel, int pLuck, int pLureSpeed)
	{
		super(pPlayer, pLevel, pLuck, pLureSpeed);
	}

	public AbstractFishingHookEntity(EntityType<? extends FishingHook> pEntityType, Level pLevel)
	{
		super(pEntityType, pLevel);
	}

	/**
	 * Get the corresponding fishing rod type.
	 * If non-null, it will be automatically removed when player is not holding the correct fishing rod.
	 */
	@Nonnull
	protected abstract FishingRodItem getFishingRodType();
	
	/**
	 * Get {@link FishingHook#luck} parameter. 
	 */
	protected int getLuck()
	{
		return NaReflectionUtils.forceGet(this, FishingHook.class, "f_37096_").cast();	// FishingHook#luck
	}
	
	/**
	 * Get {@link FishingHook#nibble} parameter.
	 */
	protected int getNibble()
	{
		return NaReflectionUtils.forceGet(this, FishingHook.class, "f_37089_").cast();	// FishingHook#nibble
	}
	
	/**
	 * Override of {@link FishingHook#shouldStopFishing}.
	 */
	protected boolean shouldStopFishing(Player pPlayer) {
		ItemStack itemstack = pPlayer.getMainHandItem();
		ItemStack itemstack1 = pPlayer.getOffhandItem();
		boolean flag = getFishingRodType() != null ? itemstack.is(getFishingRodType()) : true;
		boolean flag1 = getFishingRodType() != null ? itemstack1.is(getFishingRodType()) : true;
		if (!pPlayer.isRemoved() && pPlayer.isAlive() && (flag || flag1) && !(this.distanceToSqr(pPlayer) > 1024.0D))
		{
			return false;
		} else
		{
			this.discard();
			return true;
		}
	}
	
	/**
	 * Same operation to {@link FishingHook#retrieve}.
	 */
	@Override
	public int retrieve(ItemStack pStack)
	{
		Player player = this.getPlayerOwner();
		if (!this.level.isClientSide && player != null && !this.shouldStopFishing(player))
		{
			int i = 0;
			net.minecraftforge.event.entity.player.ItemFishedEvent event = null;
			if (this.getHookedIn() != null)
			{
				this.pullEntity(this.getHookedIn());
				CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer) player, pStack, this,
						Collections.emptyList());
				this.level.broadcastEntityEvent(this, (byte) 31);
				i = this.getHookedIn() instanceof ItemEntity ? 3 : 5;
			} else if (this.getNibble() > 0)
			{
				LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) this.level))
						.withParameter(LootContextParams.ORIGIN, this.position())
						.withParameter(LootContextParams.TOOL, pStack)
						.withParameter(LootContextParams.THIS_ENTITY, this).withRandom(this.random)
						.withLuck((float) this.getLuck() + player.getLuck());
				lootcontext$builder.withParameter(LootContextParams.KILLER_ENTITY, this.getOwner())
						.withParameter(LootContextParams.THIS_ENTITY, this);
				LootTable loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING);
				List<ItemStack> list = loottable
						.getRandomItems(lootcontext$builder.create(LootContextParamSets.FISHING));
				event = new net.minecraftforge.event.entity.player.ItemFishedEvent(list, this.onGround ? 2 : 1, this);
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
				if (event.isCanceled())
				{
					this.discard();
					return event.getRodDamage();
				}
				CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer) player, pStack, this, list);

				for (ItemStack itemstack : list)
				{
					ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(),
							itemstack);
					double d0 = player.getX() - this.getX();
					double d1 = player.getY() - this.getY();
					double d2 = player.getZ() - this.getZ();
					//double d3 = 0.1D;
					itementity.setDeltaMovement(d0 * 0.1D,
							d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
					this.level.addFreshEntity(itementity);
					player.level.addFreshEntity(new ExperienceOrb(player.level, player.getX(), player.getY() + 0.5D,
							player.getZ() + 0.5D, this.random.nextInt(6) + 1));
					if (itemstack.is(ItemTags.FISHES))
					{
						player.awardStat(Stats.FISH_CAUGHT, 1);
					}
				}

				i = 1;
			}

			if (this.onGround)
			{
				i = 2;
			}

			this.discard();
			return event == null ? i : event.getRodDamage();
		} else
		{
			return 0;
		}
	}
	
	// ==== Some parameters to access with reflection for tick.

	protected void setHookedEntity(@Nullable Entity entity)
	{
		NaReflectionUtils.forceInvoke(this, FishingHook.class, "m_150157_", Entity.class, entity);
	}
	
	protected int getLife()
	{
		return NaReflectionUtils.forceGet(this, FishingHook.class, "f_37103_").cast();
	}
	
	protected void setLife(int value)
	{
		NaReflectionUtils.forceSet(this, FishingHook.class, "f_37103_", value);
	}
	
	protected boolean getOpenWater()
	{
		return NaReflectionUtils.forceGet(this, FishingHook.class, "f_37093_").cast();
	}
	
	protected void setOpenWater(boolean value)
	{
		NaReflectionUtils.forceSet(this, FishingHook.class, "f_37093_", value);
	}
	
	protected int getTimeUntilHooked()
	{
		return NaReflectionUtils.forceGet(this, FishingHook.class, "f_37091_").cast();
	}
	
	protected boolean getBiting()
	{
		return NaReflectionUtils.forceGet(this, FishingHook.class, "f_37099_").cast();	
	}
	
	/**
	 * Same operation to {@link FishingHook#tick}.
	 */
	@Override
	public void tick() {
		this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level.getGameTime());
		this.projectileTick();
		Player player = this.getPlayerOwner();
		if (player == null)
		{
			this.discard();
		} else if (this.level.isClientSide || !this.shouldStopFishing(player))
		{
			if (this.onGround)
			{
				this.setLife(this.getLife() + 1);
				if (this.getLife() >= 1200)
				{
					this.discard();
					return;
				}
			} else
			{
				this.setLife(0);
			}

			float f = 0.0F;
			BlockPos blockpos = this.blockPosition();
			FluidState fluidstate = this.level.getFluidState(blockpos);
			if (fluidstate.is(FluidTags.WATER))
			{
				f = fluidstate.getHeight(this.level, blockpos);
			}

			boolean flag = f > 0.0F;
			if (this.currentState == FishHookState.FLYING)
			{
				if (this.getHookedIn() != null)
				{
					this.setDeltaMovement(Vec3.ZERO);
					this.currentState = FishHookState.HOOKED_IN_ENTITY;
					return;
				}

				if (flag)
				{
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
					this.currentState = FishHookState.BOBBING;
					return;
				}

				NaReflectionUtils.forceInvoke(this, FishingHook.class, "m_37171_");	// this.checkCollision();
				
			} else
			{
				if (this.currentState == FishHookState.HOOKED_IN_ENTITY)
				{
					if (this.getHookedIn() != null)
					{
						if (!this.getHookedIn().isRemoved() && this.getHookedIn().level.dimension() == this.level.dimension())
						{
							this.setPos(this.getHookedIn().getX(), this.getHookedIn().getY(0.8D), this.getHookedIn().getZ());
						} else
						{
							this.setHookedEntity(null);
							this.currentState = FishHookState.FLYING;
						}
					}

					return;
				}

				if (this.currentState == FishHookState.BOBBING)
				{
					Vec3 vec3 = this.getDeltaMovement();
					double d0 = this.getY() + vec3.y - (double) blockpos.getY() - (double) f;
					if (Math.abs(d0) < 0.01D)
					{
						d0 += Math.signum(d0) * 0.1D;
					}

					this.setDeltaMovement(vec3.x * 0.9D, vec3.y - d0 * (double) this.random.nextFloat() * 0.2D,
							vec3.z * 0.9D);
					if (this.getNibble() <= 0 && this.getTimeUntilHooked() <= 0)
					{
						this.setOpenWater(true);
					} else
					{
						this.setOpenWater(this.getOpenWater() && this.outOfWaterTime < 10 && 
								NaReflectionUtils.forceInvokeRetVal(this, FishingHook.class, "m_37158_", BlockPos.class, blockpos).castTo(Boolean.class));//this.calculateOpenWater(blockpos);
					}

					if (flag)
					{
						this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
						if (this.getBiting())
						{
							this.setDeltaMovement(this.getDeltaMovement().add(0.0D,
									-0.1D * (double) this.syncronizedRandom.nextFloat()
											* (double) this.syncronizedRandom.nextFloat(),
									0.0D));
						}

						if (!this.level.isClientSide)
						{
							NaReflectionUtils.forceInvoke(this, FishingHook.class, "m_37145_", BlockPos.class, blockpos);//this.catchingFish(blockpos);
						}
					} else
					{
						this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
					}
				}
			}

			if (!fluidstate.is(FluidTags.WATER))
			{
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
			this.updateRotation();
			if (this.currentState == FishHookState.FLYING && (this.onGround || this.horizontalCollision))
			{
				this.setDeltaMovement(Vec3.ZERO);
			}

			//double d1 = 0.92D;
			this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
			this.reapplyPosition();
		}
	}
	
	// Projectile interface
	
	protected boolean getHasBeenShot()
	{
		return NaReflectionUtils.forceGet(this, Projectile.class, "f_150164_").cast();
	}
	
	protected void setHasBeenShot(boolean value)
	{
		NaReflectionUtils.forceSet(this, Projectile.class, "f_150164_", value);
	}
	
	protected boolean getLeftOwner()
	{
		return NaReflectionUtils.forceGet(this, Projectile.class, "f_37246_").cast();
	}
	
	protected void setLeftOwner(boolean value)
	{
		NaReflectionUtils.forceSet(this, Projectile.class, "f_37246_", value);
	}
	
	protected void projectileTick()
	{
	      if (!this.getHasBeenShot()) {
	          this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.blockPosition());
	          this.setHasBeenShot(true);
	       }

	       if (!this.getLeftOwner()) {
	          this.setLeftOwner(NaReflectionUtils.forceInvokeRetVal(this, Projectile.class, "m_37276_").cast()/*this.checkLeftOwner()*/);
	       }
	       // Entity#tick
	       this.baseTick();
	}
	
	public static enum FishHookState
	{
		FLYING, 
		HOOKED_IN_ENTITY, 
		BOBBING;
	}

}
