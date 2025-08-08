package net.sodiumzh.nfu.math;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

/**
 * A {@link RandomSource} that can be used safely from different threads.
 * It maintains a {@link LegacyRandomSource} (thread-unsafe) for each thread, and
 * each {@link LegacyRandomSource} is for only a specific thread.
 */
public class ThreadSafeRandomSource implements RandomSource {

    private final ThreadLocal<RandomSource> src = ThreadLocal.withInitial(RandomSource::create);

    public ThreadSafeRandomSource(){}

    @Override
    public RandomSource fork() {
        return src.get().fork();
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return src.get().forkPositional();
    }

    @Override
    public void setSeed(long pSeed) {
        src.get().setSeed(pSeed);
    }

    @Override
    public int nextInt() {
        return src.get().nextInt();
    }

    @Override
    public int nextInt(int pBound) {
        return src.get().nextInt(pBound);
    }

    @Override
    public long nextLong() {
        return src.get().nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return src.get().nextBoolean();
    }

    @Override
    public float nextFloat() {
        return src.get().nextFloat();
    }

    @Override
    public double nextDouble() {
        return src.get().nextDouble();
    }

    @Override
    public double nextGaussian() {
        return src.get().nextGaussian();
    }
}
