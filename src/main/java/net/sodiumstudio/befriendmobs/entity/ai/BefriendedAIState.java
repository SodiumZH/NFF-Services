package net.sodiumstudio.befriendmobs.entity.ai;

import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.util.InfoHelper;

public class BefriendedAIState {
	
	private static final HashMap<Integer, BefriendedAIState> STATES = new HashMap<Integer, BefriendedAIState>();
	
	public static final BefriendedAIState WAIT = new BefriendedAIState(0);
	public static final BefriendedAIState FOLLOW = new BefriendedAIState(1);
	public static final BefriendedAIState WANDER = new BefriendedAIState(2);
	
	/**
	 * @deprecated Use custom-defined new ai state instead. Create with {@code newState(id)}.
	 */
	@Deprecated
	public static final BefriendedAIState CUSTOM_0 = new BefriendedAIState(99);
	/**
	 * @deprecated Use custom-defined new ai state instead. Create with {@code newState(id)}.
	 */
	@Deprecated
	public static final BefriendedAIState CUSTOM_1 = new BefriendedAIState(98);
	/**
	 * @deprecated Use custom-defined new ai state instead. Create with {@code newState(id)}.
	 */
	@Deprecated
	public static final BefriendedAIState CUSTOM_2 = new BefriendedAIState(97);
	
	/**
	 * Integer ID for the state
	 */
	public final int id;
	
	private BefriendedAIState(int id)
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
	
	/**
	 * Create a new AI state.
	 * The ID must be unique among all states, so it's recommended to make it unlikely to collide with other mods. 
	 */
	public BefriendedAIState newState(int id)
	{
		return new BefriendedAIState(id);
	}
	
	/**
	 * Use id property instead
	 */
	@Deprecated
	public int id()
	{
		return this.id;
	}
	
	@Nullable
	public static BefriendedAIState fromID(int id)
	{
		return STATES.get(id);		
	}
	
	@Deprecated
	public static BefriendedAIState fromID(byte id)
	{
		return fromID((int)id);
	}
	
	/**
	 * This function is unreliable. Use if-else blocks instead.
	 */
	@Deprecated
	public BefriendedAIState defaultSwitch()
	{
		return fromID(id + 1 ) != null ? fromID(id + 1) : fromID(0);
	}
	
	protected static final HashMap<BefriendedAIState, MutableComponent> DISPLAY_INFO = new HashMap<BefriendedAIState, MutableComponent>();
	static
	{
		DISPLAY_INFO.put(WAIT, InfoHelper.createTrans("info.befriendmobs.mob_wait"));
		DISPLAY_INFO.put(FOLLOW, InfoHelper.createTrans("info.befriendmobs.mob_follow"));
		DISPLAY_INFO.put(WANDER, InfoHelper.createTrans("info.befriendmobs.mob_wander"));
	}
	
	public static void putDisplayInfo(BefriendedAIState state, MutableComponent info)
	{
		DISPLAY_INFO.put(state, info);
	}
	
	public MutableComponent getDisplayInfo()
	{
		if (DISPLAY_INFO.containsKey(this))
			return DISPLAY_INFO.get(this).copy();
		else
		{
			BefriendMobs.LOGGER.error("AI State missing display info: " + this.toString());
			return InfoHelper.createText("");			
		}
	}

	/**
	 * @deprecated Legacy from enum. Use {@code getAllStates()} instead.
	 */
	@Deprecated
	public static Collection<BefriendedAIState> values()
	{
		return getAllStates();
	}
	
	/**
	 * Get all available ai states.
	 */
	public static Collection<BefriendedAIState> getAllStates()
	{
		return STATES.values();
	}
	
	
}
