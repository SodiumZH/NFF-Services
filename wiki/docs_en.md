# NFF Services Tutorial

By SodiumZH

## Overview

NFF Services is a framework for making taming features for any mobs, for example, Zombies or other mod's hostile mobs.

Usually, tamable mobs or companions uses vanilla `TamableAnimal` class. However, if you want to tame an existing mob of any type (e.g. `Zombie`), the vanilla `TamableAnimal` will not be available as its type hierarchy is fixed under `Animal` but `Zombie` is under `Monster`, and you may want to use an interface to enable taming / companions on any type of mob despite its type hierarchy.

NFF Services framework provides an interface `INFFTamed`, and you can implement this interface on any type of mobs. It also provide other functionalities related to taming and companion mobs.

## Quick Start

### Making a monster tamable

To make an originally untamable mob tamable, you need to do things below:

#### Creating a tamed mob class

First, create a class, extend the original class (e.g. `Zombie`) and implement the interface `INFFTamed`. This class is *dedicated* to be the tamed version of the mob, just like:

If a mob class is declared to implement `INFFTamed`, many things will be done automatically (described in each section). It also include vanilla `OwnableEntity`. 

Note that unlike `TamableAnimal`, `INFFTamed` mob *must* have an owner, otherwise it will cause crash. (This restriction may be removed in the future. I hope that `INFFTamed` can perform as a complete interface version of `TamableAnimal`).

If you have to apply `INFFTamed` on an existing class, you can use mixin to implement the interface. This operation is not recommended, as `INFFTamed` represents mobs that are necessarily being someone's companion, and it will not have a "wild" state. An exception is when the original class is already a dedicated tamed mob class, and you want to enable the functionalities of `INFFTamed` on it.

#### Do necessary registrations for the tamed mob

NFF Services framework uses a "replacement" mechanic on taming. That is, the tamed version will be regarded as another type of mob, although it looks exactly the same as the original one. When tamed, the wild mob will be removed, a tamed mob will be spawned in place, and the data will be merged to the new mob. As a new entity type, the tamed version doesn't necessary inherit the "wild" mob's properties.

##### Register entity type

Use Forge `DeferredRegister` to register the tamed mob as a new `EntityType`. Indeed, you can copy the original mob's registration here, but not necessarily.

##### Register attributes

Listen to Forge `EntityAttributeCreationEvent` and register its default attributes. You can but don't have to copy the original mob's attributes.

If you need to read some properties that are not available on Forge attribute registration (e.g. config values), you can consider `DeferredEntityAttributeRegisterEvent` in Sodium's Utilities library (0.x.28+). This event collects suppliers of attributes but doesn't directly run them to generate attributes. Instead, it will register the attributes to forge on server setup when most properties are already loaded. 

##### Register translation

Go to `assets/lang` and add translations of the tamed mob's name using the tamed mob's entity type key. As it's a new type, the original mob's name will not be inherited. (Auto name inheritance may be added in the future.)

##### Register models

Listen to Forge `EntityRenderersEvent.RegisterRenderers` event and register the mob's renderer. Given the type hierarchy, you can directly use the original mob's renderer. However, if you don't extend the original mob's class when creating the tamed version for some reason, you have to copy-paste the whole renderer, model and layers classes from the original mob's code.

#### Make the original mob tamable

NFF Services framework uses a Forge Capability `CNFFTamable` for tamable (original) mobs, and uses `NFFTamingProcess` to handle how a mob should be tamed.

##### Configure the taming process

The `NFFTamingProcess` is a handler for how the mob should be tamed. They are single instances (i.e. there will be only one global instance for each `NFFTamingProcess` type which handles all related mobs,  just like `Item` and `Block`), and has a registry (`NFFRegistries#TAMING_PROCESSES`).

For how to create a taming process, see the "Taming Process" section. Now we just create a subclass of `NFFTamingProcess` and let `handleInteract` return `new TamableInteractionResult()`. This means you can tame the mob only by the instant taming tool (item key:`nffservices:instant_taming_tool`). This debugging item can directly tame any tamable mobs in NFF Services.

##### Register taming mapping

You don't need to attach any capabilities manually. Instead, you need to specify which type of mob can be tamed, which type is the tamed version of this mob, and how this mob can be tamed. This can be done by calling `NFFTamingMapping#register` to map the original, tamed mob type and an `NFFTamingProcess`in mod setup (e.g. mod main class' constructor, or on `FMLCommonSetupEvent`). Once registered as the tamable mob, the necessary capabilities will be automatically attached.

## Taming Process

### Overview

A taming process (`NFFTamingProcess`) is a handler to define how a tamable mob can be tamed. It's a single instance (just like `Block` and `Item`) and has a registry (`NFFRegistries#TAMING_PROCESSES`).





## `CNFFTamedMobData`

`CNFFTamedMobData` is an additional Capability attached on all mobs which implement `INFFTamed`. It contains all common data for befriended mobs.

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