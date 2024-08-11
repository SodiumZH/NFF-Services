# BefriendMobs Framework Tutorial

By SodiumZH

## Overview

BefriendMobs Framework is intended to help modders easily create features of befriending (or say taming) mobs and friendly mobs despite their type hierarchy.

For example, vanilla tamable mobs (e.g. wolf) inherit `TamableMob` class (except horse and variants). If you want to make hostile mobs (e.g. `Zombie`) friendly or tamed, only using vanilla API it may become complex. 

You can easily create a subclass of `Zombie` (or anything else extends `Mob`) with BefriendMobs API without using any vanilla `TamableMob` interfaces, and without considering about the type hierarchy of the existing mobs.

The tutorial below is a minimal description. For more examples of application, see my depending project *Days with Monster Girls* (GitHub: https://github.com/SodiumZH/Days-with-Monster-Girls).

**WARNING: This lib is still in development. Version updates will make it incompatible to the old versions. It's recommended to specify the lib version in mod.toml instead of providing a version range.** 

## `CBefriendedMobData`

`CBefriendedMobData` is an additional Capability attached on all mobs which implement `IBefriendedMob`. It contains all common data for befriended mobs.

It allows to add custom data. For non-serialized data, use Temp Objects. For serialized but non-synched data, use Additional NBT. For synched data, use Synched Data.

### Fields

#### General

##### Identifier

The mob's additional UUID as BM identifier other than its entity UUID. If the mob dies and respawns, the entity UUID may change but this identifier will not. It's generated on befriending and keeps unchanged.

Read-only (getter: `getIdentifier`). If missing, a random new identifier will be generated using `generateIdentifier`.

##### Initial Type

The mob's type as `EntityType` when it's befriended. It will keep unchanged when the mob converts to another type.

Read only (getter: `getInitialEntityType`). If missing, the current type will be recorded using `recordInitialType`.

##### Additional NBT

An additional NBT for this mob, allowing to add/remove custom serializable data.

Getter: `getAdditionalNBT`

##### Temp Objects

An object map (`String` -> generic `Object` ) for non-serialized data, allowing to add/remove custom non-serialized data.

Not directly accessible, but can be operated by `getTempData`, `addTempData` and `removeTempData`.

##### Synched Data

A map for data that should be synched to the clients every several ticks (every tick by default).

It requires to define methods for serialization/deserialization in NBT and `FriendlyByteBuf` using `NaUtilsDataSerializer`. 

#### Owner related



## Bauble System

**Note: this tutorial is for remade Bauble System in 0.x.21. Legacy before this version is deprecated.**

Bauble System is a subsystem of BefriendMobs Framework allowing to equip baubles (additional equipment slots) onto **mobs** (like Curios API for players).

### References

#### `IBaubleRegistryEntry`

The base interface for everything that can be registered as a bauble into Bauble Registry. It defines all behaviors of a type of bauble. Generally it includes `DedicatedBaubleItem` for new items designed mainly as baubles, and `BaubleBehavior` which defines an existing item as an equippable bauble.

#### `DedicatedBaubleItem`

The base class for new items whose main function is to serve as baubles. It implements `IBaubleRegistryEntry`, allowing it to be directly put into registry.

#### `BaubleProcessingArgs`

A record of a bauble's specific environment when processing it's effects, including the specific `ItemStack`, the equipping `Mob` and the slot key it's equipped in.