package net.sodiumzh.nfu.util;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.sodiumzh.nfu.container.Tuple2;
import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class NFULevelStatics
{
	
	// Check if a block position is right under sun, i.e. can see sky, not raining, and is day
	public static boolean isUnderSun(BlockPos pos, Entity levelContext)
	{
		return levelContext.level.canSeeSky(pos) && levelContext.level.isDay() && !levelContext.level.isRaining();
	}
	
	public static boolean isEntityUnderSun(Entity test)
	{
		return isUnderSun(new BlockPos(test.position()), test);
	}
	
	public static boolean isAboveWater(BlockPos pos, Entity levelContext)
	{
		Level level = levelContext.level;
		for (int y = pos.getY(); y > -70; --y)
		{
			BlockPos currentPos = new BlockPos(pos.getX(), y, pos.getZ());
			if (level.getBlockState(currentPos).is(Blocks.WATER))
				return true;
			else if (!level.getBlockState(currentPos).isAir())
				return false;
		}
		return false;
	}
	
	public static boolean isEntityAboveWater(Entity test)
	{
		return isAboveWater(new BlockPos(test.position()), test);
	}
	
	public static boolean isAboveVoid(BlockPos pos, Entity levelContext)
	{
		Level level = levelContext.level;
		for (int y = pos.getY(); y > -70; --y)
		{
			BlockPos currentPos = new BlockPos(pos.getX(), y, pos.getZ());
			if (!level.getBlockState(currentPos).isAir())
				return false;
		}
		return true;
	}
	
	public static boolean isEntityAboveVoid(Entity test)
	{
		return isAboveVoid(new BlockPos(test.position()), test);
	}
	
	/** Get the height of a position to the standable block or liquid below.
	 * if the position is above the void, return -1.
	* @param context Entity for getting level and checking if can stand on.
	*/
	public static int getHeightToGround(BlockPos pos, Entity context)
	{
		Level level = context.level;
		BlockPos pos1 = new BlockPos(pos);
		if (level.getBlockState(pos1).entityCanStandOn(level, pos1, context)
				|| level.getBlockState(pos1).getMaterial().isLiquid())
			return 0;
		else 
		{
			int i = 0;
			while (!level.getBlockState(pos1).entityCanStandOn(level, pos1, context)
				&& !level.getBlockState(pos1).getMaterial().isLiquid())
			{
				i++;
				pos1 = pos1.below();
				if (pos1.getY() < level.getMinBuildHeight() - 1)
					return -1;
			}
			return i;
		}
	}
	
	/** Get the height of a position to the standable block or liquid below. (Vec3 version)
	 * if the position is above the void, return -1.
	* @param context Entity for getting level and checking if can stand on.
	*/
	public static int getHeightToGround(Vec3 v, Entity context)
	{
		return getHeightToGround(new BlockPos(v), context);
	}
	
	/**
	 * Get all {@link BlockPos} in a given area fulfilling given conditions.
	 * <p> WARNING: It would be slow if the area is too large! It will invoke {@link ArrayList#add} for times of the area volume at most.
	 * @param area Searching area.
	 * @param filter Condition.
	 * @return ArrayList of BlockPos. If {@code filter} is null, return all positions. 
	 */
	public static ArrayList<BlockPos> getBlockPosInArea(AABB area, Predicate<BlockPos> filter)
	{
		ArrayList<BlockPos> res = new ArrayList<>();
		BlockPos.betweenClosedStream(area).forEach((BlockPos b) -> 
		{
			if (filter == null || filter.test(b))
				res.add(new BlockPos(b.getX(), b.getY(), b.getZ()));
		});
		return res;
	}
	
	/**
	 * Make an explosion from entity.
	 * This method is for API consistency to 1.20+ because {@link Explosion.BlockInteraction} is deprecated in 1.20.
	 * @param source Explosion source entity.
	 * @param position Explosion position in level. Usually {@code source.position()}.
	 * @param power Explosion power.
	 * @param causesFire If true, the explosion will cause fire.
	 * @param breaksBlocks If true, the explosion will break blocks on hit.
	 * @param alwaysDropsItemOnBreaking If true, it will always drop block items on breaking blocks just like TNT.
	 * @param considersMobGriefingGameRule If true, it will consider MobGriefing game rule to determine whether to break blocks. If the source isn't a {@link Mob} it's ignored.
	 * @return {@link Explosion} instance, or {@code null} on client.
	 */
	public static Explosion explode(Entity source, Vec3 position, float power, boolean causesFire, boolean breaksBlocks, boolean alwaysDropsItemsOnBreaking, boolean considersMobGriefingGameRule)
	{
		if (source.level.isClientSide)
			return null;
		boolean canBreak = breaksBlocks;
		if (canBreak && source instanceof Mob && considersMobGriefingGameRule)
			canBreak = ForgeEventFactory.getMobGriefingEvent(source.level, source);
		return source.level.explode(source, position.x, position.y, position.z, power, causesFire, 
				canBreak ? (alwaysDropsItemsOnBreaking ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.DESTROY) : Explosion.BlockInteraction.NONE);
	}
	
	/**
	 * Make an explosion from entity.
	 * This method is for API consistency to 1.20+ because {@link Explosion.BlockInteraction} is deprecated in 1.20.
	 * @param source Explosion source entity.
	 * @param position Explosion position in level. Usually {@code source.position()}.
	 * @param power Explosion power.
	 * @param causesFire If true, the explosion will cause fire.
	 * @param breaksBlocks If true, the explosion will break blocks on hit. If the source is a {@link Mob} and MobGriefing game rule is false, it will be ignored.
	 * @param alwaysDropsItemOnBreaking If true, it will always drop block items on breaking blocks just like TNT.
	 * @return {@link Explosion} instance, or {@code null} on client.
	 */
	@Nullable
	public static Explosion explode(@Nonnull Entity source, Vec3 position, float power, boolean causesFire, boolean breaksBlocks, boolean alwaysDropsItemsOnBreaking)
	{
		return NFULevelStatics.explode(source, position, power, causesFire, breaksBlocks, alwaysDropsItemsOnBreaking, true);
	}
	
	/**
	 * Make an explosion from entity.
	 * This method is for API consistency to 1.20+ because {@link Explosion.BlockInteraction} is deprecated in 1.20.
	 * @param source Explosion source entity.
	 * @param position Explosion position in level. Usually {@code source.position()}.
	 * @param power Explosion power.
	 * @param causesFire If true, the explosion will cause fire.
	 * @param breaksBlocks If true, the explosion will break blocks on hit. It will NOT always drop items on breaking blocks. If the source is a {@link Mob} and MobGriefing game rule is false, it will be ignored.
	 * @return {@link Explosion} instance, or {@code null} on client.
	 */
	@Nullable
	public static Explosion explode(@Nonnull Entity source, Vec3 position, float power, boolean causesFire, boolean breaksBlocks)
	{
		return NFULevelStatics.explode(source, position, power, causesFire, breaksBlocks, false, true);
	}
	
	/**
	 * Make an explosion from entity at its position.
	 * This method is for API consistency to 1.20+ because {@link Explosion.BlockInteraction} is deprecated in 1.20.
	 * @param source Explosion source entity.
	 * @param power Explosion power.
	 * @param causesFire If true, the explosion will cause fire.
	 * @param breaksBlocks If true, the explosion will break blocks on hit. If the source is a {@link Mob} and MobGriefing game rule is false, it will be ignored.
	 * @param alwaysDropsItemOnBreaking If true, it will always drop block items on breaking blocks just like TNT.
	 * @return {@link Explosion} instance, or {@code null} on client.
	 */
	@Nullable
	public static Explosion explode(@Nonnull Entity source, float power, boolean causesFire, boolean breaksBlocks, boolean alwaysDropsItemsOnBreaking)
	{
		return NFULevelStatics.explode(source, source.position(), power, causesFire, breaksBlocks, alwaysDropsItemsOnBreaking, true);
	}
	
	/**
	 * Make an explosion from entity at its position.
	 * This method is for API consistency to 1.20+ because {@link Explosion.BlockInteraction} is deprecated in 1.20.
	 * @param source Explosion source entity.
	 * @param power Explosion power.
	 * @param causesFire If true, the explosion will cause fire.
	 * @param breaksBlocks If true, the explosion will break blocks on hit. It will NOT always drop items on breaking blocks. If the source is a {@link Mob} and MobGriefing game rule is false, it will be ignored.
	 * @return {@link Explosion} instance, or {@code null} on client.
	 */
	@Nullable
	public static Explosion explode(@Nonnull Entity source, float power, boolean causesFire, boolean breaksBlocks)
	{
		return NFULevelStatics.explode(source, source.position(), power, causesFire, breaksBlocks, false, true);
	}
	
	/**
	 * Make an explosion without entity source.
	 * This method is for API consistency to 1.20+ because {@link Explosion.BlockInteraction} is deprecated in 1.20.
	 * @param level Explosion level. On client it will not do anything.
	 * @param position Explosion position in level.
	 * @param power Explosion power.
	 * @param causesFire If true, the explosion will cause fire.
	 * @param breaksBlocks If true, the explosion will break blocks on hit.
	 * @param alwaysDropsItemOnBreaking If true, it will always drop block items on breaking blocks just like TNT.
	 * @return {@link Explosion} instance, or {@code null} on client.
	 */
	@Nullable
	public static Explosion explodeNoSource(Level level, Vec3 position, float power, boolean causesFire, boolean breaksBlocks, boolean alwaysDropsItemsOnBreaking)
	{
		if (level.isClientSide)
			return null;
		return level.explode(null, position.x, position.y, position.z, power, causesFire, 
				breaksBlocks ? (alwaysDropsItemsOnBreaking ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.DESTROY) : Explosion.BlockInteraction.NONE);
	}
	
	/**
	 * Make an explosion without entity source.
	 * This method is for API consistency to 1.20+ because {@link Explosion.BlockInteraction} is deprecated in 1.20.
	 * @param level Explosion level. On client it will not do anything.
	 * @param position Explosion position in level.
	 * @param power Explosion power.
	 * @param causesFire If true, the explosion will cause fire.
	 * @param breaksBlocks If true, the explosion will break blocks on hit. It will NOT always drop items on breaking blocks. 
	 * @param alwaysDropsItemOnBreaking If true, it will always drop block items on breaking blocks just like TNT.
	 * @return {@link Explosion} instance, or {@code null} on client.
	 */
	@Nullable
	public static Explosion explodeNoSource(Level level, Vec3 position, float power, boolean causesFire, boolean breaksBlock)
	{
		return NFULevelStatics.explodeNoSource(level, position, power, causesFire, breaksBlock, false);
	}
	
	public static <T> T selectByDifficulty(Level level, T peaceful, T easy, T normal, T hard)
	{
		switch (level.getDifficulty())
		{
		case PEACEFUL:
		{
			return peaceful;
		}
		case EASY:
		{
			return easy;
		}
		case NORMAL:
		{
			return normal;
		}
		case HARD:
		{
			return hard;
		}
		default:
		{
			throw new IllegalStateException("Illegal difficulty.");
		}
		}
	}
	
	public static <T> T selectByDifficulty(Entity levelContext, T peaceful, T easy, T normal, T hard)
	{
		return selectByDifficulty(levelContext.level, peaceful, easy, normal, hard);
	}

	/**
	 * Find the closest object from a starting point in a given direction, either block or entity.
	 * @param entityContext Entity to determine the level. Also, itself will be excluded.
	 * @param direction Direction vector, not necessarily normal, but must be non-zero. (Not end point!)
	 * @param maxDistance Max tracing distance. Objects further than this distance will be ignored. Note: don't set this value too large, or it may face performance issues.
	 * @param includeFluid If true, it will trace fluid, otherwise fluid will be ignored.
	 * @return The result if something is found, either block or entity. Empty if not. It will not return miss HitResult.
	 */
	public static Optional<HitResult> lineTrace(@Nonnull Entity entityContext, Vec3 startPoint, Vec3 direction,
			double maxDistance, boolean includeFluid) {
		Vec3 endPoint = startPoint.add(direction.normalize().scale(maxDistance));
		// Search block
		HitResult blockResult = entityContext.level.clip(new ClipContext(startPoint, endPoint, ClipContext.Block.OUTLINE,
				includeFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entityContext));
		// Search entity
		AABB entitySearchBound = entityContext.getBoundingBox()
				.expandTowards(direction.normalize().scale(maxDistance)).inflate(1.0D, 1.0D, 1.0D);
		EntityHitResult entityResult = ProjectileUtil.getEntityHitResult(entityContext, startPoint, endPoint, entitySearchBound,
				e -> !e.isSpectator() && e.isPickable(), maxDistance * maxDistance);
		// Compare results and return
		Function<HitResult, Double> distanceSqr = r -> (r != null && !r.getType().equals(HitResult.Type.MISS)) ?
				r.getLocation().distanceToSqr(startPoint) : Double.MAX_VALUE;
		if (distanceSqr.apply(blockResult) > maxDistance * maxDistance) blockResult = null;
		if (distanceSqr.apply(entityResult) > maxDistance * maxDistance) entityResult = null;
		return Optional.ofNullable(distanceSqr.apply(blockResult) > distanceSqr.apply(entityResult) ? entityResult : blockResult);
	}

	/**
	 * Find the closest object from a starting point in a given direction, either block or entity (excluding fluid).
	 * @param entityContext Entity to determine the level. Also, itself will be excluded.
	 * @param direction Direction vector, not necessarily normal, but must be non-zero. (Not end point!)
	 * @param maxDistance Max tracing distance. Objects further than this distance will be ignored. Note: don't set this value too large, or it may face performance issues.
	 * @return The result if something is found, either block or entity. Empty if not.
	 */
	public static Optional<HitResult> lineTrace(@Nonnull Entity entityContext, Vec3 startPoint, Vec3 direction, double maxDistance) {
		return lineTrace(entityContext, startPoint, direction, maxDistance, false);
	}

	/**
	 * Find the closest object in an entity's direction of view, either block or entity.
	 * @param entity Source entity.
	 * @param maxDistance Max tracing distance. Objects further than this distance will be ignored. Note: don't set this value too large, or it may face performance issues.
	 * @return The result if something is found, either block or entity. Empty if not.
	 */
	public static Optional<HitResult> eyeTrace(@Nonnull Entity entity, double maxDistance, boolean includeFluid) {
		return lineTrace(entity, entity.getEyePosition(), entity.getViewVector(1f), maxDistance, includeFluid);
	}

	/**
	 * Find the closest object in an entity's direction of view, either block or entity.
	 * @param entity Source entity.
	 * @param maxDistance Max tracing distance. Objects further than this distance will be ignored. Note: don't set this value too large, or it may face performance issues.
	 * @return The result if something is found, either block or entity. Empty if not.
	 */
	public static Optional<HitResult> eyeTrace(@Nonnull Entity entity, double maxDistance) {
		return lineTrace(entity, entity.getEyePosition(), entity.getViewVector(1f), maxDistance, false);
	}

	public static boolean hasBlockCollision(BlockPos pos, @Nonnull Entity context) {
		return !context.level.getBlockState(pos).getShape(context.level, pos, CollisionContext.of(context)).isEmpty();
	}

	/**
	 * Check if a pos is solid-collision (i.e. non-empty collision) to an entity.
	 */
	public static boolean isSolidCollision(BlockPos pos, @Nonnull Entity context) {
		return !context.level.getBlockState(pos).getBlock()
			.getCollisionShape(context.level.getBlockState(pos),
				context.level, pos, CollisionContext.of(context)).isEmpty();
	}
	/**
	 * Check if a pos is water-collision (i.e. water, no collision) to an entity.
	 */
	public static boolean isWaterCollision(BlockPos pos, @Nonnull Entity context) {
		BlockState state = context.level.getBlockState(pos);
		if (state.is(Blocks.WATER)) return true;
		else if (!state.hasProperty(BlockStateProperties.WATERLOGGED) ||
			!state.getValue(BlockStateProperties.WATERLOGGED)) return false;
		else return !hasBlockCollision(pos, context);
	}

	/**
	 * Check if a pos is liquid-collision (i.e. any liquid, no collision) to an entity.
	 */
	public static boolean isLiquidCollision(BlockPos pos, @Nonnull Entity context) {
		return isWaterCollision(pos, context) || (context.level.getBlockState(pos).getMaterial().isLiquid() && !hasBlockCollision(pos, context));
	}

	/**
	 * Check if a pos is air-collision (i.e. non-liquid, no collision) to an entity.
	 */
	public static boolean isAirCollision(BlockPos pos, @Nonnull Entity context) {
		return !context.level.getBlockState(pos).getMaterial().isLiquid() && !isSolidCollision(pos, context);
	}

	/**
	 * Find the water depth for an entity's position.
	 */
	public static int getWaterDepth(BlockPos pos, @Nonnull Entity context) {
		BlockPos currentPos = pos;
		// If air or liquid, go down and find a solid block, or go through a water layer
		if (!isSolidCollision(pos, context)) {
			boolean wentThroughWater = false;
			while(true) {
				if (isSolidCollision(currentPos, context) || (wentThroughWater && !isWaterCollision(currentPos, context)))
					break;
				else if (isWaterCollision(currentPos, context))
					wentThroughWater = true;
				currentPos = currentPos.below();
			}
		} else {
			// If solid, go up to find a non-solid block
			do {
				currentPos = currentPos.above();
			} while (isSolidCollision(currentPos, context));
			// Now it's the bottom non-solid, then go to the top solid
			currentPos = currentPos.below();
		}
		// Now the pos is "the bottom of the water", i.e. the block right below the bottom water block;
		int i = 0;	// Depth counter
		currentPos = currentPos.above();
		while (isWaterCollision(currentPos, context)) {
			i++;
			currentPos = currentPos.above();
		}
		return i;
	}

	public static int getWaterDepth( @Nonnull Entity context) {
		return getWaterDepth(context.blockPosition(), context);
	}

	/**
	 * Get a mapping as a tuple stream of the pos and Block States in a given area. Note that if the area is large,
	 * the filter should better remove most positions, otherwise it may impact the performance.
	 * <p>Note: the Block Pos in output stream's each tuple is a copy of the stream pos value. They are not {@link BlockPos.MutableBlockPos}.
	 */
	public static Stream<Tuple2<BlockPos, BlockState>> getBlockPosAndStates(Level level, AABB area, BiPredicate<BlockPos, BlockState> filter) {
		Stream<BlockPos> stream = BlockPos.betweenClosedStream(area);
		if (filter != null)
			stream = stream.filter(pos -> filter.test(pos, level.getBlockState(pos)));
		return stream.map(pos -> new Tuple2<>(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), level.getBlockState(pos)));
	}

	/**
	 * Get a mapping as a tuple stream of the pos and Block States in a given area. Note that if the area is large,
	 * the filter should better remove most positions, otherwise it may impact the performance.
	 * <p>Note: the Block Pos in output stream's each tuple is a copy of the stream pos value. They are not {@link BlockPos.MutableBlockPos}.
	 */
	public static Stream<Tuple2<BlockPos, BlockState>> getBlockPosAndStates(Level level, AABB area, Predicate<BlockState> filter) {
		Stream<BlockPos> stream = BlockPos.betweenClosedStream(area);
		if (filter != null)
			stream = stream.filter(pos -> filter.test(level.getBlockState(pos)));
		return stream.map(pos -> new Tuple2<>(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), level.getBlockState(pos)));
	}

	/**
	 * Get a mapping as a tuple stream of the pos and Block States in a spherical area of a given center and radius. Note that if the area is large,
	 * the filter should better remove most positions, otherwise it may impact the performance.
	 * <p>Note: the Block Pos in output stream's each tuple is a copy of the stream pos value. They are not {@link BlockPos.MutableBlockPos}.
	 */
	public static Stream<Tuple2<BlockPos, BlockState>> getSphericalBlockStates(Level level, BlockPos center, int radius, BiPredicate<BlockPos, BlockState> filter) {
		Stream<BlockPos> stream = BlockPos.betweenClosedStream(center.offset(radius, radius, radius), center.offset(-radius, -radius, -radius))
			.filter(pos -> pos.distSqr(center) <= radius * radius);
		if (filter != null)
			stream = stream.filter(pos -> filter.test(pos, level.getBlockState(pos)));
		return stream.map(pos -> new Tuple2<>(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), level.getBlockState(pos)));
	}

	/**
	 * Get a mapping as a tuple stream of the pos and Block States in a spherical area of a given center and radius. Note that if the area is large,
	 * the filter should better remove most positions, otherwise it may impact the performance.
	 * <p>Note: the Block Pos in output stream's each tuple is a copy of the stream pos value. They are not {@link BlockPos.MutableBlockPos}.
	 */
	public static Stream<Tuple2<BlockPos, BlockState>> getSphericalBlockStates(Level level, BlockPos center, int radius, Predicate<BlockState> filter) {
		Stream<BlockPos> stream = BlockPos.betweenClosedStream(center.offset(radius, radius, radius), center.offset(-radius, -radius, -radius))
			.filter(pos -> pos.distSqr(center) <= radius * radius);
		if (filter != null)
			stream = stream.filter(pos -> filter.test(level.getBlockState(pos)));
		return stream.map(pos -> new Tuple2<>(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), level.getBlockState(pos)));
	}
}
