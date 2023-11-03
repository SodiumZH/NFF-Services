package net.sodiumstudio.nautils.math;

public class DoubleRandomSelection extends RandomSelection<Double>
{
	protected DoubleRandomSelection(double defaultValue)
	{
		super(defaultValue);
	}
	
	public double getDouble() {
		return getValue();
	}
}
