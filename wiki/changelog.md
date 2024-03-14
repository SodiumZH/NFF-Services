# Change Log

### 0.x.22

##### BefriendMobs

Fully removed the old Bauble System.

Removed Debug AI Switch (migrated to NaUtils).

Refactored debug items.

##### NaUtils

Added Debug AI Switch.

Added Debug Target Setter.

Added Mixin-based events: `ThrownTridentSetBaseDamageEvent` and `ThrownTridentSetFinalDamageEvent` for modifying damage of Thrown Trident. 

Renamed classes:

 -`ItemHelper` => `NaItemUtils`

### 0.x.21

Bauble System remade. WARNING: it's probably very unstable! Keep your data backed-up!!!

Added tag: `befriendmobs:neutral_to_bm_mobs`. Mobs with this tag will not proactively attack befriended mobs unless being attacked.

### 0.2.20.1

##### NaUtils

Fixed a wrong mixin in `NaUtilsMixinPlayer` which causes compat issues.

## Old Versions

## 1.19.2

### 0.1.20

##### BefriendMobs

Labeled parameters in `CBefriendedMobData#Values` as private. Now they can be accessed only with getters and setters.

Now `CBefriendedMobData` will record the owner's name and encountered time.

Now `BefriendedShootProjectileGoal` supports dynamic attack intervals.

Added `IBefriendedMob#GolemAttitude` for configuring how golems should handle hostility to the mob.

Deprecated `IBefriendedMob#getOwner` and `IBefriendedMob#isOwnerPresent`. Use `getOwnerInDimension`, `getOwnerInWorld`, `isOwnerInDimension` and `isOwnerInWorld` instead.

Changed method names:

 -`IBefriendedMob#getTempData` => `IBefriendedMob#getData`

Changed parameter names:

 -`BMCaps#CAP_BEFRIENDED_MOB_TEMP_DATA` => `BMCaps#CAP_BEFRIENDED_MOB_DATA`

##### NaUtils

Added `WithStandardColors` as a collection of the same-type object with 16 vanilla color variants, like Wools, Dyes, etc. It allows to browse color by object and browse object by color. The vanilla colored objects are collected in `ColoredBlocks` and `ColoredItems`.

Added `ProjectileHitEvent` which is fired when a projectile hits anything. It's implemented by mixin-injecting into the beginning of `Projectile#onHit`. WARNING: NOT TESTED!

### 0.1.19.1

##### NaUtils

Fixed `LivingEntitySweepHurtEvent` not working and making sweeping attack not work.

### 0.1.19

##### BefriendMobs

Added Mixin-Extras dependency.

Temporarily removed `IBefriendedMob#dropInventoryOnDeath` feature due to a bug that the items will get lost if it returns false. Currently mobs always drop inventory on death.

Labeled `start()`, `stop()` and `tick()` in `BefriendedGoal` and `BefriendedTargetGoal` as final. To override these, use `IBefriendedMob#onStart`, `IBefriendedMob#onStop` and `IBefriendedMob#onTick` instead.

Now `IBefriendedSunSensitiveMob` sun immunity feature is automatically implemented using `MobSunBurnTickEvent`.

Now `BefriendedRestrictSunGoal` and `BefriendedFleeSunGoal` will not run for sun-immune `IBefriendedSunSensitiveMob`.

##### NaUtils

Added Mixin-Extras dependency.

Added some Mixin-based events:

​	-Excluded out-of-world damage from `NonLivingEntityHurtEvent` and `ItemEntityHurtEvent`. For out-of-world damage it will post `NonLivingEntityOutOfWorldEvent` and `ItemEntityOutOfWorldEvent` instead. 

​	-`MobSunBurnTickEvent` before a mob is tested to be on a sun burn tick. It will only affect sun-burn features using `Mob#isSunBurnTick`.

​	-`LivingEntitySweepHurtEvent` before a living entity is hurt by player sweep attack.

### 0.1.18

