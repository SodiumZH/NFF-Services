package net.sodiumzh.nfu.math;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

/**
 * Representing a function from a 3D position to a 3D vector.
 */
public class Field3D implements UnaryOperator<Vec3> {

    public final IFieldPattern3D pattern;
    public final Vec3 spaceScale;
    public final Vec3 valueScale;
    public final Vec3 translation;
    public final Vec3 valueAddition;
    @Nullable
    public final Inequality3D baseDefinitionDomain;

    private static final Vec3 ONE_VECTOR = new Vec3(1, 1, 1);
    public Field3D(IFieldPattern3D pattern, Vec3 spaceScale, Vec3 valueScale, Vec3 translation, Vec3 valueAddition, @Nullable Inequality3D baseDefinitionDomain) {
        this.pattern = pattern;
        this.spaceScale = spaceScale;
        this.valueScale = valueScale;
        this.translation = translation;
        this.valueAddition = valueAddition;
        this.baseDefinitionDomain = baseDefinitionDomain;
        if (Math.abs(spaceScale.x * spaceScale.y * spaceScale.z) < 1e-12)
            throw new IllegalArgumentException("Field3D space scale xyz cannot be zero.");
    }

   /* public Field3D(IFieldPattern3D pattern, Vec3 spaceScale, Vec3 valueScale, Vec3 translation) {
        this(pattern, spaceScale, valueScale, translation, DEF_DOMAIN_R3);
    }*/

    public Field3D(IFieldPattern3D pattern) {
        this(pattern, ONE_VECTOR, ONE_VECTOR, Vec3.ZERO, Vec3.ZERO, null);
    }

    public Field3D setPattern(IFieldPattern3D pattern) {
        return new Field3D(pattern, this.spaceScale, this.valueScale, this.translation, this.valueAddition, this.baseDefinitionDomain);
    }

    public Field3D scaleSpace(Vec3 scale) {
        return new Field3D(this.pattern, this.spaceScale.multiply(scale), this.valueScale, this.translation, this.valueAddition, this.baseDefinitionDomain);
    }

    public Field3D setOverallSpaceScale(Vec3 scale) {
        return new Field3D(this.pattern, scale, this.valueScale, this.translation, this.valueAddition, this.baseDefinitionDomain);
    }

    public Field3D scaleValue(Vec3 scale) {
        return new Field3D(this.pattern, this.spaceScale, this.valueScale.multiply(scale), translation, this.valueAddition, this.baseDefinitionDomain);
    }

    public Field3D setOverallValueScale(Vec3 scale) {
        return new Field3D(this.pattern, this.spaceScale, scale, this.translation, this.valueAddition, this.baseDefinitionDomain);
    }

    /**
     * Add a constant to the value. This constant is NOT impacted by symmetric operations of the field.
     */
    public Field3D putValueAddition(Vec3 value) {
        return new Field3D(this.pattern, this.spaceScale, this.valueScale, this.translation, this.valueAddition.add(value), this.baseDefinitionDomain);
    }

    public Field3D flipXZ() {
        return this.scaleSpace(new Vec3(1, -1, 1)).scaleValue(new Vec3(1, -1, 1));
    }

    public Field3D flipXY() {
        return this.scaleSpace(new Vec3(1, 1, -1)).scaleValue(new Vec3(1, 1, -1));
    }

    public Field3D flipYZ() {
        return this.scaleSpace(new Vec3(-1, 1, 1)).scaleValue(new Vec3(-1, 1, 1));
    }

    public Field3D rotateX() {
        return this.scaleSpace(new Vec3(1, -1, -1)).scaleValue(new Vec3(1, -1, -1));
    }

    public Field3D rotateY() {
        return this.scaleSpace(new Vec3(-1, 1, -1)).scaleValue(new Vec3(-1, 1, -1));
    }

    public Field3D rotateZ() {
        return this.scaleSpace(new Vec3(-1, -1, 1)).scaleValue(new Vec3(-1, -1, 1));
    }

    public Field3D invert() {
        return this.scaleSpace(new Vec3(-1, -1, -1)).scaleValue(new Vec3(-1, -1, -1));
    }

    public Field3D translate(Vec3 translationVector) {
        return new Field3D(this.pattern, this.spaceScale, this.valueScale, this.translation.add(translationVector), this.valueAddition, this.baseDefinitionDomain);
    }

    /**
     * Map this field to a coordination defined by an AABB. (0, 0, 0), (-1, -1, -1) and (1, 1, 1) are mapped to box center,
     * (minX, minY, minZ) and (maxX, maxY, maxZ) respectively.
     */
    public Field3D relToAbs(AABB coord) {
        return this.scaleSpace(new Vec3(coord.getXsize() / 2.0, coord.getYsize() / 2.0, coord.getZsize() / 2.0))
            .translate(coord.getCenter());
    }

    /**
     * Map a field pattern to a coordination defined by an AABB. (0, 0, 0), (-1, -1, -1) and (1, 1, 1) are mapped to box center,
     * (minX, minY, minZ) and (maxX, maxY, maxZ) respectively.
     */
    public static Field3D relToAbs(IFieldPattern3D pattern, AABB coord) {
        return new Field3D(pattern).relToAbs(coord);
    }

    /**
     * Set the definition domain BEFORE applying translation and scales. Out of definition domain it will be constant 0.
     */
    public Field3D setBaseDefDomain(Inequality3D domain) {
        return new Field3D(this.pattern, this.spaceScale, this.valueScale, this.translation, this.valueAddition, domain);
    }

    public Field3D noDefDomain() {
        return this.setBaseDefDomain(Inequality3D.fullSpace());
    }

    public Field3D limitInOne() {
        return this.setBaseDefDomain(new Inequality3D(IInequalityPattern3D.BOX.get()));
    }

    @Override
    public Vec3 apply(Vec3 pos) {
        Vec3 basePos = pos.subtract(this.translation)
            .multiply(1.0 / this.spaceScale.x, 1.0 / this.spaceScale.y, 1.0 / this.spaceScale.z);
        if (this.baseDefinitionDomain != null && !this.baseDefinitionDomain.test(basePos)) return Vec3.ZERO;
        return this.pattern.apply(basePos).multiply(this.valueScale).add(this.valueAddition);
    }

    public static Field3D zero() {
        return IFieldPattern3D.ZERO.get().field();
    }

    /**
     * Use this field as a new pattern.
     * <p>WARNING: Only use this when doing calculation or registering new patterns! Unregistered patterns should never
     * be present in any fields to be synched, otherwise it will cause an exception.
     */
    public IFieldPattern3D asNewPattern() {
        return this::apply;
    }
}
