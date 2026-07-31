# NFF Services — Architecture Overview (AI Reference)

> Purpose of this file: a concise, high-level map of the NFF Services framework
> for AI agents to quickly understand what the mod does and how it is organized.
> It describes the architecture and functionalities, not implementation details.
>
> Reflects the `0.2.33` "de-legacy" architecture: as of 0.2.33 the framework was
> refactored off Forge **Capabilities** onto the NFU library's **Entity Component
> System (ECS)**. See the "Entity Component System" section below.

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
   Registered wild types automatically receive their tamable components.
2. Each mapping references an **`NFFTamingProcess`** — a single‑instance handler
   (like `Item`/`Block`, with registry `NFFRegistries.TAMING_PROCESSES`) that
   defines *how* the mob is tamed (e.g. giving items, instant tool, progress‑based).
3. On successful taming the wild mob is removed, a new mob of the *tamed* type is
   spawned in place, its data is merged over, and the tamed mob is assigned an owner.
   The tamed type is a separate `EntityType` that looks identical but no longer has a
   "wild" state — it must always have an owner.

Which mobs are treated as "tamed" is decided by **`NFFTamedTypeRegistry`**: it maps a
mob `EntityType` to an accessor `Function<Mob, INFFTamed>` (the built‑in `SELF`
accessor casts mobs that directly implement `INFFTamed`). This registry replaces the
old `instanceof INFFTamed` checks and lets the ECS attach the right components to
tamed vs. tamable mobs.

## Key components

- **`INFFTamed`** — central interface for tamed/companion mobs. Extends
  `OwnableEntity`; provides owner handling, AI‑state switching, inventory access,
  GUI opening, common‑data access, and helper queries. Implemented on dedicated
  "tamed" mob classes (or via mixin in special cases). Data/behavior are delegated
  to entity components, reached through a facade **`NFFTamedDataAccessor`**
  (`getDataAccessor()`). Lifecycle hooks `onInitialize()` and `onTamed(...)` replace
  the old explicit init calls.
- **`NFFTamingProcess`** — abstract taming handler. Notable subclasses include
  item‑giving processes (`TamingProcessItemGiving`, progress‑based
  `TamingProcessItemGivingProgress`). Handles interaction results and anger checks.
- **`NFFTamingMapping`** — registry of wild→tamed type mappings and their processes.
- **`NFFTamedTypeRegistry`** — maps mob `EntityType`s to their `INFFTamed` accessor;
  drives component attachment and replaces `instanceof` checks.
- **Presets** (`entity/taming/presets/`) — reference tamed implementations
  (e.g. `NFFTamedCreeperPreset`, `NFFTamedEnderManPreset`).

## Entity Component System (ECS)

As of 0.2.33 ("de‑legacy"), per‑mob state and behavior are no longer stored in Forge
**Capabilities**; they live in **NFU entity components**
(`net.sodiumzh.nfu.entity.component.*`). Each entity carries a tree of components
addressed by **`HierarchyPath`** strings (e.g. `/nff/tamable/...`, `/nff/tamed/...`).
Component classes extend `EntityComponentBase`; each has an `EntityComponentType`
registered in NFU's `ENTITY_COMPONENT_TYPES` registry. A component can declare
required sub‑components and allowed paths, exposes lifecycle hooks (`tick`,
`joinLevel`, NBT serialize/deserialize), and is bound to a side via `AvailableSide`
(SERVER / CLIENT / BOTH).

NFF registers its component types and hierarchy paths in **`NFFEntityComponents`**,
which also holds typed `SubComponentAccessor`s and, on an `EntityComponentSetupEvent`
listener, attaches the correct components depending on whether a mob is tamable,
tamed, or neither.

**Tamable‑side components** (server‑only, under `/nff/tamable`):
- `NFFTamableComponent` — main handler (holds the taming process, hostility, etc.).
- `NFFTamableDataComponent` — general + per‑player serialized NBT.
- `NFFTamableTimerComponent` / `EntityTimerComponent` — timers.
- `NFFTamableAngerHandlerComponent` — mob anger toward players (can interrupt taming).

**Tamed‑side components** (under `/nff/tamed`):
- `NFFTamedDataComponent` — persistent data (e.g. initial `EntityType`, sun immunity).
- `NFFTamedSyncherComponent` — client‑**synched** data: stable identifier, owner
  UUID/name, encounter date, AI state, attack target, additional inventory.
- `NFFTamedInventoryComponent` — the mob's inventory, synced back to the mob on tick.
- shared `HealingHandlerComponent` and (all mobs) `EntityItemStackMonitorComponent`.

Custom synched types are registered via **`NFFDataSerializers`** (NFU
`NFUDataSerializer`s, e.g. the tamed‑mob inventory serializer).

Legacy Forge capabilities are essentially gone (`NFFCapRegistry` retains only a
deprecated level cap). Old 0.x.32 save data is migrated into the new components by
**`DataPort33`** (a temporary porting listener, slated for removal in 0.x.34).

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
`NFFMobTamedEvent`, `NFFTamedDataConstructEvent`, `NFFTamedSyncherConstructEvent`,
`NFFTamedDeathEvent`, `NFFTamedDropRespawnerOnDyingEvent`, AI events
(`NFFTamedChangeAiStateEvent`, `NFFGoalCheckCanUseEvent`), respawner lifecycle
events, level‑module tick events, and the setup event
`NFFTamingMappingRegisterEvent`. `BMHooks` centralizes common hook calls; listeners
live under `eventlistener/`.

## Package map (`net.sodiumzh.nff.services`)

- `entity/taming` — taming mappings, processes, `INFFTamed`, entity components
  (`NFF*Component`), `NFFTamedDataAccessor`, `NFFTamedTypeRegistry`, presets, and the
  legacy `CNFFTamedCommonData` (kept for data porting).
- `entity/ai` + `entity/ai/goal` (+ `preset`, `preset/target`) — AI states and goals.
- `item` (+ `event`) — catcher, respawner, tamer, ownership items.
- `inventory` — mob containers and menu.
- `registry` — `NFFEntityComponents` (ECS types/paths), `NFFDataSerializers`,
  `NFFItemRegistry`, `NFFRegistries`, `NFFTagRegistry`, `NFFCapabilityAttachments`,
  and the now‑minimal `NFFCapRegistry`.
- `level`, `network`, `event`, `eventlistener`, `client/gui` — supporting systems
  (level module, packets, events/listeners incl. `DataPort33`, client screens).

## Notes for future sessions

- **0.2.33 refactor:** the biggest change is the move from Forge Capabilities to the
  NFU **Entity Component System**. When reasoning about per‑mob data, look at the
  `NFF*Component` classes and `NFFEntityComponents`, not old `CNFF*` capabilities.
- `NFFTamedDataAccessor` is the convenient facade `INFFTamed` uses to read/write the
  underlying tamed components; `NFFTamedTypeRegistry` decides which mobs are tamed.
- Legacy namespace `befriendmobs` is redirected to `nffservices` at load time
  (`SaveDataLocationRedirector`); `DataPort33` migrates 0.x.32 save data into the new
  components and is temporary (removal planned in 0.x.34).
- This is a framework: most gameplay content (actual tamed mobs, taming items) is
  expected to be provided by downstream mods that register types, processes and
  mappings against these APIs.
- A developer‑facing tutorial exists at `wiki/docs_en.md`; the changelog at
  `wiki/changelog.md`. It also depends on the sibling **NFU‑Library**
  (`net.sodiumzh.nfu`) for the ECS and shared utilities.
