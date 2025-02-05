# Sodium's Utilities Instruction

## Overview

Sodium's Utilities (NaUtils) is a library of Minecraft utilities. It contains many different useful classes and methods mainly about the gameplay mechanics.

## Registry

NaUtils provides a simple registry system like the forge registry, mainly for the global registration of custom-defined data types.

### `NaUtilsRegistry`

The registry class. Use the code below to declare a custom registry:

#### Declaration

```java
public static final NaUtilsRegistry<YourDataType> YOUR_REGISTRY = new NaUtilsRegistry<YourDataType>(new ResourceLocation("your_mod_id", "your_registry_key"));
```

This defines a registry for data type `YourDataType` with key `"your_mod_id:your_registry_key"`. Please note this key is the key of *this registry*, or it's key in the registry of all registries. (The "registry registry" is internal.)

Note that you must call the class where registries are defined in your mod main class' constructor, so that it will be loaded in an early stage. For example, you can define an empty method `public static void init(){}` in the declaring class and call it in the mod main class.

#### Registration

`RegistryEntryCollection` is a utility for registering entries into registries. Its usage is similar to Forge `DeferredRegister`.

##### Declaration

```java
public static final RegistryEntryCollection<YourDataType> REGISTER = RegistryEntryCollection.create(YOUR_REGISTRY, "your_mod_id");
```

This defines a `RegistryEntryCollection` for registry `YOUR_REGISTRY` with namespace `"your_mod_id"`.

##### Registering

Registering should be declared in the same class of `RegistryEntryCollection` and below it.

```java
public static final NaUtilsRegistry.Accessor<YourDataType> YOUR_ENTRY = REGISTER.register("your_entry", () -> new YourDataType(...));
```

This action registers `YourDataType(...)` with key `your_mod_id:your_entry` into `REGISTER`. Like Forge registry, `NaUtilsRegistry` also uses `Supplier`s instead of instances.

It returns a `NaUtilsRegistry.Accessor` which is similar to `RegistryObject` for Forge registry. You can call `get()` to access the value.

##### Merging

Registering above only adds entries into `RegistryEntryCollection`, and you need to merge the registered entries into registry. You can do this by calling `REGISTER.merge()` in your mod main class' constructor.

#### Access

As `NaUtilsRegistry` uses `Supplier`s as values, it must generate values before accessing. By default, this will be done at the first time you call `NaUtilsRegistry#Accessor#get()`. Note that once the `NaUtilsRegistry.Accessor` outputs a non-null instance, the `Supplier` will be no longer called and the output results will no longer change.

##### Pre-generating

Optionally, you can manually generate all instances for a registry at a given phase of game setup. This action can be done by calling `setShouldGenerateOnCommonSetup()`, `setShouldGenerateOnClientSetup()` and `setShouldGenerateOnServerSetup()` on registry declaration to make the registry to generate all entries on common setup, on client setup and on server setup respectively.

#### Built-in registries

NaUtils built-in registries are declared in `NaUtilsRegistries `class.

### Misc

There are also some registries or utilities for registration that are not in `NaUtilsRegistry`.

#### `DeferredEntityAttributeRegisterEvent`

Event for registering `AttributeSupplier`s that should be added on server start, not on mod setup. This is for attributes depending on data which is not available on mod setup (e.g. config attributes).



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

### Client

#### Entity

##### `MerchantOfferUnavailableInfoEvent`

Posted on client when a vanilla merchant (villager GUI) is about to display information to show the trade is out of stock. 

By default it's "Villagers restock up to two times per day." (translation key = `"merchant.deprecated"`).

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

Posted when a mob starts to check if it should despawn. This event will be always posted despite the results of `Entity#shouldDespawnInPeaceful`, `Mob#requiresCustomPersistence` and `AllowDespawn` event.

Cancellable. If cancelled, the whole despawn check will be skipped and this mob will not despawn, despite the results above, and `AllowDespawn` event will not be posted.

##### `MonsterPreventSleepEvent`

Posted before a `Monster` is preventing player sleep.

Cancellable. If cancelled, this monster will not prevent sleep.

### Level

##### `LevelCapabilityDataLoadEvent`

Posted before loading Level capabilities from data, allowing to modify the NBT before loading.

### Item

##### `BlockItemConsumeOnPlaceEvent`

Posted before an `ItemStack` of `BlockItem` is about to be consumed after being placed onto the level.

Cancellable. If cancelled, the item will not be consumed, but the placed block will still be there.

## Vanilla Trade System

Vanilla Trade System allows to enable Vanilla Villager-like trade on any mobs. It is Implemented by Forge Capability.

### `CVanillaMerchant` and `VanillaMerchant`

`CVanillaMerchant` is the capability interface for mobs carrying vanilla trade. `VanillaMerchant` is the default implementation of `CVanillaMerchant` for users to extend.

#### Usage



## In-Game Debug Items

NaUtils provides some in-game debug utilities. The items are available only by `/give` command.

### AI Switch

Item key: `nautils:debug_ai_switch`

Right click to enable/disable a mob's AI. The effect is the same as changing the mob's `isNoAi` tag and calling `setNoAi()` method.

### Target Setter 

Item key: `nautils:debug_target_setter`

Used to specify a mob's attack target.

Right click a mob to select, and then right click another mob to make the former attack this mob.

### Mob Remover

Item key: `nautils:debug_mob_remover`

Used to remove a mob.

Right click a mob to select as pending removal, then right click this mob again to confirm removing it.

It has two modes: delete mode and killing mode. The mode can be switched by shift+right click without target. Kill mode by default.

In delete mode, the mob will be directly deleted from the level but not killed, and the actions on mob death will be skipped.

In killing mode, the mob will be killed (same as using `/kill` command). The actions on mob death will be performed. 

## Utility Method Libs

### `NaUtilsContainerStatics`

This lib includes methods for simplifying operations on containers.



