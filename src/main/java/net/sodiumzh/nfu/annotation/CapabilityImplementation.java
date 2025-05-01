package net.sodiumzh.nfu.annotation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

/**
 *
 */
public @interface CapabilityImplementation {

    public Class<?>[] caps() default {};
    public static final Class<?>[] EXCLUDED = {Item.class, Entity.class, Block.class, BlockEntity.class};
}
