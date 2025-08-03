package net.sodiumzh.nfu.annotation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Label this class is an implementation of a forge capability.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CapabilityImplementation {

    public Class<?>[] caps() default {};
    public static final Class<?>[] EXCLUDED = {Item.class, Entity.class, Block.class, BlockEntity.class};
}
