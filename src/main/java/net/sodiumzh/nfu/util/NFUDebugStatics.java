package net.sodiumzh.nfu.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nfu.registry.NFUConfigs;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class NFUDebugStatics {

	private static final Set<String> REPORTED_ERROR_MSG = new HashSet<>();
	private static final Set<String> REPORTED_WARNING_MSG = new HashSet<>();

	public static void debugPrintToScreen(Component text, Player receiver) {
		if (NFUConfigs.CACHED_DEBUG_MODE) {
			NFUMiscStatics.printToScreen(text, receiver);
		}
	}

	public static void debugPrintToScreen(String text, Player receiver) {
		if (NFUConfigs.CACHED_DEBUG_MODE) {
			NFUMiscStatics.printToScreen(text, receiver);
		}
	}

	/**
	 * Report an error to logger if it has not been reported by this method.
	 * @param callerClass The class in which the error is reported. Usually
	 * {@code this.getClass()} or {@code CallingClass.class}.
	 * @param msg Error message.
	 * @return Whether printed.
	 */
	public static boolean errorOnce(Class<?> callerClass, String msg)
	{
		if (!REPORTED_ERROR_MSG.contains(msg))
		{
			LoggerFactory.getLogger(callerClass).error(msg);
			REPORTED_ERROR_MSG.add(msg);
			return true;
		}
		else return false;
	}

	public static boolean errorOnce(String msg)
	{
		Class<?> callerClass;
		boolean missingClass = false;
		try {
			callerClass = Class.forName(Thread.currentThread().getStackTrace()[1].getClassName());
		} catch (Exception e) {
            callerClass = NFUDebugStatics.class;
			missingClass = true;
        }
        return errorOnce(callerClass, (missingClass ? "<Missing Source Class> " : "") + msg);
	}

	/**
	 * Report a warning to logger if it has not been reported by this method.
	 * @param callerClass The class in which the warning is reported. Usually
	 * {@code this.getClass()} or {@code CallingClass.class}.
	 * @param msg Warning message.
	 * @return Whether printed.
	 */
	public static boolean warnOnce(Class<?> callerClass, String msg)
	{
		if (!REPORTED_WARNING_MSG.contains(msg))
		{
			LoggerFactory.getLogger(callerClass).warn(msg);
			REPORTED_WARNING_MSG.add(msg);
			return true;
		}
		else return false;
	}

	public static boolean warnOnce(String msg)
	{
		Class<?> callerClass;
		boolean missingClass = false;
		try {
			callerClass = Class.forName(Thread.currentThread().getStackTrace()[1].getClassName());
		} catch (Exception e) {
			callerClass = NFUDebugStatics.class;
			missingClass = true;
		}
		return warnOnce(callerClass, (missingClass ? "<Missing Source Class> " : "") + msg);
	}
}
