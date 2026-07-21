# NFF-Services — AI Agent Guide

> Machine-readable orientation document for AI coding agents. Describes what this
> repository is, how it is built, and how it relates to the other Natrium projects.
> **Active branch: `1.20.1-0.2.33-de-legacy`** (study this, not the default `1.20.1`).

## 1. Identity

| Field | Value |
|-------|-------|
| Name | NFF Services (Natrium Friending Framework) |
| Repo | `The-Natrium-Projects/NFF-Services` |
| Mod ID | `nffservices` |
| Legacy Mod ID | `befriendmobs` (migrated from) |
| Root package | `net.sodiumzh.nff.services` |
| Maven group | `net.sodiumzh.nff.services` |
| Language | Java 100% (Java 17 toolchain) |
| Version | `0.2.33` (`filename_version = 0.2.33-dev`) |
| License | LGPL-3.0 |
| Author | SodiumZH |

## 2. Purpose

NFF-Services is a **framework / development toolkit for befriending (taming)
existing mobs** in Minecraft. It provides the taming mechanics, AI, entity
components, capabilities, and save-data infrastructure that content mods use to
turn vanilla/modded mobs into friendly companions. It ships no monster-girl
content itself — that lives in NFF-Girls.

## 3. Dependency Position

```
NFU-Library  (base utilities)
    ^
    |
NFF-Services (THIS repo — depends on NFU-Library)
    ^
    |
NFF-Girls    (depends on NFU-Library + NFF-Services)
```

NFF-Services is the **middle layer**: it consumes NFU-Library and is consumed by
NFF-Girls.

### How the NFU-Library dependency is wired

`build.gradle`:
```gradle
flatDir {
    dir "libs"
    dir "../NFU-Library/build/libs"   // sibling checkout of NFU-Library
}
...
implementation fg.deobf("blank:nfulib-${minecraft_version}:${nfu_version}")
```
`gradle.properties`:
```
nfu_version=0.2.33-dev
nfu_version_range=[0.2.33, 0.2.34)
```
Build NFU-Library first; its jar in `../NFU-Library/build/libs` is required.

## 4. Tech Stack

- **Loader:** Minecraft Forge `47.1.44` for **Minecraft 1.20.1**.
- **Build:** Gradle + ForgeGradle `[6.0,6.2)`, ParchmentMC mappings
  (`2023.06.26-1.20.1`), Java 17.
- **JarJar** enabled. **JEI** `15.2.0.27` declared as optional integration.
- Produces `nffservices-1.20.1-<version>` jars.

## 5. Source Layout

Root package `src/main/java/net/sodiumzh/nff/services/`:

| Package | Responsibility |
|---------|----------------|
| `NFFServices.java` | Mod entry (`@Mod("nffservices")`); registers items, data serializers, entity components; migrates the legacy `befriendmobs` namespace via `SaveDataLocationRedirector` (from NFU-Library). |
| `client` | Client-side services (rendering/UI hooks). |
| `entity` | Core befriending entities & data. Contains `NFFMobRespawnInfo`, plus sub-packages `entity/ai` (goals/behaviors) and `entity/taming` (taming logic). |
| `event` | Custom framework events. |
| `eventlistener` | Forge event listeners driving framework behavior. |
| `inventory` | Companion mob inventories/menus. |
| `item` | Framework items (e.g. taming/utility items). |
| `level` | Level-scoped framework state. |
| `network` | Client/server packets. |
| `registry` | Registration: `NFFItemRegistry`, `NFFDataSerializers`, `NFFEntityComponents`, `NFFCapabilityAttachments`. |
| `temp` | Temporary/scratch code (treat as unstable). |

## 6. Legacy Migration Note

This mod was previously `befriendmobs`. `NFFServices#redirectResourceLocations()`
uses NFU-Library's `SaveDataLocationRedirector` to remap the old namespace and
its entity/level capabilities to the new `nffservices` IDs, preserving saves.
Keys are centralized in `NFFCapabilityAttachments` (paired `KEY_*` / `KEY_*_LEGACY`).

## 7. Notes for Agents

- Keep the public taming/friending API stable — NFF-Girls binds to it directly.
- When adding capabilities/serializers, register them in the `registry` package
  and, if migrating from legacy, add a redirect in `redirectResourceLocations()`.
- Version props (`nff_version`, `nfu_version_range`) live in `gradle.properties`.
- The default GitHub branch is `1.20.1`; active development is on
  `1.20.1-0.2.33-de-legacy`. Always target the active branch.
