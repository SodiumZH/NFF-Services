package net.sodiumstudio.befriendmobs.util;

import com.mojang.logging.LogUtils;

public class BMRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6582044165992602149L;

	protected final String msg;
	
	public BMRuntimeException(String info)
	{
		super(info);
		this.msg = info;
	}
	
	public String getInfo()
	{
		return msg;
	}
	
	/**
	 * Print the exception info to log as an error.
	 * Usually it's called in to catch block to report info instead of causing crash.
	 * @return a LogPrinter for adding more log info.
	 */
	public LogPrinter printError()
	{
		LogUtils.getLogger().error(msg);
		return new LogPrinter();
	}
	
	/**
	 * Print the exception info to log as a warning.
	 * Usually it's called in to catch block to report info instead of causing crash.
	 * @return a LogPrinter for adding more log info.
	 */
	public LogPrinter printWarning()
	{
		LogUtils.getLogger().warn(msg);
		return new LogPrinter();
	}
	
	public static class LogPrinter
	{
		/**
		 * Add a log error after the error/warning printed by the exception.
		 */
		public LogPrinter addError(String info)
		{
			LogUtils.getLogger().error(info);
			return this;
		}
		
		/**
		 * Add a log warning after the error/warning printed by the exception.
		 */
		public LogPrinter addWarning(String info)
		{
			LogUtils.getLogger().warn(info);
			return this;
		}
		
		/**
		 * Add a log info after the error/warning printed by the exception.
		 */
		public LogPrinter addInfo(String info)
		{
			LogUtils.getLogger().info(info);
			return this;
		}
	}
}
