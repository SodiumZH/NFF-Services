package net.sodiumzh.nfu.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.entity.AttachedItemDisplayerEntity;
import net.sodiumzh.nfu.entity.NFUEffectZoneEntity;
import net.sodiumzh.nfu.entity.NFUItemProjectileEntity;

public class NFUEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
        NFULibrary.MOD_ID);

    public static final RegistryObject<EntityType<AttachedItemDisplayerEntity>> ATTACHED_ITEM_DISPLAYER =
        ENTITY_TYPES.register("attached_item_displayer", () -> EntityType.Builder
            .of(AttachedItemDisplayerEntity::new, MobCategory.MISC)
            .sized(0.25f, 0.25f)
            .noSave()
            .noSummon()
            .updateInterval(1)
            .build(new ResourceLocation(NFULibrary.MOD_ID, "attached_item_displayer").toString()));

    public static final RegistryObject<EntityType<NFUItemProjectileEntity>> DEFAULT_ITEM_PROJECTILE
        = ENTITY_TYPES.register("default_item_projectile", () -> EntityType.Builder
        .of(NFUItemProjectileEntity::new, MobCategory.MISC)
        .sized(0.25f, 0.25f)
        .noSave()
        .noSummon()
        .updateInterval(1)
        .build(new ResourceLocation(NFULibrary.MOD_ID, "default_item_projectile").toString()));

    public static final RegistryObject<EntityType<NFUEffectZoneEntity>> DEFAULT_EFFECT_ZONE
        = ENTITY_TYPES.register("default_effect_zone", () -> EntityType.Builder
        .of(NFUEffectZoneEntity::new, MobCategory.MISC)
        .sized(1f, 1f)
        .noSave()
        .noSummon()
        .updateInterval(1)
        .build(new ResourceLocation(NFULibrary.MOD_ID, "default_effect_zone").toString()));
}
