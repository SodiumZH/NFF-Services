package net.sodiumzh.nfu.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.util.NFUEntityStatics;

import javax.annotation.Nullable;

/**
 * Represents a motion (movement, acceleration) on server. Applying the motion will be synched to client.
 * <p>Movement in tick^-1. Acceleration in tick^-2.
 * <p>Note: this motion should be handled only on server. Calling {@code apply} on client will not do anything.
 */
public class ServerEntityMotion {
    private Vec3 movement = Vec3.ZERO;
    private Vec3 accel = Vec3.ZERO;

    private ServerEntityMotion(){}

    public static ServerEntityMotion zero() {
        return new ServerEntityMotion();
    }

    public static ServerEntityMotion movement(Vec3 deltaMovement) {
        ServerEntityMotion m = new ServerEntityMotion();
        m.movement = deltaMovement;
        return m;
    }

    public static ServerEntityMotion movement(double deltaMovementX, double deltaMovementY, double deltaMovementZ) {
        ServerEntityMotion m = new ServerEntityMotion();
        m.movement = new Vec3(deltaMovementX, deltaMovementY, deltaMovementZ);
        return m;
    }

    public static ServerEntityMotion accel(Vec3 accel) {
        ServerEntityMotion m = new ServerEntityMotion();
        m.accel = accel;
        return m;
    }

    public static ServerEntityMotion accel(double accelX, double accelY, double accelZ) {
        ServerEntityMotion m = new ServerEntityMotion();
        m.accel = new Vec3(accelX, accelY, accelZ);
        return m;
    }

    public ServerEntityMotion setMovement(Vec3 movement) {
        this.movement = movement;
        return this;
    }

    public ServerEntityMotion setMovement(double x, double y, double z) {
        this.movement = new Vec3(x, y, z);
        return this;
    }

    public ServerEntityMotion addMovement(Vec3 delta) {
        this.movement = this.movement.add(delta);
        return this;
    }

    public ServerEntityMotion addMovement(double dx, double dy, double dz) {
        this.movement = this.movement.add(dx, dy, dz);
        return this;
    }

    public ServerEntityMotion setExactPos(Entity ctx, Vec3 pos) {
        this.movement = pos.subtract(ctx.position());
        return this;
    }

    public ServerEntityMotion setExactPos(Entity ctx, double x, double y, double z) {
        return setExactPos(ctx, new Vec3(x, y, z));
    }

    public ServerEntityMotion setAccel(Vec3 accel) {
        this.accel = accel;
        return this;
    }

    public ServerEntityMotion setAccel(double x, double y, double z) {
        this.accel = new Vec3(x, y, z);
        return this;
    }

    public ServerEntityMotion addAccel(Vec3 delta) {
        this.accel = this.accel.add(delta);
        return this;
    }

    public ServerEntityMotion addAccel(double dx, double dy, double dz) {
        this.accel = this.accel.add(dx, dy, dz);
        return this;
    }

    public ServerEntityMotion setExactVelocity(Entity ctx, Vec3 velocity) {
        this.accel = velocity.subtract(ctx.getDeltaMovement().subtract(velocity));
        return this;
    }

    public ServerEntityMotion setExactVelocity(Entity ctx, double vx, double vy, double vz) {
        return setExactVelocity(ctx, new Vec3(vx, vy, vz));
    }

    public Vec3 getMovement() {
        return movement;
    }

    public Vec3 getAcceleration() {
        return accel;
    }

    public void apply(Entity e) {
        NFUEntityStatics.addMotionOnServer(e, this.movement, this.accel);
    }

}