##### BefriendMobs

Added Mixin. Now adding BefriendMobs API as dependency requires to add Mixin in `build.gradle`. For details see `build.gradle` in mod *Days with Monster Girls*. 

Added `HealingItemTable` to handle healing. It accepts 4 methods to check if can use: item, tag, item stack predicate and item registry key. Generally it accepts only one of them. Adding multiple methods is allowed but not recommended.

Changed `IBefriendedMob#getHealingItems` to `HealingItemTable`. Deprecated `IBefriendedMob#getNonconsumingHealingItems`.

Changed `CHealingHandler` to adapt `HealingItemTable`. Now ``CHealingHandler#applyHealingItem` accepts cooldown ticks input. Deprecated `CHealingHandler#getHealingCooldownTicks`.

##### NaUtils

Added Mixin. Now adding BefriendMobs API as dependency requires Mixin in `build.gradle`. For details see `build.gradle` in mod *Days with Monster Girls*. 

Added `ItemEntityHurtEvent` and `NonLivingEntityHurtEvent`, allowing to cancel damages to non-living entities.

Added `ConditionalAttributeModifier` as an auto-updating `AttributeModifier` added and removed depending on a `Predicate` as condition.

Added`ObjectOrSupplier` as a supplier either from a static object or a functional `Supplier`.

Added `ObjectOrKey` as a supplier either from a static object or a key of registry.

Added `ItemOrKey` and `EntityTypeOrKey` as wrappers for `ObjectOrKey` of `Item` and `EntityType` .

Added `DynamicObjectKeyMap` as a mapping from registered objects to something. It has `ObjectOrKey` as keys and `ObjectOrSupplier` as values, allowing to dynamically find objects from registries and get volatile values (e.g. max health) from static map objects.

Added `DynamicItemKeyMap` and `DynamicEntityTypeKeyMap` as wrappers of `DynamicObjectKeyMap` of `Item` and `EntityType`.

### 0.1.17

Added `CLivingEntityDelayedActionHandler` capability and `ILivingDelayedActions` as a wrapper interface for delayed (latent) actions in `LivingEntity`s.

Adjusted the implementation of `CAttributeMonitor` to support non-vanilla `Attribute`s, and added `IAttributeMonitor` as a wrapper.

Adjusted `CItemStackMonitor` and added `IItemStackMonitor` as a wrapper.

Now `CAttributeMonitor` and `CItemStackMonitor` are attached only to `LivingEntity`s implementing corresponding wrapper interfaces.

Added `MutablePredicate` for composition of multiple checking conditions, and re-implemented `BefriendedSunSensitiveMob` sun-immunity with it.

### 0.1.16

Added `RepeatableAttributeModifier` for applying a same `AttributeModifier` for multiple times to a single `LivingEntity`.

Added `ItemPackageItem` for a temporary container of multiple `ItemStack`s, mainly for recipe results. (Not tested)

Added some setup hooks for simplification of BM-specific registration.

​	-`RegisterBefriendingTypeEvent` instead of `BefriendingTypeRegistry`

​	-`RegisterGuiScreenEvent` instead of `BefriendedGuiScreenMaker`

Adjusted Bauble system:

​	-Added several event hooks in `BaubleHandler`.

​	-Renamed class: `IBaubleHolder` => `IBaubleEquipable`

​	-Marked some methods as `final` to prevent accident overriding.

Fixed issues in `CBefriendableMob`:

​	-Fixed `hasPlayerTimer` always returning `true`.

​	-Fixed `addHatredWithReason` crash with input time 0. Now it will trigger all related events but not add hatred if input is 0. Internal `addHatred` still crashes with time 0.

Some internal API changes for consistency with 1.20.1.

### 0.1.15

Added `CBMLevelModule` as a serializable capability for levels.

Renamed classes:

​	-`CBMPlayer` => `CBMPlayerModule`

​	-`IBefriendedUndeadMob` => `IBefriendedSunSensitiveMob`

