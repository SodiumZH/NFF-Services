# NFF Services — Architecture Overview (AI Reference)

> Purpose of this file: a concise, high-level map of the NFF Services framework
> for AI agents to quickly understand what the mod does and how it is organized.
> It describes the architecture and functionalities, not implementation details.

## What this mod is

**NFF Services** (Natrium Friending Framework, mod id `nffservices`, legacy id
`befriendmobs`) is a **Minecraft Forge (1.20.1) gameplay framework/library**, not a
content mod. It provides the infrastructure for **taming arbitrary mobs** — including
hostile or third‑party mod mobs — and turning them into player‑owned companions.

The core problem it solves: vanilla only supports taming through `TamableAnimal`,
whose class hierarchy is locked under `Animal`. NFF Services replaces this with the
interface **`INFFTamed`**, which can be applied to *any* mob type regardless of its
class hierarchy (e.g. a `Zombie` or `Creeper`), plus all the surrounding systems
(AI, inventory, GUI, persistence, networking) needed for companions.

It depends on the sibling **NFU** library (`net.sodiumzh.nfu`, "Natrium Friending
Utilities" / Sodium's Utilities) for shared capability, entity, registry and
networking helpers. An example content mod built on it is **NFF‑Girls**.

## Core taming model

Taming uses a **"replacement" mechanic** rather than mutating the wild mob:

1. A wild mob type is registered as *tamable* and mapped to a distinct *tamed* mob
   type via **`NFFTamingMapping.register(from, convertTo, processSupplier)`**.
   Registered wild types automatically receive the `CNFFTamable` capability.
2. Each mapping references an **`NFFTamingProcess`** — a single‑instance handler
   (like `Item`/`Block`, with registry `NFFRegistries.TAMING_PROCESSES`) that
   defines *how* the mob is tamed (e.g. giving items, instant tool, progress‑based).
3. On successful taming the wild mob is removed, a new mob of the *tamed* type is
   spawned in place, its data is merged over, and the tamed mob is assigned an owner.
   The tamed type is a separate `EntityType` that looks identical but no longer has a
   "wild" state — it must always have an owner.

## Key components

- **`INFFTamed`** — central interface for tamed/companion mobs. Extends
  `OwnableEntity`; provides owner handling, AI‑state switching, inventory access,
  GUI opening, common‑data access, and helper queries. Implemented on dedicated
  "tamed" mob classes (or via mixin in special cases).
- **`NFFTamingProcess`** — abstract taming handler. Notable subclasses include
  item‑giving processes (`TamingProcessItemGiving`, progress‑based
  `TamingProcessItemGivingProgress`). Handles interaction results and anger checks.
- **`NFFTamingMapping`** — registry of wild→tamed type mappings and their processes.
- **Presets** (`entity/taming/presets/`) — reference tamed implementations
  (e.g. `NFFTamedCreeperPreset`, `NFFTamedEnderManPreset`).

## Capabilities (persistent per‑entity/level/player data)

Registered in `NFFCapRegistry`; several tick each game tick.

- **`CNFFTamable`** — attached to wild tamable mobs; also handles mob **anger**
  (can get angry at players, interrupting taming). Stores general and
  per‑player NBT.
- **`CNFFTamedCommonData`** — common data on every `INFFTamed` mob: a stable
  identifier (survives death/respawn), initial entity type, additional serialized
  NBT, non‑serialized temp objects, and periodically **synched** data. Owner info.
- Support capabilities: `CHealingHandler`, `CAttributeMonitor`, `CItemStackMonitor`,
  `CLivingEntityDelayedActionHandler`, plus player‑scope `CNFFPlayerModule` and
  level‑scope `CNFFLevelModule` (server‑side serializable per‑level state).

## AI system

- **`NFFTamedMobAIState`** — extensible mob "mode" (built‑ins: `WAIT`, `FOLLOW`,
  `WANDER`), cycled via a `CyclicSwitch`. Owners toggle the state.
- **`NFFGoal`** (base, implements `INFFTamedGoal`) — goals are gated by which AI
  states they are allowed in, and support extras like skip chance, interrupt chance,
  and "require owner present". Wrappers/adapters exist for vanilla goals
  (`NFFGoalWrapper`, `NFFMoveGoal`, `NFFTargetGoal`).
- A large **preset goal library** (`entity/ai/goal/preset/`): follow‑owner (ground,
  flying, in‑water), pathfinding/stroll/swim, melee/ranged/bow attacks, projectile
  shooting, leap attacks, block actions, sun‑sensitivity (flee/restrict), amphibious
  and flying movement; and target goals (owner‑hurt / owner‑hurt‑by / hurt‑by /
  nearest attackable / nearest unfriendly mob).

## Items & companion management

Registered in `NFFItemRegistry` (framework ships mainly debug/utility items):

- **`NFFInstantTamerItem`** (`instant_taming_tool`) — debug item that instantly tames
  any tamable mob.
- **`MobCatcherItem`** — captures a companion mob into a respawner item (configurable
  conditions, invulnerability, void recovery).
- **`NFFMobRespawnerItem`** / **`NFFMobRespawnerInstance`** — store a mob as an item
  and respawn it later, optionally retaining inventory.
- **`NFFMobOwnershipTransfererItem`** — transfer ownership of a companion.

## Inventory & GUI

- **`NFFTamedMobInventory`** (+ `WithEquipment`, `WithHandItems` variants) — per‑mob
  container, syncs back to the owning mob on change.
- **`NFFTamedInventoryMenu`** + client **`NFFTamedGUI`** screen, with a GUI
  constructor registry for custom per‑mob screens.

## Networking

- **`NFFChannels`** — a Forge `SimpleChannel` (`bm_channel`). Clientbound packets:
  open tamed GUI, tamed‑mob init, and common‑data sync.

## Event system

Custom Forge‑bus events let content mods hook the lifecycle, e.g.
`NFFMobTamedEvent`, `NFFTamedCommonDataConstructEvent`, `NFFTamedDeathEvent`,
`NFFTamedDropRespawnerOnDyingEvent`, AI events (`NFFTamedChangeAiStateEvent`,
`NFFGoalCheckCanUseEvent`), respawner lifecycle events, level‑module tick events,
and the setup event `NFFTamingMappingRegisterEvent`. `BMHooks` centralizes common
hook calls; listeners live under `eventlistener/`.

## Package map (`net.sodiumzh.nff.services`)

- `entity/taming` — taming mappings, processes, `INFFTamed`, common data, presets.
- `entity/capability` — entity capabilities (tamable, healing, attribute/item monitors).
- `entity/ai` + `entity/ai/goal` (+ `preset`, `preset/target`) — AI states and goals.
- `item` (+ `capability`, `event`) — catcher, respawner, tamer, ownership items.
- `inventory` — mob containers and menu.
- `level`, `network`, `registry`, `event`, `eventlistener`, `client/gui` — supporting
  systems (level module, packets, registries, events/listeners, client screens).

## Notes for future sessions

- Legacy namespace `befriendmobs` is redirected to `nffservices` at load time
  (`SaveDataLocationRedirector`), so old class/capability/save keys still resolve.
- This is a framework: most gameplay content (actual tamed mobs, taming items) is
  expected to be provided by downstream mods that register types, processes and
  mappings against these APIs.
- A developer‑facing tutorial exists at `wiki/docs_en.md`; the changelog at
  `wiki/changelog.md`.
