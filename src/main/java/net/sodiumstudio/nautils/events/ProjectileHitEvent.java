package net.sodiumstudio.nautils.events;

import javax.annotation.Nullable;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sodiumstudio.nautils.mixins.mixins.NaUtilsMixinProjectile;

/**
 * Fired when any projectile hit something, either block or entity.
 * It's fired on any projectile calling {@link Projectile#onHit} and the hit result isn't {@code MISS}.
 * Usually it's not fired if {@link ProjectileImpactEvent} is cancelled.
 * <p>This event is implemented via {@link NaUtilsMixinProjectile}.
 * <p>Note: if in subclasses {@link Projectile#onHit} is overridden and something is invoked before {@code super.onHit()}, these actions won't be cancelled. 
 */
@Cancelable
public class ProjectileHitEvent extends NaUtilsEntityEvent<Projectile>
{
	
	public final HitResult hitResult;
	
	public ProjectileHitEvent(Projectile entity, HitResult hitResult)
	{
		super(entity);
		this.hitResult = hitResult;
	}
	
	/**
	 * Cast the hit result to {@link BlockHitResult} if it is one. Otherwise return null.
	 */
	@Nullable
	public BlockHitResult getBlockHitResult()
	{
		if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult res)
			return res;
		else return null;
	}
	
	/**
	 * Cast the hit result to {@link EntityHitResult} if it is one. Otherwise return null.
	 */
	@Nullable
	public EntityHitResult getEntityHitResult()
	{
		if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult res)
			return res;
		else return null;
	}
}
