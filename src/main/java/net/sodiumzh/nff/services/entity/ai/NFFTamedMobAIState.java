package net.sodiumzh.nff.services.entity.ai;

import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;
import net.sodiumzh.nff.services.NFFServices;

public class NFFTamedMobAIState {
	
	private static final HashMap<ResourceLocation, NFFTamedMobAIState> STATES = new HashMap<ResourceLocation, NFFTamedMobAIState>();
	
	public static final NFFTamedMobAIState WAIT = new NFFTamedMobAIState(NFFServices.MOD_ID, "wait");
	public static final NFFTamedMobAIState FOLLOW = new NFFTamedMobAIState(NFFServices.MOD_ID, "follow");
	public static final NFFTamedMobAIState WANDER = new NFFTamedMobAIState(NFFServices.MOD_ID, "wander");

	/**
	 * ID for the state. Must be unique.
	 */
	private final ResourceLocation id;
	
	private NFFTamedMobAIState(ResourceLocation id)
	{
		this.id = id;
		if (STATES.containsValue(this))
			throw new IllegalArgumentException("Befriended AI State: duplicate state.");
		if (STATES.containsKey(id))
		{
			throw new IllegalArgumentException("Befriended AI State: duplicate id. \"" + this.toString() + "\" and \"" + STATES.get(id).toString()
				+ ". Please contact mod authors for compatibility issues.");
		}
			
		STATES.put(id, this);
	}

	private NFFTamedMobAIState(String modId, String key)
	{
		this(new ResourceLocation(modId, key));
	}
	
	/** Unique ID */
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	@Nullable
	public static NFFTamedMobAIState fromID(ResourceLocation id)
	{
		NFFTamedMobAIState res = STATES.get(id);	
		return res != null ? res : NFFTamedMobAIState.WAIT;
	}
	
	@Nullable
	public static NFFTamedMobAIState fromID(String modid, String key)
	{
		return fromID(new ResourceLocation(modid, key));
	}
	
	protected static final HashMap<NFFTamedMobAIState, MutableComponent> DISPLAY_INFO = new HashMap<NFFTamedMobAIState, MutableComponent>();
	static
	{
		DISPLAY_INFO.put(WAIT, NaUtilsInfoStatics.createTranslatable("info.nffservices.mob_wait"));
		DISPLAY_INFO.put(FOLLOW, NaUtilsInfoStatics.createTranslatable("info.nffservices.mob_follow"));
		DISPLAY_INFO.put(WANDER, NaUtilsInfoStatics.createTranslatable("info.nffservices.mob_wander"));
	}
	
	public static void putDisplayInfo(NFFTamedMobAIState state, MutableComponent info)
	{
		DISPLAY_INFO.put(state, info);
	}
	
	public MutableComponent getDisplayInfo()
	{
		if (DISPLAY_INFO.containsKey(this))
			return DISPLAY_INFO.get(this).copy();
		else
		{
			NFFServices.LOGGER.error("AI State missing display info: " + this.toString());
			return NaUtilsInfoStatics.createText("");			
		}
	}

	/**
	 * @deprecated Legacy from enum. Use {@code getAllStates()} instead.
	 */
	@Deprecated
	public static Collection<NFFTamedMobAIState> values()
	{
		return getAllStates();
	}
	
	/**
	 * Get all available ai states.
	 */
	public static Collection<NFFTamedMobAIState> getAllStates()
	{
		return STATES.values();
	}
	
	
}
