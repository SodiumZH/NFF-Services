package net.sodiumzh.nautils.statics;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class NaUtilsParticleStatics {
	
	public static void sendParticlesToEntity(Entity entity, ParticleOptions options, Vec3 positionOffset, Vec3 rndScale,
			int amount, double speed) {
		if (entity.level.isClientSide)
			return;
		Vec3 pos = entity.position();
		((ServerLevel) (entity.level)).sendParticles(options, pos.x + positionOffset.x, pos.y + positionOffset.y,
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