Changed class package paths:

​	-`entity.IBefriendedMob` => `entity.befriended.IBefriendedMob`

​	-`entity.BefriendedHelper` => `entity.befriended.BefriendedHelper`

​	-`entity.ai.IBefriendedUndeadMob` => `entity.befriended.IBefriendedSunSensitiveMob`

​	-`entity.ai.IBefriendedAmphibious` => `entity.befriended.IBefriendedAmphibious`

​	and some other classes.

Added some parameters for `BefriendedGoal` and `BefriendedTargetGoal`.

Added an event on mob befriended.

### 0.1.14

Added `IBefriendableMob#stopTimer`related methods.

### 0.1.13

Added `LinearColor` as a representation of RGB colors, and added related utilities.

Adjusted `HandlerItemGivingProgress` for flexibility:

 -- Added `getReturnedItem` and `onItemGiven` hooks.

 -- Changed `HandlerItemGiving#isItemAcceptable` and `HandlerItemGiving#shouldItemConsume` checks to `ItemStack` sensitive versions.

 -- Changed `getProcValueToAdd` into player, mob and previous progress value sensitive version.

Now in `BefriendingHandler` the mob will be set persistent if in process by default. To disable it, override `persistantIfInProcess` to false.

Added `CHealingHandler#HealingSucceededEvent`.

### 0.1.12

Fixed `ReflectHelper` callers not using SRG names.

### 0.1.11

Added `CBMPlayer`, a data-storage capability attached on players.

Added `MobOwnershipTransfererItem` which can change the owner of mobs.

Changed `MobCatcherItem#canUsePredicate` from `Predicate<Mob>` to `BiPredicate<Mob, Player>`, involving the item user.  

Now `BefriendedMeleeAttakGoal#checkAndPerformAttack` distance check depends on the minimum distance from the attacker's border box to the target's border box. This fixes the issue that ordinary mobs (like Zombies) can't hit flying mobs (like Phantoms). 

### 0.1.10

Added `MobCatcherItem`. This item converts mob to corresponding Respawner.

Renamed `ItemMobRespawner` to `MobRespawnerItem`.

Fixed befriended mobs' inventory always being cleared on respawning from respawner.  Added `retainBefriendedMobInventory` attribute to `ItemMobRespawner` which is default `true`. Use `ItemMobRespawner#setRetainBefriendedMobInventory` to set the value.

Fixed `ReflectHelper` totally not working without IDE. Now `ReflectHelper` needs SRG field/method names.

### 0.1.9

Added `BaubleHanlder#getItemKeysAcceptable` in place of `BaubleHandler#getItemsAcceptable`, using item registry keys instead of objects. Previous `BaubleHandler#getItemsAcceptable` still works but are not recommended to override.

Changed package path: `net.sodiumstudio.befriendmobs.util` => `net.sodiumstudio.nautils`.

Classes renamed for simplification:

 -- `BefMobCapabilities` => `BMCaps`

 -- `BefMobCapabilityAttachment` => `BMCapabilityAttachment`

 -- `BefMobItems` => `BMItems`

Deprecated `BMItems#MOB_RESPAWNER` default instance. Now mob respawners should be defined in dependents. Also removed all methods related to this default instance. **WARNING: This may be incompatible to existing item stacks of default Mob Respawner in the game!**

Removed `IBefriendedMob#getRespawnerType` default implementation. Also it now controls whether the mob will drop respawners, non-null for enabled and null for disabled. Removed `IBefriendedMob#shouldDropRespawner`.

Added some utility functions, mainly in `ContainerHelper` and `TagHelper` .

Added `SerializableMap` and related util functions in `NbtHelper`. 

Removed `IBefriendedMob#onInteraction` and `IBefriendedMob#onInteractionShift`. Use vanilla `Mob#mobInteract` instead.

Fixed `BefriendingHandler#initCap` not working.

### 0.1.8

