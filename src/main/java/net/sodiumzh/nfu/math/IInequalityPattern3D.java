package net.sodiumzh.nfu.math;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.annotation.DontCallManually;
import net.sodiumzh.nfu.annotation.MustBeRegistered;
import net.sodiumzh.nfu.registry.NFURegistry;

import java.util.function.Predicate;

/**
 * Represents an inequality with 3 unknowns represented by a {@link Vec3}. Representing a shape in 3D space.
 * Usually it should be defined within bounding box between (-1, -1, -1) to (1, 1, 1) to be easily mapped to
 * any bounding boxes.
 */
@MustBeRegistered
@FunctionalInterface
public interface IInequalityPattern3D extends Predicate<Vec3> {

    @DontCallManually
    public static void init(){}

    public default Inequality3D inequality() {
        return new Inequality3D(this);
    }

    public static final NFURegistry<IInequalityPattern3D> REGISTRY =
        new NFURegistry<>(new ResourceLocation(NFULibrary.MOD_ID, "inequality_patterns_3d"));

    public static final NFURegistry.Accessor<IInequalityPattern3D> FULL_SPACE = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "full_space"), () -> (v -> true));
    public static final NFURegistry.Accessor<IInequalityPattern3D> BOX = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "box"),
        () -> (v -> Math.abs(v.x) <= 1.0 && Math.abs(v.y) <= 1.0 && Math.abs(v.z) <= 1.0));
    public static final NFURegistry.Accessor<IInequalityPattern3D> SPHERE = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "sphere"),
        () -> (v -> v.lengthSqr() <= 1.0));
    public static final NFURegistry.Accessor<IInequalityPattern3D> CYLINDER = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "cylinder"),
        () -> (v -> v.x * v.x + v.z * v.z <= 1.0));
    /**
     * A cone with center (0,0,0) and include (1,1,0).
     */
    public static final NFURegistry.Accessor<IInequalityPattern3D> CONE = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "cone"),
        () -> (v -> v.x * v.x + v.z * v.z <= v.y * v.y && v.y >= 0));
    /**
     * A cone with center (0,-1,0) and include (1,1,0).
     */
    public static final NFURegistry.Accessor<IInequalityPattern3D> CONE_SLIM = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "cone_slim"),
        () -> v -> CONE.get().inequality().scale(new Vec3(1.0, 2.0, 1.0))
            .translate(new Vec3(0, -1.0, 0)).test(v));

    /**
     * A pyramid with center (0,0,0) and include (1,1,1).
     */
    public static final NFURegistry.Accessor<IInequalityPattern3D> PYRAMID = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "pyramid"),
        () -> (v -> Math.max(v.x * v.x, v.z * v.z) <= v.y * v.y && v.y >= 0));
    /**
     * A pyramid with center (0,-1,0) and include (1,1,1).
     */
    public static final NFURegistry.Accessor<IInequalityPattern3D> PYRAMID_SLIM = REGISTRY.register(
        new ResourceLocation(NFULibrary.MOD_ID, "pyramid_slim"),
        () -> v -> PYRAMID.get().inequality().scale(new Vec3(1.0, 2.0, 1.0))
            .translate(new Vec3(0, -1.0, 0)).test(v));
}
