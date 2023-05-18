package net.sodiumstudio.befriendmobs.entity.capability;

import java.util.HashMap;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumstudio.befriendmobs.registry.BefMobCapabilities;
import net.sodiumstudio.befriendmobs.util.Wrapped;
import net.sodiumstudio.befriendmobs.util.annotation.DontOverride;
import net.sodiumstudio.befriendmobs.util.annotation.DontCallManually;

// A capability which posts LivingAttributeValueChangeEvent when the given attribute value changes.
public interface CAttributeMonitor {

	public LivingEntity getOwner();
	
	/**
	 * Get the listened attribute list
	 * Key: attribute register key
	 * Value: current attribute value
	 */
	public HashMap<String, Double> getListenList();
	
	/** Add an attribute to the listen list.
	 * It still works if the attribute isn't available yet (e.g. on capability attachment).
	*/
	@SuppressWarnings("deprecation")
	public default CAttributeMonitor listen(Attribute attribute)
	{
		// Use NaN to label an attribute position before entity attributes creation
		double val = getOwner().getAttributes() == null ? Double.NaN : getOwner().getAttributeValue(attribute);
		getListenList().put(Registry.ATTRIBUTE.getKey(attribute).toString(), val);
		return this;
	}
	
	// Update and detect change on tick
	@SuppressWarnings("deprecation")
	@DontOverride
	@DontCallManually
	public default void tick()
	{
		for (String key: getListenList().keySet())
		{
			Attribute attr = Registry.ATTRIBUTE.get(new ResourceLocation(key));
			double oldVal = getListenList().get(key);
			double newVal;
			if (attr == null)
				newVal = Double.NaN;
			else newVal	= getOwner().getAttributeValue(attr);	
			// NaN indicates the value is not available yet, so don't post event but still update value
			// After attribute is created the value will update to non-NaN
			if (!Double.isNaN(oldVal)
				&& !Double.isNaN(newVal)
				&& (oldVal - newVal > 0.0000001 || oldVal - newVal < -0.0000001))
			{			
				MinecraftForge.EVENT_BUS.post(new LivingAttributeValueChangeEvent(
						getOwner(), attr, oldVal, newVal));
				MinecraftForge.EVENT_BUS.post(new ChangeEvent(
						getOwner(), attr, oldVal, newVal));
			}
			getListenList().put(key, newVal);
		}
	}
	
	/**
	* Add listened attribute to a living entity. 
	* The living entity must have CAttributeMonitor capability attached.
	*/
	@DontOverride
	public static CAttributeMonitor listen(LivingEntity living, Attribute attr)
	{
		Wrapped<CAttributeMonitor> cap = new Wrapped<CAttributeMonitor>(null);
		living.getCapability(BefMobCapabilities.CAP_ATTRIBUTE_MONITOR).ifPresent((c) -> 
		{
			cap.set(c);
			c.listen(attr);
		});
		if (cap.get() != null)
			return cap.get();
		else throw new IllegalStateException("Living entity missing attribute monitor capability.");
	}
	
	public static class SetupEvent extends Event
	{
		public final LivingEntity living;
		public final CAttributeMonitor monitor;
		public SetupEvent(LivingEntity living, CAttributeMonitor cap)
		{
			this.living = living;
			this.monitor = cap;
		}
		
		public void addListen(Attribute attr)
		{
			monitor.listen(attr);
		}	
	}
	
	public class ChangeEvent extends Event {

		public final LivingEntity entity;
		public final Attribute attribute;
		public final double oldValue;
		public final double newValue;
		
		public ChangeEvent(LivingEntity entity, Attribute attribute,
				double oldValue, double newValue)
		{
			this.entity = entity;
			this.attribute = attribute;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
	}
}
