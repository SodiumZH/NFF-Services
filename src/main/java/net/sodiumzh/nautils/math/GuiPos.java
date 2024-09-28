package net.sodiumzh.nautils.math;

/**
 * A {@code GuiPos} is an xy (or uv) integer pair pointing at a position on the screen or a texture image.
 */
public class GuiPos 
{
	public static final GuiPos ZERO = new GuiPos();
	
	public final int x;
	public final int y;
	
	public GuiPos(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Makes a {@code GuiPos} of which x == y == val.
	 */
	public GuiPos(int val)
	{
		this(val, val);
	}
	
	/**
	 * Makes a zero value.
	 */
	public GuiPos()
	{
		this(0, 0);
	}

	public static GuiPos valueOf(int x, int y)
	{
		return new GuiPos(x, y);
	}
	
	public static GuiPos valueOf(int val)
	{
		return new GuiPos(val);
	}
	
	public static GuiPos zero()
	{
		return ZERO;
	}
	
	public boolean equals(GuiPos other)
	{
		return this.x == other.x && this.y == other.y;
	}
	
	@Deprecated
	public GuiPos copy()
	{
		return new GuiPos(this.x, this.y);
	}
	
	@Deprecated
	public GuiPos set(int x, int y)
	{
		return new GuiPos(x, y);		
	}
	
	public GuiPos negate()
	{
		return new GuiPos(-this.x, -this.y);
	}

	public GuiPos add(GuiPos other)
	{
		return new GuiPos(this.x + other.x, this.y + other.y);	
	}

	/**
	 * @deprecated use {@code subtract} instead
	 */
	@Deprecated
	public GuiPos minus(GuiPos other)
	{
		return new GuiPos(this.x - other.x, this.y - other.y);	
	}
	
	/**
	 * Subtract a value from this {@code GuiPos}. This action will change the caller.
	 * @return this.
	 */
	public GuiPos subtract(GuiPos other)
	{
		return new GuiPos(this.x + other.x, this.y + other.y);	
	}
	
	/**
	 * Add an X value to this {@code GuiPos}. This action will change the caller.
	 * @return this.
	 */
	public GuiPos addX(int other)
	{
		return add(other, 0);
	}
	
	/**
	 * Add a y value to this {@code GuiPos}. This action will change the caller.
	 * @return this.
	 */
	public GuiPos addY(int other)
	{
		return add(0, other);
	}
	 
	/**
	 * Add a value to both X and Y of this {@code GuiPos}. This action will change the caller.
	 * @return this.
	 */
	public GuiPos add(int other)
	{
		return add(other, other);
	}
	
	/**
	 * Add two values to X and Y of this {@code GuiPos} respectively. This action will change the caller.
	 * @return this.
	 */
	public GuiPos add(int x, int y)
	{
		return add(new GuiPos(x, y));
	}
	
	/* Utility for inventory menu XY handling */
	
	/**
	 * Get the position of the n item slots below.
	 * <p>Dedicated for cases when the GuiPos represents an item slot position. 
	 * <p>This action will change the caller.
	 * @return this.
	 */
	public GuiPos slotBelow(int n)
	{
		return addY(18 * n);
	}
	
	/**
	 * Get the position of the item slot below.
	 * <p>Dedicated for cases when the GuiPos represents an item slot position. 
	 * <p>This action will change the caller.
	 * @return this.
	 */
	public GuiPos slotBelow()
	{
		return slotBelow(1);
	}
	
	/**
	 * Get the position of the n item slots at right.
	 * <p>Dedicated for cases when the GuiPos represents an item slot position. 
	 * <p>This action will change the caller.
	 * @return this.
	 */
	public GuiPos slotRight(int n)
	{
		return addX(18 * n);
	}
	
	/**
	 * Get the position of the slot at right.
	 * <p>Dedicated for cases when the GuiPos represents an item slot position. 
	 * <p>This action will change the caller.
	 * @return this.
	 */
	public GuiPos slotRight()
	{
		return slotRight(1);
	}

	/**
	 * Get the position of the n item slots at right.
	 * <p>Dedicated for cases when the GuiPos represents an item slot position. 
	 */
	public GuiPos slotAbove(int n) {
		return addY(-18 * n);
	}
	
	public GuiPos slotAbove() {
		return slotAbove(1);
	}
	
	public GuiPos slotLeft(int n) {
		return addX(-18 * n);
	}
	
	public GuiPos slotLeft() {
		return slotLeft(1);
	}

	// Get slot coordinate from this as the base point.
	// e.g. for base point v, v.coord(1,2) means v.slotRight(1).slotBelow(2)
	// For locating slot in 2D asset array of item slots.
	public GuiPos coord(int x, int y)
	{
		return add(18 * x, 18 * y);
	}
	
	@Override
	public String toString()
	{
		return "GuiPos(" + Integer.toString(x) + ", " + Integer.toString(y) + ")";
	}
	
}
