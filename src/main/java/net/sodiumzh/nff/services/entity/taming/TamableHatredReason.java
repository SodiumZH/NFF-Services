package net.sodiumzh.nff.services.entity.taming;

public enum TamableHatredReason
{
	/** Not in hatred */
	NOT(-1), 
	/** The befriendable mob is attack by player */
	ATTACKED(0),
	/** The befriendable mob attacked the player */
	ATTACKING(1), 
	/** The hostile befriendable mob set target to player */
	SET_TARGET(2),
	/** Unused */
	PROVOKED(3), 
	/** The mob is hit by the player, but not taking damage */
	HIT(4), 
	/** The mob is damaged by the thorns enchantment of player */
	THORNS(5),
	CUSTOM_1(99), 
	CUSTOM_2(98),
	CUSTOM_3(97),
	CUSTOM_4(96), 
	CUSTOM_5(95), 
	CUSTOM_6(94), 
	CUSTOM_7(93),
	CUSTOM_8(92),
	CUSTOM_9(91);

	private int id;

	private TamableHatredReason(int inID)
	{
		id = inID;
	}

	public TamableHatredReason fromID(int inId) {
		for (TamableHatredReason r : values())
		{
			if (r.id() == inId)
				return r;
		}
		throw new IllegalArgumentException();
	}

	public int id() 
	{
		return this.id;
	}
	
	
}
