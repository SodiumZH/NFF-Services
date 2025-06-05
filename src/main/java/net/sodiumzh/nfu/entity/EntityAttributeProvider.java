package net.sodiumzh.nfu.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.sodiumzh.nfu.registry.NFURegistries;
import org.w3c.dom.Attr;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An {@code EntityAttributeProvider} is a supplier of {@link AttributeSupplier.Builder}. It allows to pre-register
 * attributes instead of defining a static method for each entity type.
 * <p>Register-able. In {@link NFURegistries#ENTITY_ATTRIBUTE_PROVIDERS}.
 */
public class EntityAttributeProvider implements Supplier<AttributeSupplier.Builder> {

    private final Supplier<AttributeSupplier.Builder> base;
    private final Map<Attribute, Supplier<Double>> values = new HashMap<>();

    private EntityAttributeProvider(Supplier<AttributeSupplier.Builder> base) {
        this.base = base;
    }

    /**
     * Create based on {@link LivingEntity#createLivingAttributes()}.
     */
    public static EntityAttributeProvider living() {
        return new EntityAttributeProvider(LivingEntity::createLivingAttributes);
    }

    /**
     * Create based on {@link Mob#createLivingAttributes()}.
     */
    public static EntityAttributeProvider mob() {
        return new EntityAttributeProvider(Mob::createMobAttributes);
    }

    /**
     * Create based on {@link Monster#createLivingAttributes()}.
     */
    public static EntityAttributeProvider monster() {
        return new EntityAttributeProvider(Monster::createMobAttributes);
    }

    /**
     * Create based on {@link Zombie#createLivingAttributes()}.
     */
    public static EntityAttributeProvider zombie() {
        return new EntityAttributeProvider(Zombie::createMobAttributes);
    }

    /**
     * Create based on another {@link Supplier} of {@link AttributeSupplier.Builder}, including
     * another {@link EntityAttributeProvider}.
     * This operation will not impact the input supplier but only calls its {@link Supplier#get} method.
     */
    public static EntityAttributeProvider from(Supplier<AttributeSupplier.Builder> other) {
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

    public EntityAttributeProvider apply(Consumer<EntityAttributeProvider> action) {
        action.accept(this);
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
