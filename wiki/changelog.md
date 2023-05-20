# Change Log

## 1.18.2

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

## 1.19.2

### 0.1.3

### 0.1.2-alpha

Made most methods in BaubleSystem key-sensitive in order to enable different types of Bauble slots in a single mob.

Added `CItemStackMonitor`capability, intended to detect change of given item slots of Living Entities.

Some minor API changes.

### 0.1.1-alpha

Separated from DWMG mod.

