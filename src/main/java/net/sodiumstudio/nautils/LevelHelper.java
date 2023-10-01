package net.sodiumstudio.nautils;

import java.util.ArrayList;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sodiumstudio.nautils.math.MathUtil;

public class LevelHelper
{
	
	// Check if a block position is right under sun, i.e. can see sky, not raining, and is day
	public static boolean isUnderSun(BlockPos pos, Entity levelContext)
	{
		return levelContext.level().canSeeSky(pos) && levelContext.level().isDay() && !levelContext.level().isRaining();
	}
	
	public static boolean isEntityUnderSun(Entity test)
	{
		return isUnderSun(MathUtil.getBlockPos(test.position()), test);
	}
	
	public static boolean isAboveWater(BlockPos pos, Entity levelContext)
	{
		Level level = levelContext.level();
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
		return isAboveWater(MathUtil.getBlockPos(test.position()), test);
	}
	
	public static boolean isAboveVoid(BlockPos pos, Entity levelContext)
	{
		Level level = levelContext.level();
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
		return isAboveVoid(MathUtil.getBlockPos(test.position()), test);
	}
	
	/** Get the height of a position to the standable block or liquid below.
	 * if the position is above the void, return -1.
	* @param context Entity for getting level and checking if can stand on.
	*/
	public static int getHeightToGround(BlockPos pos, Entity context)
	{
		Level level = context.level();
		BlockPos pos1 = new BlockPos(pos);
		if (level.getBlockState(pos1).entityCanStandOn(level, pos1, context)
				|| level.getBlockState(pos1).liquid())
			return 0;
		else 
		{
			int i = 0;
			while (!level.getBlockState(pos1).entityCanStandOn(level, pos1, context)
				&& !level.getBlockState(pos1).liquid())
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
		return getHeightToGround(MathUtil.getBlockPos(v), context);
	}
	
	public ArrayList<BlockPos> getBlockPosInArea(AABB area, Predicate<BlockPos> filter)
	{
		Wrapped<ArrayList<BlockPos>> wrapped = new Wrapped<>(new ArrayList<>());
		BlockPos.betweenClosedStream(area).forEach((BlockPos b) -> 
		{
			if (filter == null || filter.test(b))
				wrapped.get().add(new BlockPos(b.getX(), b.getY(), b.getZ()));
		});
		return wrapped.get();
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
		return LevelHelper.explode(source, position, power, causesFire, breaksBlocks, alwaysDropsItemsOnBreaking, true);
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
		return LevelHelper.explode(source, position, power, causesFire, breaksBlocks, false, true);
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
		return LevelHelper.explode(source, source.position(), power, causesFire, breaksBlocks, alwaysDropsItemsOnBreaking, true);
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
		return LevelHelper.explode(source, source.position(), power, causesFire, breaksBlocks, false, true);
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
		return LevelHelper.explodeNoSource(level, position, power, causesFire, breaksBlock, false);
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
}