Added a serializable `CompoundTag` to `CBefriendedMobTempData`.

Added `BaubleItem` of which the description text on hovering can be customized on registration.

Added preset features in `CBefriendableMob`:	(For details see source file)

​	-- `getAlwaysHostileTo` and `setAlwaysHostileTo` 

​	-- `isForcePersistent` and `setForcePersistent`  

Adjusted befriendable mob hatred mechanism: 

  -- Added `BefriendableAddHatredReason.HIT` reason, indicating that the player hit the mob but didn't cause damage.

  -- If the damage player given to the mob is less than 0.1, the reason will be `HIT` instead of `ATTACKED`.

Fixed Mob Respawner item entities vanishing in fire and lava.

### 0.1.7

Removed deprecated `onInteraction` and `onInteractionShift` override in `AbstractBefriendedCreeper` which blocks the ` mobInteract` method.

### 0.1.6

Added `IBefriendedGoal` interface to combine the common methods of `BefriendedGoal` and `BefriendedTargetGoal` . Use `asGoal()` to cast this to `Goal`.

Set `BefriendedGoal#canUse` , `BefriendedGoal#canContinueToUse`, `BefriendedTargetGoal#canUse`, `BefriendedTargetGoal#canContinueToUse` as final and included some necessary checks inside. In subclasses, override `IBefriendedGoal#checkCanUse` and `IBefriendedGoal#checkCanContinueToUse` instead.

Added `Map<String, Object> tempObjects` to `CBefriendedMobTempData` for temporary storage of objects.

Now GUI will be automatically closed when the mob is removed from level.

### 0.1.5

Fixed `ReflectHelper::forceInvoke` and `ReflectHelper::forceInvokeRetVal`. The previous versions are TOTALLY WRONG. Now the varargs need firstly parameter classes followed by parameter values. 

Fixed crashes related to `IBefriendedMob::getOwner` returning null when owner isn't in the same level to the mob.

Added Debug AI Switch for simplification of `setNoAi()` operation.

Added `BMDebugItemHandler.UseEvent` for custom debug items.

Some internal changes to adapt to DWMG 0.1.5.

### 0.1.4

Refactored `BefriendedAIState` from enum to class.

Removed `BefriendedInventoryMenu::makeGui` because it doesn't work on dedicated server. To register a method of GUI construction, listen to `FMLClientSetup` event and use `BefriendedGuiScreenMaker::put` to map inventory menu and GUI construction method.

Refactored `ItemMobRespawner`. The `CMobRespawner` capability was removed, and now `ItemStack` of `ItemMobRespawner` uses a wrapper class `MobRespawnerInstance` which provides the same methods to previous `CMobRespawner`. This change is intended to solve the incompatibility on dedicated server. Use `MobRespawnerInstance::create` to cast `ItemStack` to `MobRespawnerInstance`. If the `ItemStack` isn't a valid `ItemMobRespawner` it will return `null`, and use null check in place of previous capability `LazyOptional::ifPresent` check. 

WARNING: the change makes `ItemMobRespawner` save data incompatible to old versions.

### 0.1.3

Versions above will no longer use "alpha" as postfix. All versions with prefix 0 are alpha.

Adjusted `getItemsAccepted` and `isAccepted` checks mob-sensitive in BaubleSystem. Method `getItemsAccepted` now uses a HashMap with `Predicate<IBaubleHolder>` as value, and if this predicate is non-null, the mob will need to fulfill this additional check to accept the given item.

Reimplemented `IBefriendedUndeadMob`. Now it uses rules (`Supplier<Boolean>`) to dynamically check if the mob should be sun-immune. For details see source file. 

Added `CHealingHandler.ApplyHealingItemEvent` and `CHealingHandler.HealingFailedEvent` for healing handler. Now simple adjustment of healing actions and particle sending needn't rewrite capability implementation.

Rounded-off the default mob attribute values displayed in `BefriendedGuiScreen` to integer.

