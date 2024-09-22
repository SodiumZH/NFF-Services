package net.sodiumzh.nautils.exceptions;

/**
 * Thrown when something should be invoked only on a certain side, but actually invoked on another side.
 */
public class WrongSideException extends RuntimeException
{

	private static final long serialVersionUID = 1990333858334025102L;

	public WrongSideException(String msg)
	{
		super(msg);
	}
	
	public WrongSideException(String msg, Throwable cause)
	{
		super(msg);
	}
	
	public static WrongSideException serverOnly(String msg)
	{
		return new WrongSideException(msg + " Only on server.");
	}
	
	public static WrongSideException serverOnly(String msg, Throwable cause)
	{
		return new WrongSideException(msg + " Only on server.", cause);
	}

	public static WrongSideException clientOnly(String msg)
	{
		return new WrongSideException(msg + " Only on client.");
	}
	
	public static WrongSideException clientOnly(String msg, Throwable cause)
	{
		return new WrongSideException(msg + " Only on client.", cause);
	}
	
}
