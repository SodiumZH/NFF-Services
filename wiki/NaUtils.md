# NaUtils Instruction

## Overview

NaUtils (Sodium's Utilities) is a library of Minecraft utilities. It contains many different useful classes and methods mainly about the gameplay mechanics.

## Forge Capabilities

##### `CEntityTickingCapability`

A capability template that will be ticked together with entities.

To use this capability, add a static block under the capability declaration:

```java
// Assume YourCapabilityInterface extends CEntityTickingCapability
public static final Capability<YourCapabilityInterface> YOUR_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

static {
	CEntityTickingCapability.registerTicking(YOUR_CAPABILITY);
}
```

Then the capability will be auto ticked by calling `CEntityTickingCapability#tick` in an `EntityTickEvent` (see Mixin Events) listener. Override `tick()` to define what to do on tick.

**Note**: This ticking will *NOT* be cancelled by cancelling `LivingTickEvent` (1.19.2+) or `LivingUpdateEvent` (1.18.2).



## Mixin Events

NaUtils provides some event hooks implemented by Mixin.

### Entity

#### Generic Entity

##### `EntityTickEvent`

Posted at the head of `Entity#tick`, before `Entity#baseTick`.

Not cancellable.

**Note:** If something is done before `super.tick()` in the subclass override, these actions will be done *before* this event.

##### `NonLivingEntityHurtEvent`

Posted when a non-living entity gets hurt only on server. 

Cancellable. If cancelled, the damage will be skipped.

**Note:** This event will *NOT* be posted if it's falling out of world to prevent unexpected infinite falling.  Instead a `NonLivingEntityOutOfWorldEvent` will be posted.

**Note:** `ItemEntity` will *NOT* post this but `ItemEntityHurtEvent`.

For `LivingEntity`, use Forge `LivingHurtEvent`.

##### `NonLivingEntityOutOfWorldEvent`

Posted when a non-living entity gets hurt out of world.

Cancellable. If cancelled, the damage will be skipped.

**Note:** `ItemEntity` will *NOT* post this but `ItemEntityOutOfWorldEvent`.
For `LivingEntity`, use Forge `LivingHurtEvent`.

**Note:** Take care cancelling this event. It may cause infinite falling.

##### `EntityLoadEvent`

Posted before loading entity data from NBT (at the head of `Entity#load()`), allowing to modify the NBT before loading.

##### `EntityFinalizeLoadingEvent`

Posted after loading entity data from NBT (at the end of `Entity#load()`).

**Note:** if exception thrown during loading, this event will not be posted.

#### Projectile

##### `ProjectileHitEvent`

Posted when any projectile hit something, either block or entity, on any projectile calling `Projectile#onHit` and the hit result isn't `MISS`.

Usually it's not posted if `ProjectileImpactEvent` is cancelled.

Cancellable. If cancelled, the hit action will be cancelled.

**Note:** if in subclasses `onHit` is overridden and something is done before `super.onHit()`, these actions won't be cancelled. 

##### `ThrownTridentSetBaseDamageEvent`

Posted when a `ThrownTrident` set base damage, before the damage value is modified by vanilla mechanisms e.g. enchantments. This event allows to modify the damage value.

##### `ThrownTridentSetFinalDamageEvent`

Posted before a `ThrownTrident` finally applies its damage, allowing to modify the damage value.

#### Item Entity

##### `ItemEntityHurtEvent`

Posted when an `ItemEntity` takes damage. 

Cancellable. If cancelled, the damage will be cancelled.

**Note:** This event will NOT be fired if it's falling out of world to prevent possible infinite falling. Instead an `ItemEntityOutOfWorldEvent` will be posted.

##### `ItemEntityOutOfWorldEvent`

Posted when an `ItemEntity` takes damage out of world

Cancellable. If cancelled, the damage will be skipped.

**Note:** Take care cancelling this event. It may cause infinite falling.

#### Living Entity

##### `LivingEntitySweepHurtEvent`

Posted when a `LivingEntity` receives sweep damage from a player.

Cancellable. If cancelled, the damage will be cancelled。

**Note:** It's only posted on vanilla sweep. Sweep attack from other mods will not post this.

##### `LivingStartDeathEvent`

Posted when a `LivingEntity` starts to do the death process, after Forge `LivingDeathEvent` is posted, which means this entity will *really* die.

*Not* cancellable.

##### `LootCheckPlayerKillEvent`

Posted on a `LivingEntity` dies, before dropping items from the loot table, after Forge `LootingLevelEvent` posted, before checking whether this kill is committed by a player.

Not cancellable, but having a result. `ALLOW` = always regarding as player-killed; `DENY` = always regarding as non-player-killed; `DEFAULT` = original value.

#### Mob

##### `MobSunBurnTickEvent`

Posted on a mob is undergoing a sun-burn check and accounted for being on a sun-burn tick (i.e. going to catch fire under sun).

Cancellable. If cancelled, the mob will be accounted for not being on a sun-burn tick (i.e. not going to catch fire).

**Note:** This event is posted in `Mob#isSunBurnTick`. This methods is used for most sun-sensitive mobs (including all vanilla sun-sensitive mobs), but maybe not all. So it's no guarantee that all sun-sensitivity can be controlled by this event.

##### `MobPickUpItemEvent`

Posted before a `Mob` picks up an `ItemEntity`.

Cancellable. If cancelled, the picking action will be skipped.

**Note**: Player picking up item will not post this event.

##### `MobFinalizePickingUpItemEvent`

Posted after a `Mob` picks up an `ItemEntity`.

##### `MobInteractEvent`

Posted before `Mob#mobInteract`. If the interaction is cancelled before `Mob#mobInteract` is called, this event will not be posted.

This event is not cancellable or having an event result (`Event.Result`), but holds an `InteractionResult` as result. If the result is set to "consumes action" i.e. `SUCCESS`, `CONSUME` or `CONSUME_PARTIAL`, the following `mobInteract` will be skipped.

##### `MobCheckDespawnEvent`

Posted when a mob starts to check if it should despawn. This event will be always posted despite of the results of `Entity#shouldDespawnInPeaceful`, `Mob#requiresCustomPersistence` and `AllowDespawn` event.

Cancellable. If cancelled, the whole despawn check will be skipped and this mob will not despawn, despite of the results above, and `AllowDespawn` event will not be posted.

##### `MonsterPreventSleepEvent`

Posted before a `Monster` is preventing player sleep.

Cancellable. If cancelled, this monster will not prevent sleep.

## Utility Method Libs



### `NaContainerUtils`

This lib includes methods for simplifying operations on containers.



