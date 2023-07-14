# Change Log

## 1.19.2

### 0.1.9

Added `BaubleHanlder#getItemKeysAcceptable` in place of `BaubleHandler#getItemsAcceptable`, using item registry keys instead of objects. Previous `BaubleHandler#getItemsAcceptable` still works but are not recommended to override.

Changed package path: `net.sodiumstudio.befriendmobs.util` => `net.sodiumstudio.nautils`.

Added some utility functions, mainly in `ContainerHelper` and `TagHelper` .

Added `SerializableMap` and related util functions in `NbtHelper`. 

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

## 1.18.2

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