Added support for non-standard texture size in `BefriendedGuiScreen`.

Fixed befriended mob unable to drop inventory on death.

### 0.1.2-alpha

Made most methods in BaubleSystem key-sensitive in order to enable different types of Bauble slots in a single mob.

Added `CItemStackMonitor`capability, intended to detect change of given item slots of Living Entities.

Some minor API changes.

### 0.1.1-alpha

Separated from DWMG mod.

## 1.20.1

### 0.2.17

Identical to 1.19.2-0.1.17.

### 0.2.16

Identical to 1.19.2-0.1.16.

### 0.2.15

Ported from 1.19.2-0.1.15.

Added `BlockMaterial`, an alternative `BlockBehaviour.Properties` generator class for old `Material`.

Adjusted the API of `BefriendedGuiScreen`.

Adjusted the explosions of `AbstractBefriendedCreeper` and added an option for always dropping blocks like TNT.

## 1.18.2

### 0.0.17

All changes in 1.19.2-0.1.17.

Fixed crash on mob changing target.

### 0.0.16

Identical to 1.19.2-0.1.16.

### 0.0.15

All changes in 1.19.2-0.1.15.

Added `AbstractFishingHookEntity`, a wrapper of `FishingHook` to support non-vanilla Fishing Rods.

### 0.0.14

Identical to 1.19.2-0.1.14.

### 0.0.13

Identical to 1.19.2-0.1.13.

### 0.0.12

Identical to 1.19.2-0.1.12.

### 0.0.11

Identical to 1.19.2-0.1.11.

### 0.0.10

Added `MobCatcherItem`. This item converts mob to corresponding Respawner.

Renamed `ItemMobRespawner` to `MobRespawnerItem`.

Fixed befriended mobs' inventory always being cleared on respawning from respawner.  Added `retainBefriendedMobInventory` attribute to `ItemMobRespawner` which is default `true`. Use `ItemMobRespawner#setRetainBefriendedMobInventory` to set the value.

Fixed `ReflectHelper` totally not working without IDE. Now `ReflectHelper` needs SRG field/method names.

### 0.0.9

Added `BaubleHanlder#getItemKeysAcceptable` in place of `BaubleHandler#getItemsAcceptable`, using item registry keys instead of objects. Previous `BaubleHandler#getItemsAcceptable` still works but are not recommended to override.

Changed package path: `net.sodiumstudio.befriendmobs.util` => `net.sodiumstudio.nautils`.

Classes renamed for simplification:

 -- `BefMobCapabilities` => `BMCaps`

 -- `BefMobCapabilityAttachment` => `BMCapabilityAttachment`

 -- `BefMobItems` => `BMItems`

Deprecated `BMItems#MOB_RESPAWNER` default instance. Now mob respawners should be defined in dependents. Also removed all methods related to this default instance. **WARNING: This may be incompatible to existing item stacks of default Mob Respawner in the game!**

Removed `IBefriendedMob#getRespawnerType` default implementation. Also it now controls whether the mob will drop respawners, non-null for enabled and null for disabled. Removed `IBefriendedMob#shouldDropRespawner`.

Added some utility functions, mainly in `ContainerHelper` and `TagHelper` .

Added `SerializableMap` and related util functions in `NbtHelper`. 

Removed `IBefriendedMob#onInteraction` and `IBefriendedMob#onInteractionShift`. Use vanilla `Mob#mobInteract` instead.

Fixed `BefriendingHandler#initCap` not working.

### 0.0.8

Added a serializable `CompoundTag` to `CBefriendedMobTempData`.

Added `BaubleItem` of which the description text on hovering can be customized on registration.

Added preset features in `CBefriendableMob`:	(For details see source file)

​	-- `getAlwaysHostileTo` and `setAlwaysHostileTo` 

​	-- `isForcePersistent` and `setForcePersistent`  

