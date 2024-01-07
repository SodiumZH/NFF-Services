# BefriendMobs Framework Tutorial

## Overview

BefriendMobs Framework is intended to help modders easily create features of befriending (or say taming) mobs and friendly mobs despite their type hierarchy.

For example, vanilla tamable mobs (e.g. wolf) inherit `TamableMob` class (except horse and variants). If you want to make hostile mobs (e.g. `Zombie`) friendly or tamed, only using vanilla API it may become complex. 

You can easily create a subclass of `Zombie` (or anything else extends `Mob`) with BefriendMobs API without using any vanilla `TamableMob` interfaces, and without considering about the type hierarchy of the existing mobs.

The tutorial below is a minimal description. For more examples of application, see my depending project *Days with Monster Girls* (GitHub: https://github.com/SodiumZH/Days-with-Monster-Girls).

**WARNING: This lib is still in development. Version updates will make it incompatible to the old versions. It's recommended to specify the lib version in mod.toml instead of providing a version range.** 

## Key terms

### Befriending

A process for acquire ownership of a certain mob. This concept is more commonly called "Taming", but as I designed this system primarily for humanoid mobs (more specifically, mobs in some monster girl mods), let's use the word "Befriending" instead in this system.

### Befriendable Mobs

Existing mobs that can be befriended. Usually they belong to existing classes, generally not providing any methods to allow players to own them.

### Befriended Mobs

The class of a mob after befriending, usually inheriting the corresponding befriendable mob class. It should have a player as owner, and performs more like "tamed" mobs e.g. wolves.

The term Befriended Mob is actually not limited to mobs after befriending. It can be simply created as a friendly mob inheriting mobs of classes you need.

For creating Befriended Mobs, see below. Simply creating a Befriended Mob class are not related to any Befriendable Mobs.

### Befriending Process

A procedure to befriend a mob, i.e. convert a befriendable mob into the corresponding befriended mob. It's highly customizable (see below).



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