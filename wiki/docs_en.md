# BefriendMobs API Tutorial

## Overview

BefriendMobs API is intended to help modders easily create features of befriending (or say taming) mobs and friendly mobs despite their type hierarchy.

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



## Create a minimal befriended mob class

1. Create a mob class (anything extends `LivingEntity`; `Monster` or `Animal` recommended) and **implement `IBefriendedMob` interface**. Register its entity type, client renderer, attributes, etc. (Similar as creating any other mob classes.)

2. Copy-paste the code in a template class in `net.sodiumstudio.befriendmobs.template`. The comments in the template code will tell you how to implement the methods as minimal.

3. (Optional if you need GUI) Create the inventory menu. Inventory menu is for adding the mob's inventory into the GUI. Inventory menu for befriended mob **inherits `BefriendInventoryMenu` class**. In this class you need to override `addMenuSlots` method for the inventory of your mob. 

   By default, after running `addMenuSlots`, the player inventory will be automatically added just like in the vanilla inventory screen, at the position specified by `getPlayerInventoryPosition` method which you need to override. If player inventory is not needed, override `doAddPlayerInventory` to false.

   Finally, override `IBefriendedMob#makeMenu` method to supply the inventory menu on opening inventory. The syntax is provided in templates.

4. Configure GUI. The GUI screen class for Befriended Mobs **inherit `BefriendedGuiScreen` class**. You must override `getTextureLocation` method to specify the texture resource location, and override `renderBg` to make the GUI background. Generally `render`method doesn't need to be overridden, but if you need some features other than the inventory and mob rendering (e.g. buttons), you need to manually implement them (including pack sending).

   Don't forget to ensure inventory menu and GUI are well aligned.

5. Add opening GUI action. Go back to the mob class and use `BefriendedHelper.openBefriendedInventory` to open the GUI. Please note that this method is only executed on SERVER and send pack to client to execute GUI opening. Also, ensure the `makeMenu` method doesn't return null, or the game will crash.

6. Map inventory menu and GUI. Listen to `FMLCommonSetupEvent` and use `BefriendedGuiScreenMaker.put()` method to register GUI constructing method. This function receives 2 parameters, the first is the class of the menu class, and the latter is a function from the menu instance to a new GUI instance. Like this: 

   ```java
   BefriendedGuiMaker.put(MyInventoryMenu.class, menu -> new MyGui(...));
   ```

   

Warning: GUI isn't available on dedicated server before (not including) 1.18.2-0.0.4 and 1.19.2-0.1.4 due to a bug. The legacy method of adding GUI is removed and please use 1.18.2-0.0.4 & 1.19.2-0.1.4 or above instead. 

## Additional module presets in `IBefriendedMob`

Firstly: **DO NOT** override methods labeled with `@DontOverride` annotation (unless you clearly know what you're doing). Usually the default implementations of these methods are related to some automatic actions in BefriendMobs internal implementation. If they're overridden incorrectly something unexpected may happen. 

### AI State

AI State (`BefriendedAIState` enum) indicates which type of preset AI this mob should use. With this value the mob will be able to use `BefriendedGoal` and `BefriendedTargetGoal` which adapts vanilla `Goal` and `TargetGoal` to `IBefriendedMob`. There are also presets porting some general vanilla goals as `BefriendedGoal` or `BefriendedTargetGoal`. 

AI State can be switched during gameplay using `IBefriendedMob#setAIState` or `IBefriendedMob#switchAIState`. The AI goals will automatically adapt to the AI State change. There are 3 default presets: `WAIT`, `FOLLOW` and `WANDER`; other custom AI States need to manually implement. (This preset is inspired by *Alex's Mobs*.)

`IBefriendedMob#switchAIState` is intended to create a switching loop e.g. wait -> follow -> wander -> wait (the default loop). If you need to adjust, override `getNextAIState` method.

### Wandering Anchor

Wandering Anchor system is for preventing the mob to wander too far away. By default it's enabled with wandering radius 64 (the mob will not wander out of this distance). If you need to disable it, simply override `getAnchorPos` method to `null` (not zero), and everything about the mob's wandering anchor will stop. To adjust the radius, override `getAnchoredStrollRadius` to the desired value.

Wandering Anchor only applies on X and Z (horizontal) distances and ignores heights.

### Healing Handler

Healing Handler simplifies the feature about using some items to the mob to heal it. By default it doesn't do anything.

To add healing items, override `getHealingItems` and add items and corresponding healing amounts. If you need the item not to consume after healing the mob, add them in `getNonconsumingHealingItems` override. (These items must also be present in `getHealingItem`.)

To apply item on mob, simply call `tryApplyHealingItems`. It will test if the item is acceptable, and if so heal the mob and optionally consume the item. The method returns `InteractionResult.SUCCESS` if used successfully, `PASS` if item not acceptable, and `FAIL` if the item is acceptable but `IBefriendedMob#applyHealingItem` returns false. It also send glint particles on success and smoke particles on failure.

The implementation of Healing Handler is defined in `CHealingHandler` capability implementation. If you need any behaviors other than the features above, you need to firstly implement `CHealingHandler` capability to change the behaviors on applying items to mobs (see `CHealingHandler` and `CHealingHandlerImpl` for detail), and override `IBefriendedMob#healingHandlerClass` to the new implementation. 

### Respawner

Mob Respawner is an item similar to Spawn Eggs containing all the mob's data. By default Befriended Mobs drop respawner on death. The dropped respawner entities are invulnerable (unless to creative players and /kill command), not expiring, and will be lifted up on dropping into the void. To close respawner features, simply override `shouldDropRespawner` to false. To disable the protection features of item entities, override the corresponding method.

## How to configure a befriendable mob

1) Register a mapping between befriendable mob type, corresponding befriended mob and a befriending handler which defines the befriending process.

   To register the mapping, use:

   `BefriendingTypeRegistry.register(YourBefriendableMob.getType(), YourBefriendedMob.getType(), new YourBefriendingHandler());`

   This action maps the type of the befriendable mob, the befriended mob and the befriending process handler types (described below). 

2)  Create a handler class inheriting `BefriendingHandler` and override methods in which you define all behaviors on interacting with mobs of a specific class (defined in step 1 with type mapping). In these overridden methods you can add very complex logic about befriending the mobs. Use `Befriend` in the handler class to finally befriend the mob. (By default it will instantly convert the mob to the corresponding Befriended Mob and do data sync.) For application examples see *Days with Monster Girls* example project.

   Please note that if you have a "Debug Befriender" item (`befriendmobs:debug_befriender`) on the main hand, the interaction method in the handler will NOT be executed, and the target mob will be instantly befriended. (i.e. directly call the `Befriend` method.)

## AI Goals

Docs under construction

## Bauble System

Docs under construction