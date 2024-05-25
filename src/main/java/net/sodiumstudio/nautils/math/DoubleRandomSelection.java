package net.sodiumstudio.nautils.math;

/**
 * @deprecated Use {@link RandomSelection<Double>} instead.
 */
@Deprecated
public final class DoubleRandomSelection extends RandomSelection<Double>
{
	protected DoubleRandomSelection(double defaultValue)
	{
		super(defaultValue);
	}
	
	public double getDouble() {
		return getValue();
	}
	
	public DoubleRandomSelection add(double value, double probability)
	{
		super.add(value, probability);
		return this;
	}
}
