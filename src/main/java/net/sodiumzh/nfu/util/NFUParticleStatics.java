package net.sodiumzh.nfu.util;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.math.ThreadSafeRandomSource;
import org.jetbrains.annotations.Nullable;

public class NFUParticleStatics {

	public static final RandomSource RND = new ThreadSafeRandomSource();

	/**
	 * Only on client, add a particle with randomized position and velocity
	 */
	public static void addRandomizedParticle(Level level, ParticleOptions type, Vec3 position, Vec3 posRandomScale, Vec3 maxVelocity, Vec3 velocityRandomScale) {
		if (level.isClientSide) {
			level.addParticle(type, position.x + RND.nextGaussian() * posRandomScale.x, position.y + RND.nextGaussian() * posRandomScale.y,
				position.z + RND.nextGaussian() * posRandomScale.z, maxVelocity.x * RND.nextGaussian() * velocityRandomScale.x,
				maxVelocity.y * RND.nextGaussian() * velocityRandomScale.y, maxVelocity.z * RND.nextGaussian() * velocityRandomScale.z);
		}
	}

	/**
	 * Side-safe adding particles.
	 */
	public static void sendParticlesToEntity(Entity entity, ParticleOptions options, Vec3 positionOffset, Vec3 rndScale,
			int amount, double speed) {
		if (amount <= 0) return;
		Vec3 pos = entity.position();
		if (entity.level().isClientSide) {
			for (int i = 0; i < amount; ++i) {
				addRandomizedParticle(entity.level(), options, pos.add(positionOffset), rndScale, new Vec3(speed, speed, speed), new Vec3(1d, 1d, 1d));
			}
		}
		else if (!entity.level().isClientSide)
			((ServerLevel)(entity.level())).sendParticles(options, pos.x + positionOffset.x, pos.y + positionOffset.y,
				pos.z + positionOffset.z, amount, rndScale.x, rndScale.y, rndScale.z, speed);
	}
	
	public static void sendParticlesToEntity(Entity entity, ParticleOptions options, double posOffsetX,
			double posOffsetY, double posOffsetZ, double rndScaleX, double rndScaleY, double rndScaleZ, int amount,
			double speed) {
		sendParticlesToEntity(entity, options, new Vec3(posOffsetX, posOffsetY, posOffsetZ),
				new Vec3(rndScaleX, rndScaleY, rndScaleZ), amount, speed);

	}

	public static void sendParticlesToEntity(Entity entity, ParticleOptions options, Vec3 posOffset, double rndScale,
			int amount, double speed) {
		sendParticlesToEntity(entity, options, posOffset, new Vec3(rndScale, rndScale, rndScale), amount, speed);
	}

	public static void sendParticlesToEntity(Entity entity, ParticleOptions options, double heightOffset,
			double rndScale, int amount, double speed) {
		sendParticlesToEntity(entity, options, new Vec3(0d, heightOffset, 0d), rndScale, amount, speed);
	}

	public static void sendHeartParticlesToEntityDefault(LivingEntity entity, float heightOffset, int amount) {
		sendParticlesToEntity(entity, ParticleTypes.HEART, entity.getBbHeight() - 0.2d + heightOffset, 0.5d, amount, 1d);
	}
	
	public static void sendHeartParticlesToEntityDefault(LivingEntity entity, float heightOffset) {
		sendHeartParticlesToEntityDefault(entity, heightOffset, 10);
	}

	public static void sendHeartParticlesToEntityDefault(LivingEntity entity) {
		sendHeartParticlesToEntityDefault(entity, 0f);
	}
	
	public static void sendGlintParticlesToEntityDefault(LivingEntity entity, float heightOffset, int amount) {
		sendParticlesToEntity(entity, ParticleTypes.HAPPY_VILLAGER, entity.getBbHeight() - 0.2 + heightOffset, 0.5d, amount, 1d);
	}

	public static void sendGlintParticlesToEntityDefault(LivingEntity entity, float heightOffset)
	{
		sendGlintParticlesToEntityDefault(entity, heightOffset, 20);
	}
	
	public static void sendGlintParticlesToEntityDefault(LivingEntity entity) {
		sendGlintParticlesToEntityDefault(entity, 0);
	}
	
	public static void sendSmokeParticlesToEntityDefault(LivingEntity entity, float heightOffset, int amount) {
		sendParticlesToEntity(entity, ParticleTypes.SMOKE, entity.getBbHeight() - 0.2 + heightOffset, 0.2d, amount, 0d);
	}
	
	public static void sendSmokeParticlesToEntityDefault(LivingEntity entity, float heightOffset)
	{
		sendSmokeParticlesToEntityDefault(entity, heightOffset, 30);
	}

	public static void sendSmokeParticlesToEntityDefault(LivingEntity entity) {
		sendSmokeParticlesToEntityDefault(entity, 0f);
	}
	
	
	public static void sendAngryParticlesToEntityDefault(LivingEntity entity, float heightOffset, int amount) {
		sendParticlesToEntity(entity, ParticleTypes.ANGRY_VILLAGER, entity.getBbHeight() - 0.2 + heightOffset, 0.3d, amount, 1d);
	}

	public static void sendAngryParticlesToEntityDefault(LivingEntity entity, float heightOffset)
	{
		sendAngryParticlesToEntityDefault(entity, heightOffset, 5);
	}
	
	public static void sendAngryParticlesToEntityDefault(LivingEntity entity)
	{
		sendAngryParticlesToEntityDefault(entity, 0);
	}

}
