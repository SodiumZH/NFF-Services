package net.sodiumzh.nfu.math;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.annotation.MustBeRegistered;
import net.sodiumzh.nfu.registry.NFURegistry;
import net.sodiumzh.nfu.util.NFUMathStatics;

import java.util.function.UnaryOperator;

/**
 * A {@code IFieldPattern3D} defines a function from a 3D space position to a Vec3 value. It can be used to describe a
 * force field in a 3D space. Note that it only describes a non-linear field shape and should not include linear transforms.
 * Linear transforms are handled in {@link Field3D}. Usually it should be defined within (-1, -1, -1) to (1, 1, 1) to be
 * easily mapped to any bounding boxes.
 * <p> It should be single-instance and registered. Entities use the registry key to sync it between sides.
 */
@MustBeRegistered
@FunctionalInterface
public interface IFieldPattern3D extends UnaryOperator<Vec3> {

    public default Field3D field() {
        return new Field3D(this);
    }

    public static NFURegistry<IFieldPattern3D> REGISTRY =
        new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "field_patterns_3d"));

    public static NFURegistry.Accessor<IFieldPattern3D> ZERO =
        REGISTRY.register(new ResourceLocation(NFULibrary.MOD_ID, "zero"), () -> (v -> Vec3.ZERO));

    public static NFURegistry.Accessor<IFieldPattern3D> IDENTITY =
        REGISTRY.register(new ResourceLocation(NFULibrary.MOD_ID, "identity"), () -> (v -> v));

    /**
     * Returns a random unit vector (length == 1) at any position. Strictly this is not a field defined in physics.
     */
    public static NFURegistry.Accessor<IFieldPattern3D> RANDOM_UNIT =
        REGISTRY.register(new ResourceLocation(NFULibrary.MOD_ID, "random_unit"), () ->
            (v -> NFUMathStatics.randomUnitVector()));

    /**
     * Returns a random vector with Gaussian-distributed length at any position. Strictly this is not a
     * field defined in physics.
     */
    public static NFURegistry.Accessor<IFieldPattern3D> RANDOM_GAUSSIAN =
        REGISTRY.register(new ResourceLocation(NFULibrary.MOD_ID, "random_gaussian"), () ->
            (v -> NFUMathStatics.randomUnitVector().scale(NFUMathStatics.RND.nextGaussian())));
}
