package net.sodiumzh.nff.services.entity.taming;

public class TamableInteractionResult {
	
	public boolean handled = false;
	public boolean shouldCancelEvent = false;
	public INFFTamed nFFTamed = null;

	public static TamableInteractionResult of(boolean handled, boolean shouldCancelEvent, INFFTamed befriended)
	{
		TamableInteractionResult res = new TamableInteractionResult();
		res.nFFTamed = befriended;
		res.handled = handled;
		res.shouldCancelEvent = shouldCancelEvent;
		return res;
	}

	public void setHandled()
	{
		handled = true;
	}
}
