package net.sodiumzh.nff.services.entity.taming;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.capability.CNFFTamable;
import net.sodiumzh.nff.services.entity.capability.CNFFTamableImpl;
import net.sodiumzh.nff.services.event.BMHooks;
import net.sodiumzh.nff.services.eventlistener.NFFEntityEventListeners;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;
import net.sodiumzh.nff.services.registry.NFFItemRegistry;
import net.sodiumzh.nfu.capability.CEntityTimerCapability;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;
import net.sodiumzh.nfu.entity.taming.ITamingProcess;
import net.sodiumzh.nfu.math.ThreadSafeRandomSource;
import net.sodiumzh.nfu.util.NFUEntityStatics;
import net.sodiumzh.nfu.util.NFUMiscStatics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public abstract class NFFTamingProcess implements ITamingProcess<Mob>
{

	protected static final UUID EMPTY_UUID = new UUID(0L, 0L);
	protected static final RandomSource RND = new ThreadSafeRandomSource();

	public NFFTamingProcess()
	{	
	}

	/**
	 * Invoked on entity join level to initialize the tamable capability.
	 * <p>Handled in {@link NFFEntityEventListeners#onEntityJoinWorld}.
	 */
	public abstract void tamableInit(CNFFTamable cap);

	@Override
	public Mob doTaming(Player player, Mob target)
	{
		// Don't execute on client
		if (target.level().isClientSide())
			return null;
		// Don't execute on mobs already befriended
		if (target instanceof INFFTamed)
			return null;		

		// Check if befriendable capability is attached
		if(!target.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).isPresent())
			throw new RuntimeException("Befriending: Target mob not having CNFFTamable capability attached.");
		
		// Get new type, and do check
		@SuppressWarnings("unchecked")
		EntityType<? extends Mob> newType = NFFTamingMapping.getConvertTo((EntityType<? extends Mob>) target.getType());
		if (newType == null)
			throw new RuntimeException("Befriending: Entity type after befriending is not valid. Check if the befriendable mob has been registered to NFFTamingMapping.");
		
		// Record the old mob's some properties for initializing new mob on client UNIMPLEMENTED
		//ClientboundTamedInitPacket packet = new ClientboundTamedInitPacket(target);
		
		// Do conversion
		Mob newMob = NFUEntityStatics.replaceMob(newType, target);
		if(!(newMob instanceof INFFTamed))
			throw new RuntimeException("Befriending: Entity type after befriending not implementing INFFTamed interface.");
		INFFTamed bm = (INFFTamed)newMob;
		bm.setOwner(player);
		bm.getData().setOwnerName(player.getName().getString());
		bm.init(player.getUUID(), target);
		bm.setInventoryFromMob();
		bm.getData().generateIdentifier();
		bm.getData().recordEntityType();
		bm.getData().recordEncounteredDate();
		//NaUtilsDebugStatics.debugPrintToScreen("Mob \""+target.getDisplayName().getString()+"\" befriended", player);
		BMHooks.Befriending.onMobBefriended(target, bm);
		bm.setInit();
		// Sync the recorded properties UNIMPLEMENTED
		//NaUtilsNetworkStatics.sendToAllPlayers(newBefMob.asMob().level, NFFChannels.BM_CHANNEL, packet);
		return bm.asMob();
	}

	/**
	 * Invoked on mob tick on server.
	 * <p> For custom tick actions, override {@code serverTick} instead.
	 * <p> Implemented through {@link CNFFTamableImpl#tick()}.
	 */
	public final void doServerTick(Mob mob)
	{
		if (persistentIfInProcess())
		{
			mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent(c -> 
			{
				if (this.isInAnyProcess(mob))
					c.setForcePersistent(true);
			});
		}
		serverTick(mob);
	}

	@Override
	public abstract void interrupt(Player player, Mob mob, boolean isQuiet);

	/** Execute when the mob attacks the player in taming process with it, no action by default.
	 * <p>This method doesn't check if the mob got angry. For behaviors based on anger, override {@code onAngryAt} instead.
	 * <p>Implemented through {@link NFFEntityEventListeners#onLivingHurt}.</>
	 */
	@Override
	public void onAttackProcessingPlayer(Mob mob, Player player, double damage) {
	}


	/** Execute when the mob is attacked by the player in taming process with it, no action by default.
	 * <p>This method doesn't check if the mob got angry. For behaviors based on anger, override {@code onAngryAt} instead.
	 * <p>Implemented through {@link NFFEntityEventListeners#onLivingHurt}.</>
	 * */
	@Override
	public void onAttackedByProcessingPlayer(Mob mob, Player player, double damage) {
	}

	/** Execute when the mob added player into hatred list
	* Interrupt if attacked by default
	 * <p>Implemented through {@link CNFFTamableImpl#onAngryAt}</>
	* */
	@Override
	public void onAngryAt(Mob mob, Player player, MobAngerReason reason)
	{
		if (isInProcess(player, mob) && this.getInterruptingAngerRules().getForgivingTicks(reason, mob, player) != 0)
			interrupt(player, mob, false);
	}

	@Nonnull
	public MobAngerRules getInterruptingAngerRules() {
		return MobAngerRules.ATTACKER_DAMAGED.get();
	}

	@Override
	public boolean dontInterruptOnPlayerDie() {
		return false;
	}

	@Override
	public boolean persistentIfInProcess() {
		return true;
	}

	/**
	 * Invoked when a general timer of a mob expires.
	 */
	public void onGeneralTimerExpire(Mob mob, String key) {}

	/**
	 * Invoked when a player timer of a mob expires.
	 */
	public void onPlayerTimerExpire(Mob mob, UUID playerUUID, String key) {}

	/**
	 * Get the tamable capability of a mob. It's a shortcut of {@link CNFFTamable#get}.
	 */
	public final CNFFTamable getTamable(Mob mob) {
		return CNFFTamable.get(mob);
	}

	/**
	 * Print info if holding the debug sign.
	 */
	protected void debugPrint(Player printTo, String info) {
		if (printTo.getOffhandItem().is(NFFItemRegistry.NFF_DEBUG_SIGN.get()))
			NFUMiscStatics.printToScreen(info, printTo);
	}

	public boolean allowsToProgressOnRiding(Mob mob, @Nullable Entity mount) {
		return mount instanceof Mob;	// Allow to tame on horse
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NFFServices.MOD_ID)
	public static class EventListeners {
		@SubscribeEvent
		public static void notifyTimerExpire(CEntityTimerCapability.ExpireEvent event) {
			if (event.getCapability() instanceof CNFFTamable tamable) {
				var playerInfo = CNFFTamable.parsePlayerSpecificTimerKey(event.getKey());
				playerInfo.ifPresent(info -> tamable.getTamingProcess().onPlayerTimerExpire(tamable.getEntity(), info.getA(), info.getB()));
				if (playerInfo.isEmpty()) {
					tamable.getTamingProcess().onGeneralTimerExpire(tamable.getEntity(), event.getKey());
				}
			}
		}

		@SubscribeEvent
		public static void onMobTick(LivingEvent.LivingTickEvent event) {
			if (event.getEntity() instanceof Mob mob && !event.getEntity().level().isClientSide && mob.isPassenger())
			{
				NFFTamingProcess proc = NFFTamingMapping.getProcess(mob);
				if (proc != null && proc.isInAnyProcess(mob) && !proc.allowsToProgressOnRiding(mob, mob.getVehicle()))
					mob.stopRiding();
			}
		}

		@SubscribeEvent
		public static void onMobMount(EntityMountEvent event) {
			if (event.isMounting() && !event.getEntity().level().isClientSide && event.getEntity() instanceof Mob mob)
			{
				NFFTamingProcess proc = NFFTamingMapping.getProcess(mob);
				if (proc != null && proc.isInAnyProcess(mob) && !proc.allowsToProgressOnRiding(mob, mob.getVehicle()))
					event.setCanceled(true);
			}

		}
	}
}
