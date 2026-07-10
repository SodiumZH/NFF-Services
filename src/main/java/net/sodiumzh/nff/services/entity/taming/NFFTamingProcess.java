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
import net.sodiumzh.nff.services.event.BMHooks;
import net.sodiumzh.nff.services.eventlistener.NFFEntityEventListeners;
import net.sodiumzh.nff.services.registry.NFFEntityComponents;
import net.sodiumzh.nff.services.registry.NFFItemRegistry;
import net.sodiumzh.nfu.entity.anger.MobAngerReason;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;
import net.sodiumzh.nfu.entity.component.preset.EntityTimerComponent;
import net.sodiumzh.nfu.entity.taming.ITamingProcess;
import net.sodiumzh.nfu.math.ThreadSafeRandomSource;
import net.sodiumzh.nfu.object.Upcastable;
import net.sodiumzh.nfu.util.NFUEntityStatics;
import net.sodiumzh.nfu.util.NFUMiscStatics;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public abstract class NFFTamingProcess implements ITamingProcess<Mob>, Upcastable<NFFTamingProcess>
{

	protected static final UUID EMPTY_UUID = new UUID(0L, 0L);
	protected static final RandomSource RND = new ThreadSafeRandomSource();
	protected MobAngerRules angerRules = MobAngerRules.ATTACKER_DAMAGED.get();;
	protected MobAngerRules interruptionRules = MobAngerRules.ATTACKER_DAMAGED.get();;

	public NFFTamingProcess()
	{	
	}

	/**
	 * Invoked on entity join level to initialize the tamable capability.
	 * <p>Handled in {@link NFFEntityEventListeners#onEntityJoinWorld}.
	 */
	public abstract void tamableInit(NFFTamableComponent cap);

	@Override
	public Mob doTaming(Player player, Mob target)
	{
		// Don't execute on client
		if (target.level().isClientSide())
			return null;
		// Don't execute on mobs already befriended
		if (INFFTamed.get(target).filter(t -> t.getOwnerUUID() != null).isPresent())
			return null;		

		// Check if befriendable capability is attached
		if(NFFTamableComponent.getOptional(target).isEmpty())
			throw new RuntimeException("Befriending: Target mob not having NFFTamableComponent correctly attached.");
		
		// Get new type, and do check
		@SuppressWarnings("unchecked")
		EntityType<? extends Mob> newType = NFFTamingMapping.getConvertTo((EntityType<? extends Mob>) target.getType());
		if (newType == null)
			throw new RuntimeException("Befriending: Entity type after befriending is not valid. Check if the befriendable mob has been registered to NFFTamingMapping.");
		
		// Record the old mob's some properties for initializing new mob on client UNIMPLEMENTED
		//ClientboundTamedInitPacket packet = new ClientboundTamedInitPacket(target);
		
		// Do conversion
		Mob newMob = NFUEntityStatics.replaceMob(newType, target);
		INFFTamed bm = INFFTamed.get(newMob).orElseThrow(() -> new RuntimeException("Befriending: Entity type after befriending is missing INFFTamed interface."));
		bm.setOwner(player);
        NFFTamedDataAccessor accessor = bm.getDataAccessor();
        accessor.setOwnerName(player.getName().getString());
		bm.init(player.getUUID(), target);
		bm.getAdditionalInventory().getFromMob(bm.asMob());
        accessor.generateIdentifier();
        accessor.recordEntityType();
        accessor.recordEncounteredDate();
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
	 * <p> Implemented through {@link NFFTamableComponent#tick()}.
	 */
	public final void doServerTick(Mob mob)
	{
		if (persistentIfInProcess())
		{
			NFFTamableComponent.getOptional(mob).ifPresent(c ->
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
	 * <p>Implemented through {@link NFFTamableAngerHandlerComponent#onAngryAt}</>
	* */
	@Override
	public void onAngryAt(Mob mob, Player player, MobAngerReason reason)
	{
		if (isInProcess(player, mob) && this.getInterruptionRules().getForgivingTicks(reason, mob, player) != 0)
			interrupt(player, mob, false);
	}

	@Nonnull
	@ApiStatus.NonExtendable
	public MobAngerRules getInterruptionRules() {
		return this.interruptionRules;
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
	 * Get the tamable component of a mob. It's a shortcut of {@link NFFTamableComponent#get}.
	 */
	public final NFFTamableComponent getTamable(Mob mob) {
		return NFFTamableComponent.getOptional(mob).orElse(NFFEntityComponents.TAMABLE.get().create(mob));
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

	/**
	 * Set rules making the mob angry. Note that this rule doesn't impact whether the friending process
	 * will be interrupted when getting angry. To set interruption rules, use {@code setInterruptingRules} or
	 * {@code setAngerAndInterruptionRules}.
	 */
	@ApiStatus.NonExtendable
	public NFFTamingProcess setAngerRules(MobAngerRules rules) {
		this.angerRules = rules;
		return this;
	}

	/**
	 * Set rules that make the progress interrupted when getting angry.
	 * <p>Note that the mob will not get angry for reasons not included in the anger rules (accessed by
	 * {@code getAngerRules} and {@code setAngerRules}), even if it's in the interruption rules.
	 * <p>To set anger rules, use {@code setAngerRules} or {@code setAngerAndInterruptionRules}.
	 */
	@ApiStatus.NonExtendable
	public NFFTamingProcess setInterruptionRules(MobAngerRules rules) {
		this.interruptionRules = rules;
		return this;
	}

	/**
	 * Set both anger rules and interruption rules. Equivalent to calling {@code setAngerRules} and {@code setInterruptionRules}
	 * simultaneously.
	 */
	@ApiStatus.NonExtendable
	public NFFTamingProcess setAngerAndInterruptionRules(MobAngerRules anger, MobAngerRules interruption) {
		this.angerRules = anger;
		this.interruptionRules = interruption;
		return this;
	}

	/**
	 * Set both anger rules and interruption rules to the given value (i.e. set that the mob will get angry for given
	 * reasons, and always interrupt the progress when getting angry).
	 */
	@ApiStatus.NonExtendable
	public NFFTamingProcess setAngerAndInterruptionRules(MobAngerRules rules) {
		return setAngerAndInterruptionRules(rules, rules);
	}

	@Override
	@ApiStatus.NonExtendable
	public MobAngerRules getAngerRules() {
		return this.angerRules;
	}

	/**
	 * Up-cast self to a specified subclass. For registration convenience. Take care of type hierarchy.
	 * @throws ClassCastException If class mismatches.
	 */
	@SuppressWarnings("unchecked")
	public final <T extends NFFTamingProcess> T castUnsafe(Class<T> clazz) {
		return (T)this;
	}

	/**
	 * Up-cast self to a context-determined subclass. For registration convenience. Take care of type hierarchy.
	 * @throws ClassCastException If class mismatches.
	 */
	@SuppressWarnings("unchecked")
	public final <T extends NFFTamingProcess> T castUnsafe() {
		return (T)this;
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NFFServices.MOD_ID)
	public static class EventListeners {
		@SubscribeEvent
		public static void notifyTimerExpire(EntityTimerComponent.ExpireEvent event) {
			if (event.getComponent().equals(NFFTamableComponent.getOptional(event.getEntity()).map(NFFTamableComponent::getTimerComponent).orElse(null))
				&& event.getEntity() instanceof Mob mob)
			{
				NFFTamableComponent tamableComponent = NFFTamableComponent.getOptional(event.getEntity()).orElseThrow();
				if (event.isUUIDSpecific()) {
					tamableComponent.getTamingProcess().onPlayerTimerExpire(mob, event.getUUID().orElseThrow(), event.getName());
				}
				else {
					tamableComponent.getTamingProcess().onGeneralTimerExpire(mob, event.getName());
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
