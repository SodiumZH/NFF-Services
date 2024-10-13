package net.sodiumzh.nff.services.eventlisteners;

import java.util.UUID;

import com.mojang.logging.LogUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.sodiumzh.nautils.statics.NaUtilsEntityStatics;
import net.sodiumzh.nff.services.NFFServices;
import net.sodiumzh.nff.services.entity.ai.NFFTamedMobAIState;
import net.sodiumzh.nff.services.entity.capability.CAttributeMonitor;
import net.sodiumzh.nff.services.entity.capability.CLivingEntityDelayedActionHandler;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nff.services.entity.taming.INFFTamed.DeathRespawnerGenerationType;
import net.sodiumzh.nff.services.entity.taming.INFFTamedSunSensitiveMob;
import net.sodiumzh.nff.services.entity.taming.NFFTamedStatics;
import net.sodiumzh.nff.services.entity.taming.NFFTamingMapping;
import net.sodiumzh.nff.services.entity.taming.NFFTamingProcess;
import net.sodiumzh.nff.services.entity.taming.TamableHatredReason;
import net.sodiumzh.nff.services.entity.taming.TamableInteractArguments;
import net.sodiumzh.nff.services.entity.taming.TamableInteractionResult;
import net.sodiumzh.nff.services.event.entity.NFFTamedDropRespawnerOnDyingEvent;
import net.sodiumzh.nff.services.event.entity.ai.NFFTamedChangeAiStateEvent;
import net.sodiumzh.nff.services.inventory.NFFTamedMobInventory;
import net.sodiumzh.nff.services.item.NFFMobRespawnerInstance;
import net.sodiumzh.nff.services.item.NFFMobRespawnerItem;
import net.sodiumzh.nff.services.item.capability.CItemStackMonitor;
import net.sodiumzh.nff.services.registry.NFFCapRegistry;
import net.sodiumzh.nff.services.registry.NFFItemRegistry;
import net.sodiumzh.nff.services.registry.NFFTagRegistry;
import org.apache.commons.lang3.mutable.MutableObject;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = NFFServices.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NFFEntityEventListeners
{

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		if (event.isCanceled())
			return;
		Entity target = event.getTarget();
		Player player = event.getEntity();
		MutableObject<InteractionResult> result = new MutableObject<InteractionResult>(InteractionResult.PASS);
		boolean isClientSide = event.getSide() == LogicalSide.CLIENT;
		boolean isMainHand = event.getHand() == InteractionHand.MAIN_HAND;
		MutableObject<Boolean> shouldPostInteractEvent = new MutableObject<Boolean>(Boolean.FALSE);

		// Mob interaction start //
		if (target != null && target instanceof Mob) 
		{
	
			Mob mob = (Mob)target;
			@SuppressWarnings("unchecked")
			EntityType<Mob> type = (EntityType<Mob>) mob.getType();

			
			// Do debug actions and skip when holding debug items
			/*if (NaUtilsTagStatics.hasTag(player.getMainHandItem().getItem(), "befriendmobs", "debug_tools"))
			{
				if (!isClientSide && isMainHand)
					BMDebugItemHandler.onDebugItemUsed(player, (Mob)target, player.getMainHandItem().getItem());
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.sidedSuccess(isClientSide));			
				return;		
			}
			
			// Handle befriendable mob start //
			else*/ if (mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).isPresent()
					&& !(mob instanceof INFFTamed)) {
				mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) -> 
				{

					TamableInteractionResult res = NFFTamingMapping.getHandler(type).handleInteract(
							TamableInteractArguments.of(event.getSide(), player, mob, event.getHand()));
					if (res.nFFTamed != null) // Directly exit if befriended, as this mob is no longer valid
					{
						event.setCanceled(true);
						event.setCancellationResult(InteractionResult.sidedSuccess(isClientSide));
						return;
					} else if (res.handled)
					{
						event.setCanceled(true);
						result.setValue(InteractionResult.sidedSuccess(isClientSide));
						shouldPostInteractEvent.setValue(true);
					}
					
				});
			}
			// Handle befriendable mob end //
			// Handle befriended mob start //
			else if (mob instanceof INFFTamed bef) 
			{
				// if (!isClientSide && isMainHand)

				if (player.isShiftKeyDown() && player.getMainHandItem().getItem() == NFFItemRegistry.DEBUG_BEFRIENDER.get()) {
					bef.init(player.getUUID(), null);
					// Debug.printToScreen("Befriended mob initialized", player, living);
					result.setValue(InteractionResult.sidedSuccess(isClientSide));
				}
			}
			// Handle befriended mob end //
		}
		// Mob interaction end //

		// Server events end //
		// Client events start //
		else {
		}
		// Client events end //
		event.setCanceled(result.getValue().equals(InteractionResult.sidedSuccess(isClientSide)));
		event.setCancellationResult(result.getValue());
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingChangeTarget_Lowest(LivingChangeTargetEvent event)
	{
		/** Handle {@link CNFFTamable} AlwaysHostile feature */
		if (!event.getEntity().level.isClientSide)
		{
			event.getEntity().getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent(cap -> 
			{
				Mob mob = cap.getOwner();
				UUID alwaysHostileUUID = cap.getAlwaysHostileTo();
				Entity target = NaUtilsEntityStatics.getIfCanSee(alwaysHostileUUID, mob).orElse(null);
				if (target != null 
						&& target instanceof LivingEntity targetLiving
						&& event.getNewTarget() != target)
					event.setCanceled(true);
			});
		}
	}
	
	@SubscribeEvent
	public static void onLivingChangeTarget(LivingChangeTargetEvent event)
	{
		@SuppressWarnings("deprecation")
		LivingEntity target = event.getNewTarget();		
		// Handle mobs //
		if (target != null && event.getEntity() instanceof Mob mob)
		{ 	
	        // Handle befriended mobs //	        
	        if (mob instanceof INFFTamed bm)
	        {
	        	// Befriended mob should never attack the owner
	        	if (target == bm.getOwner())
	        		event.setNewTarget(bm.getPreviousTarget());
	        	// Befriended mob shouldn't attack owner's other befriended mobs
	        	else if (target instanceof INFFTamed tbef)
	        	{
	        		if (bm.getOwner() != null && tbef.getOwner() != null && bm.getOwner() == tbef.getOwner())
	        		{
	        			event.setNewTarget(bm.getPreviousTarget());
	        		}
	        	}
	        	// Befriended mob shouldn't attack owner's tamable animals
	        	else if (target instanceof TamableAnimal ta)
	        	{
	        		if (bm.getOwner() != null && ta.getOwner() != null && bm.getOwner() == ta.getOwner())
	        		{
	        			event.setNewTarget(bm.getPreviousTarget());
	        		}
	        	}
	        	else
	        		bm.setPreviousTarget(target);
	        	
	        }
	        // Handle befriended mobs end //
	        // Handle TamableAnimal //	
	        if (mob instanceof TamableAnimal ta)
	        {
	        	// Tamable animals shouldn't attack owner's befriended mobs
	        	if (target instanceof INFFTamed tbef)
	        	{
	        		if (ta.getOwner() != null && tbef.getOwner() != null && ta.getOwner() == tbef.getOwner())
	        		{
	        			event.setCanceled(true);
	        		}
	        	}
	        }
	        // Handle TamableAnimal end //
	        // Handle Golems //
	        if (mob instanceof AbstractGolem g)
	        {
	        	if (target instanceof INFFTamed bm)
	        	{
	        		
	        		switch (bm.golemAttitude())
	        		{
	        		case DEFAULT:
	        		{
	        			// No change
	        			break;
	        		}
	        		case NEUTRAL:
	        		{
	        			// Golems keep neutral to befriended mobs, but if it's attacked it will still attack back
		        		if (g.getLastHurtByMob() == null || !g.getLastHurtByMob().equals(target))
		        		{
		        			event.setCanceled(true);
		        		}
		        		break;
	        		}
	        		case PASSIVE:
	        		{
	        			// Always cancel
	        			event.setCanceled(true);
	        			break;
	        		}
	        		case CUSTOM:
	        		{
	        			// Use custom config
	        			if (!bm.shouldGolemAttack(g))
	        			{
	        				event.setCanceled(true);
	        			}
	        			break;
	        		}
	        		default:
	        		{
	        			throw new RuntimeException();
	        		}
	        		}
	        	}
	        }
	        // Handle Golems End
	        // Handle tag befriendmobs:neutral_to_bm_mobs
	        if (mob.getType().is(NFFTagRegistry.NEUTRAL_TO_NFF_MOBS) && target instanceof INFFTamed && mob.getLastHurtByMob() != target)
	        	event.setCanceled(true);
		}
		// Handle mobs end //
	}	
	
	/** Here it's still necessary because other mods may reset target here overriding the AlwaysHostile feature*/
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingSetAttackTarget_Lowest(LivingSetAttackTargetEvent event)
	{
		
		if (!event.getEntity().level.isClientSide)
		{
			// Handle befriendable mobs //
			event.getEntity().getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent(cap -> 
			{
				/** Handle {@link CNFFTamable} AlwaysHostile feature */
				Mob mob = cap.getOwner();
				UUID alwaysHostileUUID = cap.getAlwaysHostileTo();
				Entity target = NaUtilsEntityStatics.getIfCanSee(alwaysHostileUUID, mob).orElse(null);
				if (target != null && target instanceof LivingEntity targetLiving)
					NaUtilsEntityStatics.forceSetTarget(mob, targetLiving);
				
        		// Add hatred only when settring to player
        		if (mob.getTarget() instanceof Player player)
        			cap.addHatredWithReason(player, TamableHatredReason.SET_TARGET);	   
			});
		}
		
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		if (event.isCanceled())
			return;
		if (!event.getEntity().level.isClientSide) {
			if (event.getEntity() instanceof INFFTamed bef) {
				if (MinecraftForge.EVENT_BUS.post(new NFFTamedDeathEvent(bef, event.getSource()))) {
					event.setCanceled(true);
					return;
				}
				// Befriended mobs should not kill each other with same owner, or get killed by
				// owner-tamed animals
				else if (event.getSource().getEntity() instanceof INFFTamed srcBef) {
					if (srcBef.getOwner() != null && bef.getOwner() != null && srcBef.getOwner() == bef.getOwner()) {
						bef.asMob().setHealth(1.0f);
						bef.asMob().invulnerableTime += 20;
						event.setCanceled(true);
						return;
					}
				} else if (event.getSource().getEntity() instanceof TamableAnimal ta) {
					if (ta.getOwner() != null && bef.getOwner() != null && ta.getOwner() == bef.getOwner()) {
						bef.asMob().setHealth(1.0f);
						bef.asMob().invulnerableTime += 20;
						event.setCanceled(true);
						return;
					}
				}
				if (!event.getEntity().level.isClientSide) {
					// Drop all items in inventory if no vanishing curse
					if (/*bef.dropInventoryOnDeath()*//** TODO: Fix item loss if not dropping */true) {
						NFFTamedMobInventory container = bef.getAdditionalInventory();
						for (int i = 0; i < container.getContainerSize(); ++i) {
							if (container.getItem(i) != ItemStack.EMPTY) {
								if (!EnchantmentHelper.hasVanishingCurse(container.getItem(i)))
								{
									event.getEntity().spawnAtLocation(container.getItem(i).copy());
								}
								container.getItem(i).setCount(0);
								bef.updateFromInventory();
							}
						}
					}
					// If drop respawner, drop and initialize
					if (bef.getRespawnerType() != null) {
						NFFMobRespawnerInstance ins = NFFMobRespawnerInstance
								.create(NFFMobRespawnerItem.fromMob(bef.getRespawnerType(), bef.asMob()));
						if (ins != null) {
							if (bef.getDeathRespawnerGenerationType() == DeathRespawnerGenerationType.GIVE) {
								if (bef.isOwnerPresent() && bef.getOwner().getInventory().getFreeSlot() != -1 && bef.getOwner().addItem(ins.get())) 
								{} 
								else 
								{
									if (!bef.asMob().level.getCapability(NFFCapRegistry.CAP_BM_LEVEL).isPresent()) {
										throw new IllegalStateException(
												"BefriendedMobs: Server level missing CNFFLevelModule capability");
									}
									bef.asMob().level.getCapability(NFFCapRegistry.CAP_BM_LEVEL).ifPresent(cap -> 
									{
										cap.addSuspendedRespawner(ins);
									});
								}
							}

							else if (bef.getDeathRespawnerGenerationType() == DeathRespawnerGenerationType.DROP)
							{
								ItemEntity resp = new ItemEntity(event.getEntity().level, event.getEntity().getX(),
										event.getEntity().getY(), event.getEntity().getZ(), ins.get());
								if (bef.isRespawnerInvulnerable()) {
									ins.setInvulnerable(true);
									resp.setInvulnerable(true);
								}
								ins.setRecoverInVoid(bef.shouldRespawnerRecoverOnDropInVoid());
								ins.setNoExpire(bef.respawnerNoExpire());
								if (!MinecraftForge.EVENT_BUS.post(new NFFTamedDropRespawnerOnDyingEvent(bef, ins)))
									event.getEntity().level.addFreshEntity(resp);
							}
						}
					}
				}

				else if (event.getEntity() instanceof TamableAnimal ta) 
				{
					if (event.getSource().getEntity() instanceof INFFTamed srcBef) 
					{
						if (srcBef.getOwner() != null && ta.getOwner() != null && srcBef.getOwner() == ta.getOwner()) {
							ta.setHealth(1.0f);
							ta.invulnerableTime += 20;
							event.setCanceled(true);
							return;
						}
					}
				}

				else if (event.getEntity() instanceof Player player) {
					for (Entity en : ((ServerLevel) (player.level)).getAllEntities()) {
						if (en instanceof Mob mob && mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).isPresent()) 
						{
							if (!NFFTamingMapping.getHandler(mob).dontInterruptOnPlayerDie()
									&& NFFTamingMapping.getHandler(mob).isInProcess(player, mob))
							{
								NFFTamingMapping.getHandler(mob).interrupt(player, mob, true);
							}
						}
					}
				}
			}
		}
	}
	
	// Don't allow befriended zombies to summon
	@SubscribeEvent
	public static void onZombieSummon(SummonAidEvent event)
	{
		if (event.getEntity() instanceof INFFTamed)
		{
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent
	public static void onTimerUp(TamableTimerUpEvent event)
	{
		if (event.getPlayer() != null)
		{
			if (event.getKey().equals("in_hatred"))
			{
				if (event.getCapability().getHatred().contains(event.getPlayer().getUUID()))
				{
					event.getCapability().getHatred().remove(event.getPlayer().getUUID());
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event)
	{
		if (event.isCanceled())
			return;
		LivingEntity living = event.getEntity();
		LivingEntity source = (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity) ?
				(LivingEntity)(event.getSource().getEntity()) : null;
		if (!living.level.isClientSide)
		{
			// Handle befriending process events on mob attacked
			// The events are handled in handler classes, not a forge event
			// On befriendable mob attacked by player
			if (living instanceof Mob 
				&& living.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).isPresent()
				&& source != null
				&& source instanceof Player)
			{
				Mob mob = (Mob)living;
				Player player = (Player)source;
				NFFTamingProcess handler = NFFTamingMapping.getHandler(mob);
				if (handler.isInProcess(player, mob))
				{
					handler.onAttackedByProcessingPlayer(mob, player, event.getAmount() > 0.000001);
				}
				living.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((cap) ->
				{
					if (event.getSource().getMsgId().equals("thorns") && event.getAmount() >= 0.1)
						cap.addHatredWithReason(player, TamableHatredReason.THORNS);
				    else if (event.getAmount() < 0.1)	
						cap.addHatredWithReason(player, TamableHatredReason.HIT);
					else cap.addHatredWithReason(player, TamableHatredReason.ATTACKED);
				});
			}
			// On player attacked by befriendable mob
			else if (living instanceof Player 
				&& source != null 
				&& (source instanceof Mob)
				&& source.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).isPresent())
			{
				Player player = (Player)living;
				Mob mob = (Mob)source;
				NFFTamingProcess handler = NFFTamingMapping.getHandler(mob);
				if (handler.isInProcess(player, mob))
				{
					handler.onAttackProcessingPlayer(mob, player, event.getAmount() > 0.000001);
				}
				mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((cap) ->
				{
					if (event.getAmount() > 0)
						cap.addHatredWithReason(player, TamableHatredReason.ATTACKING);
				});
			}
		}
	}


	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void onLivingUpdate(LivingTickEvent event)
	{
		if (!event.getEntity().level.isClientSide)
		{
			// Tick attribute monitor
			event.getEntity().getCapability(NFFCapRegistry.CAP_ATTRIBUTE_MONITOR).ifPresent(CAttributeMonitor::tick);
			// Tick item stack monitor
			event.getEntity().getCapability(NFFCapRegistry.CAP_ITEM_STACK_MONITOR).ifPresent(CItemStackMonitor::tick);
			// Tick delay action handler
			event.getEntity().getCapability(NFFCapRegistry.CAP_DELAYED_ACTION_HANDLER).ifPresent(CLivingEntityDelayedActionHandler::tick);
			// Tick data
			// event.getEntity().getCapability(NFFCapRegistry.CAP_BEFRIENDED_MOB_DATA).ifPresent(CNFFTamedCommonData::tick);
			if (event.getEntity() instanceof Mob mob)
			{
				// update befriendable mobs
				if (!(mob instanceof INFFTamed))
				{
					mob.getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent((l) ->
					{
						// Timers
						l.updateTimers();
						// AlwaysHostile feature
						if (l.getAlwaysHostileTo() != null)
						{
							Entity target = NaUtilsEntityStatics.getIfCanSee(l.getAlwaysHostileTo(), mob).orElse(null);
							if (target != null && target instanceof LivingEntity targetLiving)
								mob.setTarget(targetLiving);
						}
						// Befriending handler tick
						NFFTamingMapping.getHandler((EntityType<Mob>) (mob.getType())).serverTickInternal(mob);
					});
				}
				// update healing handler cooldown
				mob.getCapability(NFFCapRegistry.CAP_HEALING_HANDLER).ifPresent((l) ->
				{
					l.updateCooldown();
				});
				// IBaubleEquipable tick
				/*if (mob instanceof IBaubleEquipable holder)
				{
					holder.updateBaubleEffects();
				}*/
				
				// update befriended mob anchor position
				if (mob instanceof INFFTamed bm)
				{
					if (bm.getAnchorPos() != null)
					{
						// Stop update when wandering
						if (bm.getAIState() != NFFTamedMobAIState.WANDER)
							bm.updateAnchor();
					}
					// Sometimes it may happens that the mobs still attack allies, reset here
		        	// Generally the code below shouldn't be invoked, so print an error to log
		        	if (NFFTamedStatics.isLivingAlliedToBM(bm, bm.asMob().getTarget()))
		        	{
		        		LogUtils.getLogger().error("BefriendedMobs Framework: Befriended mob [" 
		        				+ bm.asMob().getName().getString() + "] attempting to attack ally ["
		        				+ bm.asMob().getTarget().getName().getString() + "]. Target reset.");
		        		bm.asMob().setTarget(null);
		        		if (bm.asMob().getTarget() != null)
		        			// Maybe sometimes setTarget can be cancelled
		        			NaUtilsEntityStatics.forceSetTarget(bm.asMob(), null);
		        		bm.setPreviousTarget(null);
		        	}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onBefriendedChangeAiState(NFFTamedChangeAiStateEvent event)
	{
		// When switching from wait, clear mob target and owner last hurt target
		// or it may unexpectedly start to attack right on switching
		// But if the owner was just hurt by a mob, this befriended mob will still
		// start to attack it.
		if (event.getStateBefore().equals(NFFTamedMobAIState.WAIT))
		{
			event.getMob().asMob().setTarget(null);
			if (event.getMob().isOwnerPresent())
				event.getMob().getOwner().setLastHurtMob(null);
		}
	}
	
	@SubscribeEvent
	public static void onItemExpire(ItemExpireEvent event)
	{
		NFFMobRespawnerInstance ins = NFFMobRespawnerInstance.create(event.getEntity().getItem());
		if (ins != null && ins.isNoExpire())
		{
			event.setCanceled(true);	
		}
	}
	
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event)
	{
		if (event.getEntity() instanceof LivingEntity living)
		{
			// Setup attribute monitor cap
			event.getEntity().getCapability(NFFCapRegistry.CAP_ATTRIBUTE_MONITOR).ifPresent((cap) -> 
			{
				MinecraftForge.EVENT_BUS.post(new CAttributeMonitor.SetupEvent(living, cap));
			});
			// Setup item stack monitor cap
			event.getEntity().getCapability(NFFCapRegistry.CAP_ITEM_STACK_MONITOR).ifPresent((cap) -> 
			{
				MinecraftForge.EVENT_BUS.post(new CItemStackMonitor.SetupEvent(living, cap));
			});
		}
		if (event.getEntity() instanceof INFFTamedSunSensitiveMob um)
		{
			// Setup befriended undead sun-immunity rules
			um.setupSunImmunityRules();
		}
	}
	
	/*@SubscribeEvent
	public static void onDespawn(AllowDespawn event)
	{
		event.getEntity().getCapability(NFFCapRegistry.CAP_BEFRIENDABLE_MOB).ifPresent(cap ->
		{
			if (cap.isForcePersistent())
				event.setResult(Result.DENY);
		});
	}*/
	
}
