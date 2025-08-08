package net.sodiumzh.nfu.math;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Represents a part of the 3D space of which the shape is defined with a 3-unknown inequality.
 */
public class Inequality3D implements Predicate<Vec3> {
    
    public final IInequalityPattern3D pattern;
    public final Vec3 scale;
    public final Vec3 translation;
    public final AABB defDomain;

    private static final Vec3 ONE_VECTOR = new Vec3(1, 1, 1);
    private static final AABB DEF_DOMAIN_R3
        = new AABB(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

    public Inequality3D(@Nonnull IInequalityPattern3D pattern, Vec3 scale, Vec3 translation, AABB defDomain) {
        this.pattern = pattern;
        this.scale = scale;
        this.translation = translation;
        this.defDomain = defDomain;
        if (Math.abs(scale.x * scale.y * scale.z) < 1e-12)
            throw new IllegalArgumentException("Inequality3D scale xyz cannot be zero.");
    }

    public Inequality3D(@Nonnull IInequalityPattern3D pattern) {
        this(pattern, ONE_VECTOR, Vec3.ZERO, DEF_DOMAIN_R3);
    }

    public Inequality3D setPattern(@Nonnull IInequalityPattern3D pattern) {
        return new Inequality3D(pattern, this.scale, this.translation, defDomain);
    }

    public Inequality3D scale(Vec3 scale) {
        return new Inequality3D(this.pattern, this.scale.multiply(scale), this.translation, defDomain);
    }

    public Inequality3D setScale(Vec3 scale) {
        return new Inequality3D(this.pattern, scale, this.translation, defDomain);
    }

    public Inequality3D flipXZ() {
        return this.scale(new Vec3(1, -1, 1));
    }

    public Inequality3D flipXY() {
        return this.scale(new Vec3(1, 1, -1));
    }

    public Inequality3D flipYZ() {
        return this.scale(new Vec3(-1, 1, 1));
    }

    public Inequality3D rotateX() {
        return this.scale(new Vec3(1, -1, -1));
    }

    public Inequality3D rotateY() {
        return this.scale(new Vec3(-1, 1, -1));
    }

    public Inequality3D rotateZ() {
        return this.scale(new Vec3(-1, -1, 1));
    }

    public Inequality3D invert() {
        return this.scale(new Vec3(-1, -1, -1));
    }

    /**
     * Translate this shape to a given position.
     */
    public Inequality3D translate(Vec3 translationVector) {
        return new Inequality3D(this.pattern, this.scale, this.translation.add(translationVector), defDomain);
    }

    /**
     * Map this inequality to a coordination defined by an AABB. (0, 0, 0), (-1, -1, -1) and (1, 1, 1) are mapped to box center,
     * (minX, minY, minZ) and (maxX, maxY, maxZ) respectively.
     */
    public Inequality3D relToAbs(AABB coord) {
        return this.scale(new Vec3(coord.getXsize() / 2.0, coord.getYsize() / 2.0, coord.getZsize() / 2.0))
            .translate(coord.getCenter());
    }

    /**
     * Map an inequality pattern to a coordination defined by an AABB. (0, 0, 0), (-1, -1, -1) and (1, 1, 1) are mapped to box center,
     * (minX, minY, minZ) and (maxX, maxY, maxZ) respectively.
     */
    public static Inequality3D relToAbs(IInequalityPattern3D pattern, AABB coord) {
        return new Inequality3D(pattern).relToAbs(coord);
    }

    /**
     * Set the definition domain BEFORE applying translation and scales. Out of definition domain it will be constant 0.
     */
    public Inequality3D setBaseDefDomain(AABB defDomain) {
        return new Inequality3D(this.pattern, this.scale, this.translation, defDomain);
    }

    public Inequality3D setBaseDefDomain(double x1, double y1, double z1, double x2, double y2, double z2) {
        return setBaseDefDomain(new AABB(x1, y1, z1, x2, y2, z2));
    }

    public Inequality3D setBaseDefDomainX(double x1, double x2) {
        return this.setBaseDefDomain(x1, this.defDomain.minY, this.defDomain.minZ, x2, this.defDomain.maxY, this.defDomain.maxZ);
    }

    public Inequality3D setBaseDefDomainY(double y1, double y2) {
        return this.setBaseDefDomain(this.defDomain.minX, y1, this.defDomain.minZ, this.defDomain.maxX, y2, this.defDomain.maxZ);
    }

    public Inequality3D setBaseDefDomainZ(double z1, double z2) {
        return this.setBaseDefDomain(this.defDomain.minX, this.defDomain.minY, z1, this.defDomain.maxX, this.defDomain.minY, z2);
    }

    public Inequality3D limitInOne() {
        return this.setBaseDefDomain(-1.0, -1.0, -1.0, 1.0, 1.0, 1.0);
    }
    @Override
    public boolean test(Vec3 v) {
        Vec3 baseV = v.subtract(this.translation).multiply(1.0 / this.scale.x, 1.0 / this.scale.y, 1.0 / this.scale.z);
        if (!this.defDomain.contains(baseV)) return false;
        return this.pattern.test(baseV);
    }

    public static Inequality3D fullSpace() {
        return new Inequality3D(IInequalityPattern3D.FULL_SPACE.get());
    }

    public static Inequality3D limitedInOne() {return new Inequality3D(IInequalityPattern3D.BOX.get());}

    /**
     * Use this inequality as a new pattern.
     * <p>WARNING: Only use this when doing calculation or registering new patterns! Unregistered patterns should never
     * be present in any inequalities to be synched, otherwise it will cause an exception.
     */
    public IInequalityPattern3D asNewPattern() {
        return this::test;
    }
}
