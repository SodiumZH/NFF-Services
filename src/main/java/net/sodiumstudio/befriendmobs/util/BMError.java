package net.sodiumstudio.befriendmobs.util;

import com.mojang.logging.LogUtils;

public class BMError {

	/**
	 * This method throws a BM-specified exception.
	 * <p> Warning: CRASH after calling! 
	 */
	public static void exception(String info)
	{
		throw new RuntimeException("BefriendedMobs Exception thrown: " + info);
	}
	
	/**
	 * Print a BM-specified error info to logger.
	 */
	public static void error(String info)
	{
		LogUtils.getLogger().error("BefriendedMobs Error: " + info);
	}
	
	/**
	 * Print a BM-specified warning info to logger.
	 */
	public static void warning(String info)
	{
		LogUtils.getLogger().warn("BefriendedMobs Earning: " + info);
	}
	
	
}
