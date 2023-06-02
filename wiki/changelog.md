# Change Log

## 1.19.2

### 0.1.5

Fixed `ReflectHelper::forceInvoke` and `ReflectHelper::forceInvokeRetVal`. The previous versions are TOTALLY WRONG. Now the varargs need firstly parameter classes followed by parameter values. 

Fixed crashes related to `IBefriendedMob::getOwner` returning null when owner isn't in the same level to the mob.

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


