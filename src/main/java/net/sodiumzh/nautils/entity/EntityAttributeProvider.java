package net.sodiumzh.nautils.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.sodiumzh.nautils.statics.NaUtilsReflectionStatics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntityAttributeProvider implements Supplier<AttributeSupplier.Builder> {

    private final Supplier<AttributeSupplier.Builder> base;
    private final Map<Attribute, Supplier<Double>> values = new HashMap<>();

    private EntityAttributeProvider(Supplier<AttributeSupplier.Builder> base) {
        this.base = base;
    }

    /**
     * Create based on {@link LivingEntity#createLivingAttributes()}.
     */
    public EntityAttributeProvider living() {
        return new EntityAttributeProvider(LivingEntity::createLivingAttributes);
    }

    /**
     * Create based on {@link Mob#createLivingAttributes()}.
     */
    public EntityAttributeProvider mob() {
        return new EntityAttributeProvider(Mob::createMobAttributes);
    }

    /**
     * Create based on {@link Monster#createLivingAttributes()}.
     */
    public EntityAttributeProvider monster() {
        return new EntityAttributeProvider(Monster::createMobAttributes);
    }

    /**
     * Create based on {@link Zombie#createLivingAttributes()}.
     */
    public EntityAttributeProvider zombie() {
        return new EntityAttributeProvider(Zombie::createMobAttributes);
    }

    /**
     * Create based on another {@link Supplier} of {@link AttributeSupplier.Builder}, including
     * another {@link EntityAttributeProvider}.
     * This operation will not impact the input supplier but only calls its {@link Supplier#get} method.
     */
    public EntityAttributeProvider from(Supplier<AttributeSupplier.Builder> other) {
        return new EntityAttributeProvider(other);
    }

    public EntityAttributeProvider add(Attribute attribute, Double value) {
        values.put(attribute, () -> value);
        return this;
    }

    public EntityAttributeProvider add(Attribute attribute, Supplier<Double> valueSupplier) {
        values.put(attribute, valueSupplier);
        return this;
    }

    @Override
    public AttributeSupplier.Builder get() {
        AttributeSupplier.Builder builder = this.base.get();
        for (var entry: values.entrySet()) {
            builder.add(entry.getKey(), entry.getValue().get());
        }
        return builder;
    }
}