Adjusted befriendable mob hatred mechanism: 

  -- Added `BefriendableAddHatredReason.HIT` reason, indicating that the player hit the mob but didn't cause damage.

  -- If the damage player given to the mob is less than 0.1, the reason will be `HIT` instead of `ATTACKED`.

Fixed Mob Respawner item entities vanishing in fire and lava.

### 0.0.7

Removed deprecated `onInteraction` and `onInteractionShift` override in `AbstractBefriendedCreeper` which blocks the ` mobInteract` method.

### 0.0.6

Added `IBefriendedGoal` interface to combine the common methods of `BefriendedGoal` and `BefriendedTargetGoal` . Use `asGoal()` to cast this to `Goal`.

Set `BefriendedGoal#canUse` , `BefriendedGoal#canContinueToUse`, `BefriendedTargetGoal#canUse`, `BefriendedTargetGoal#canContinueToUse` as final and included some necessary checks inside. In subclasses, override `IBefriendedGoal#checkCanUse` and `IBefriendedGoal#checkCanContinueToUse` instead.

Added `Map<String, Object> tempObjects` to `CBefriendedMobTempData` for temporary storage of objects.

Now GUI will be automatically closed when the mob is removed from level.

### 0.0.5

Fixed `ReflectHelper::forceInvoke` and `ReflectHelper::forceInvokeRetVal`. The previous versions are TOTALLY WRONG. Now the varargs need firstly parameter classes followed by parameter values. 

Fixed crashes related to `IBefriendedMob::getOwner` returning null when owner isn't in the same level to the mob.

Added Debug AI Switch for simplification of `setNoAi()` operation.

Added `BMDebugItemHandler.UseEvent` for custom debug items.

Some internal changes to adapt to DWMG 0.1.5.

### 0.0.4

Refactored `BefriendedAIState` from enum to class.

Removed `BefriendedInventoryMenu::makeGui` because it doesn't work on dedicated server. To register a method of GUI construction, listen to `FMLClientSetup` event and use `BefriendedGuiScreenMaker::put` to map inventory menu and GUI construction method.

Refactored `ItemMobRespawner`. The `CMobRespawner` capability was removed, and now `ItemStack` of `ItemMobRespawner` uses a wrapper class `MobRespawnerInstance` which provides the same methods to previous `CMobRespawner`. This change is intended to solve the incompatibility on dedicated server. Use `MobRespawnerInstance::create` to cast `ItemStack` to `MobRespawnerInstance`. If the `ItemStack` isn't a valid `ItemMobRespawner` it will return `null`, and use null check in place of previous capability `LazyOptional::ifPresent` check. 

WARNING: the change makes `ItemMobRespawner` save data incompatible to old versions.

### 0.0.3

Versions above will no longer use "alpha" as postfix. All versions with prefix 0 are alpha.

Adjusted `getItemsAccepted` and `isAccepted` checks mob-sensitive in BaubleSystem. Method `getItemsAccepted` now uses a HashMap with `Predicate<IBaubleHolder>` as value, and if this predicate is non-null, the mob will need to fulfill this additional check to accept the given item.

Reimplemented `IBefriendedUndeadMob`. Now it uses rules (`Supplier<Boolean>`) to dynamically check if the mob should be sun-immune. For details see source file. 

Added `CHealingHandler.ApplyHealingItemEvent` and `CHealingHandler.HealingFailedEvent` for healing handler. Now simple adjustment of healing actions and particle sending needn't rewrite capability implementation.

Rounded-off the default mob attribute values displayed in `BefriendedGuiScreen` to integer.

Added support for non-standard texture size in `BefriendedGuiScreen`.

Fixed befriended mob unable to drop inventory on death.

### 0.0.2-alpha

Made most methods in BaubleSystem key-sensitive in order to enable different types of Bauble slots in a single mob.

Added `CItemStackMonitor`capability, intended to detect change of given item slots of Living Entities.

Some minor API changes.

### 0.0.1-alpha

Separated from DWMG mod.