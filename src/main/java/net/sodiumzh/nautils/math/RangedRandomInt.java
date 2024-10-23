package net.sodiumzh.nautils.math;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.util.RandomSource;

/**
 * A {@code RangedRandomInt} is a random integer generator with a range and
 * certain randomization methods.
 * <p>
 * Now it supports 3 types of outputting: <i>fixed</i> (always outputting the
 * same value), <i>uniform</i> (all values within the range having the same
 * probability to be picked) and <i>Poisson</i> (the probabilities following the
 * Poisson distribution with specified parameter p).
 */
public class RangedRandomInt implements Supplier<Integer>
{
    private static final RandomSource RND = RandomSource.create();
    private final int minValue;	// Included
    private final int maxValue;	// Included
    private final double p;
    private final RandomizationType rndType;
    private int lastValue;
    private boolean lastValueValid = false;

    private RangedRandomInt(int min, int max, double p, RandomizationType type)
    {
        this.minValue = min;
        this.maxValue = max;
        this.p = p;
        this.rndType = type;
        this.lastValue = min;
    }

    /**
     * Get a {@code RangedRandomInt} representing a fixed value.
     */
    public static RangedRandomInt fixed(int value)
    {
        if (value < 0) throw new IllegalArgumentException();
        var res = new RangedRandomInt(value, value, 0.5d, RandomizationType.FIXED_VALUE);
        res.lastValue = value;
        res.lastValueValid = true;
        return res;
    }

    /**
     * Get a {@code RangedRandomInt} with probabilities of Poisson distribution.
     */
    public static RangedRandomInt poisson(int min, int max, double p)
    {
        if (min > max || p < 0 || p > 1) throw new IllegalArgumentException();
        if (min == max) return fixed(min);
        return new RangedRandomInt(min, max, p, RandomizationType.POISSON);
    }

    /**
     * Get a {@code RangedRandomInt} with probabilities of Poisson distribution with p = 0.5.
     */
    public static RangedRandomInt poisson(int min, int max)
    {
        return poisson(min, max, 0.5d);
    }

    /**
     * Get a {@code RangedRandomInt} with probabilities of uniform distribution.
     */
    public static RangedRandomInt uniform(int min, int max)
    {
        if (min > max) throw new IllegalArgumentException();
        if (min == max) return fixed(min);
        return new RangedRandomInt(min, max, 0.5, RandomizationType.UNIFORM);
    }

    public RangedRandomInt setUniform()
    {
        return new RangedRandomInt(this.minValue, this.maxValue, this.p, RandomizationType.UNIFORM);
    }

    /**
     * Get a new {@code RangedRandomInt} with the same min and max, using Poisson distributive randomization with p.
     */
    public RangedRandomInt setPoisson(double p)
    {
        return new RangedRandomInt(this.minValue, this.maxValue, p, RandomizationType.POISSON);
    }

    public RangedRandomInt setRange(int min, int max)
    {
        return new RangedRandomInt(min, max, this.p, this.rndType);
    }

    /**
     * Get a random value using the given RandomSource.
     */
    public int getValue(RandomSource rnd)
    {
        switch (rndType)
        {
            case FIXED_VALUE:
            {
                this.lastValue = this.minValue;
                return this.minValue;
            }
            case UNIFORM:
            {
                if (this.minValue == this.maxValue) return this.minValue;
                int val = rnd.nextInt(minValue, maxValue + 1);
                this.lastValue = val;
                this.lastValueValid = true;
                return val;
            }
            case POISSON:
            {
                if (this.minValue == this.maxValue) return this.minValue;
                int res = minValue;
                for (int i = minValue; i < maxValue; ++i)
                {
                    if (rnd.nextDouble() <= p) res++;
                }
                this.lastValue = res;
                this.lastValueValid = true;
                return res;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Get a random value using the internal RandomSource.
     */
    public int getValue()
    {
        return this.getValue(RND);
    }

    /**
     * Get the value last output.
     * @throws IllegalStateException If it has never output via {@code getValue}.
     */
    public int lastValue()
    {
        if (!this.lastValueValid)
            throw new IllegalStateException("NaUtils#VanillaMerchant#RangedRandomInt: lastValue() requires to have run getValue() at least once already.");
        return this.lastValue;
    }

    @Override
    public String toString()
    {
        if (this.rndType == RandomizationType.FIXED_VALUE || this.minValue == this.maxValue)
            return String.format("%d", this.minValue);
        return String.format("[%d, %d]%s", this.minValue, this.maxValue, this.rndType.getName());
    }

    @Override
    public Integer get() {
        return this.getValue();
    }

    public double[] toArrayRepresentation()
    {
        switch (this.rndType)
        {
            case FIXED_VALUE: return new double[] {this.minValue};
            case UNIFORM: return new double[] {this.minValue, this.maxValue};
            case POISSON: return new double[] {this.minValue, this.maxValue, this.p};
            default: throw new IllegalArgumentException("Invalid randomization type");
        }
    }

    public static RangedRandomInt fromArrayRepresentation(double[] array)
    {
        switch (array.length) {
            case 1: return RangedRandomInt.fixed((int)(array[0]));
            case 2: return RangedRandomInt.uniform((int)(array[0]), (int)(array[1]));
            case 3: return RangedRandomInt.poisson((int)(array[0]), (int)(array[1]), array[2]);
            default: throw new IllegalArgumentException("Invalid array length");
        }
    }

    public static enum RandomizationType
    {
        FIXED_VALUE("Fixed"),
        POISSON("Poisson"),
        UNIFORM("Uniform");

        private String name;
        private RandomizationType(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    // Using value list: 1 element = fixed, 2 elements = uniform, 3 elements = Poisson (the 3rd value is Poisson factor p).
    public static final Codec<RangedRandomInt> CODEC = Codec.DOUBLE.listOf().comapFlatMap(inst ->
    {
        try
        {
            switch (inst.size())
            {
                case 1:
                    return DataResult.success(RangedRandomInt.fixed((int) Math.round(inst.get(0))));
                case 2:
                    return DataResult
                            .success(RangedRandomInt.uniform((int) Math.round(inst.get(0)), (int) Math.round(inst.get(1))));
                case 3:
                    return DataResult.success(RangedRandomInt.poisson((int) Math.round(inst.get(0)),
                            (int) Math.round(inst.get(1)), inst.get(2)));
                default:
                    return DataResult.error("RangedRandomInt: invalid length. Size 1 = fixed, 2 = uniform, 3 = poisson.");
            }
        } catch (IllegalArgumentException e)
        {
            return DataResult.error("RangedRandomInt: invalid value.");
        }
    }, inst ->
    {
        switch (inst.rndType)
        {
            case FIXED_VALUE:
                return List.of((double) inst.minValue);
            case UNIFORM:
                return List.of((double) inst.minValue, (double) inst.maxValue);
            case POISSON:
                return List.of((double) inst.minValue, (double) inst.maxValue, inst.p);
        }
        throw new RuntimeException();
    });
}
