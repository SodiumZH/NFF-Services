package net.sodiumzh.nautils.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.RandomSource;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * A random double generator with a given range.
 * <p>
 * It now supports 3 types of distribution: fixed (always outputing the same value), uniform
 * distribution and truncated normal distribution.
 */
public class RangedRandomDouble {
    private static final RandomSource RND = RandomSource.create();
    private final double minValue;
    private final double maxValue;
    // Limit divided by the normal distribution's standard deviation.
    private final double limitFactor;
    private final RangedRandomDouble.RandomizationType rndType;
    private double lastValue;
    private boolean lastValueValid = false;

    private RangedRandomDouble(double min, double max, double limitFactor, RangedRandomDouble.RandomizationType type)
    {
        this.minValue = min;
        this.maxValue = max;
        this.limitFactor = limitFactor;
        this.rndType = type;
        this.lastValue = min;
    }

    /**
     * Get a {@code RangedRandomDouble} representing a fixed value.
     */
    public static RangedRandomDouble fixed(double value)
    {
        var res = new RangedRandomDouble(value, value, 1d, RangedRandomDouble.RandomizationType.FIXED_VALUE);
        res.lastValue = value;
        res.lastValueValid = true;
        return res;
    }

    /**
     * Get a {@code RangedRandomDouble} with probabilities of truncated normal distribution.
     * @Param limitFactor Limit divided by the normal distribution's standard deviation.
     */
    public static RangedRandomDouble truncatedNormal(double min, double max, double limitFactor)
    {
        if (max - min < -1e-12 || limitFactor <= 0) throw new IllegalArgumentException();
        if (Math.abs(max - min) < 1e-12) return fixed(min);
        return new RangedRandomDouble(min, max, limitFactor, RangedRandomDouble.RandomizationType.TRUNCATED_NORMAL);
    }

    /**
     * Get a {@code RangedRandomDouble} with probabilities of Poisson distribution with p = 0.5.
     */
    public static RangedRandomDouble truncatedNormal(double min, double max)
    {
        return truncatedNormal(min, max, 1d);
    }

    /**
     * Get a {@code RangedRandomDouble} with probabilities of uniform distribution.
     */
    public static RangedRandomDouble uniform(double min, double max)
    {
        if (max - min < -1e-12) throw new IllegalArgumentException();
        if (Math.abs(max - min) < 1e-12) return fixed(min);
        return new RangedRandomDouble(min, max, 1, RangedRandomDouble.RandomizationType.UNIFORM);
    }

    public RangedRandomDouble setUniform()
    {
        return new RangedRandomDouble(this.minValue, this.maxValue, this.limitFactor, RangedRandomDouble.RandomizationType.UNIFORM);
    }

    /**
     * Get a new {@code RangedRandomDouble} with the same min and max, using truncated-normal distribution randomization
     * with given limit factor.
     */
    public RangedRandomDouble setTruncatedNormal(double limitFactor)
    {
        return new RangedRandomDouble(this.minValue, this.maxValue, limitFactor, RangedRandomDouble.RandomizationType.TRUNCATED_NORMAL);
    }

    public RangedRandomDouble setRange(int min, int max)
    {
        return new RangedRandomDouble(min, max, this.limitFactor, this.rndType);
    }

    /**
     * Get a random value using the given RandomSource.
     */
    public double getValue(RandomSource rnd)
    {
        double res = (this.minValue + this.maxValue) / 2;
        switch (rndType)
        {
            case FIXED_VALUE:
            {
                res = this.minValue;
                break;
            }
            case UNIFORM:
            {
                res = this.minValue + RND.nextDouble() * (this.maxValue - this.minValue);
                break;
            }
            case TRUNCATED_NORMAL:
            {
                if (this.minValue == this.maxValue) return this.minValue;
                double pos = RND.nextGaussian();
                int ctrl = 0;   // To avoid the loop running too many times and lagging the game
                while (pos > limitFactor || pos < -limitFactor)
                {
                    pos = RND.nextGaussian();
                    if (ctrl > 100) {
                        // Running too many times means the limit factor is very small,
                        // so pick 0 in this case
                        pos = 0;
                        break;
                    }
                    ctrl++;
                }
                pos = (pos / (2 * limitFactor)) + 0.5d;    // Normalize to [0, 1]
                res = this.minValue + pos * (this.maxValue - this.minValue);
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
        this.lastValue = res;
        this.lastValueValid = true;
        return res;
    }

    /**
     * Get a random value using the internal RandomSource.
     */
    public double getValue()
    {
        return this.getValue(RND);
    }

    /**
     * Get the value last output.
     * @throws IllegalStateException If it has never output via {@code getValue}.
     */
    public double lastValue()
    {
        if (!this.lastValueValid)
            throw new IllegalStateException("NaUtils#RangedRandomDouble: lastValue() requires to have run getValue() at least once already.");
        return this.lastValue;
    }

    @Override
    public String toString()
    {
        if (this.rndType == RangedRandomDouble.RandomizationType.FIXED_VALUE || this.minValue == this.maxValue)
            return String.format("%d", this.minValue);
        return String.format("[%d, %d]%s", this.minValue, this.maxValue, this.rndType.getName());
    }

    private static enum RandomizationType
    {
        FIXED_VALUE("Fixed"),
        TRUNCATED_NORMAL("truncated_normal"),
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
    public static final Codec<RangedRandomDouble> CODEC = Codec.DOUBLE.listOf().comapFlatMap(inst ->
    {
        try
        {
            switch (inst.size())
            {
                case 1:
                    return DataResult.success(RangedRandomDouble.fixed(inst.get(0)));
                case 2:
                    return DataResult
                            .success(RangedRandomDouble.uniform(inst.get(0), inst.get(1)));
                case 3:
                    return DataResult.success(RangedRandomDouble.truncatedNormal(inst.get(0),
                            inst.get(1), inst.get(2)));
                default:
                    return DataResult.error("RangedRandomDouble: invalid length. Size 1 = fixed, 2 = uniform, 3 = truncated normal.");
            }
        } catch (IllegalArgumentException e)
        {
            return DataResult.error("RangedRandomDouble: invalid value.");
        }
    }, inst ->
    {
        switch (inst.rndType)
        {
            case FIXED_VALUE:
                return List.of((double) inst.minValue);
            case UNIFORM:
                return List.of((double) inst.minValue, (double) inst.maxValue);
            case TRUNCATED_NORMAL:
                return List.of((double) inst.minValue, (double) inst.maxValue, inst.limitFactor);
        }
        throw new RuntimeException();
    });

}
