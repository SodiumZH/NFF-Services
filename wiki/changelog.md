# Change Log

## 1.18.2

### 0.0.3

Versions above will no longer use "alpha" as postfix. All versions with prefix 0 are alpha.

Adjusted `getItemsAccepted` and `isAccepted` checks mob-sensitive in BaubleSystem. Method `getItemsAccepted` now uses a HashMap with `Predicate<IBaubleHolder>` as value, and if this predicate is non-null, the mob will fulfill this additional check to accept the given item.

Remade `IBefriendedUndeadMob`. Now it uses rules (`Predicate<IBefriendedMob>`) to dynamically check if the mob should be sun-immune. For details see source files.

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

