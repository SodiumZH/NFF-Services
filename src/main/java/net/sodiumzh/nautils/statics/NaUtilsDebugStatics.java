package net.sodiumzh.nautils.statics;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sodiumzh.nautils.registries.NaUtilsConfigs;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class NaUtilsDebugStatics {

	private static final Set<String> REPORTED_ERROR_MSG = new HashSet<>();
	private static final Set<String> REPORTED_WARNING_MSG = new HashSet<>();

	public static void debugPrintToScreen(Component text, Player receiver) {
		if (NaUtilsConfigs.CACHED_DEBUG_MODE) {
			NaUtilsMiscStatics.printToScreen(text, receiver);
		}
	}

	public static void debugPrintToScreen(String text, Player receiver) {
		if (NaUtilsConfigs.CACHED_DEBUG_MODE) {
			NaUtilsMiscStatics.printToScreen(text, receiver);
		}
	}

	/**
	 * Report an error to logger if it has not been reported by this method.
	 * @param logger Use {@link LogUtils#getLogger} to provide a context-sensitive logger reference
	 * @param msg Error message.
	 */
	public static void errorOnce(Logger logger, String msg)
	{
		if (!REPORTED_ERROR_MSG.contains(msg))
		{
			logger.error(msg);
			REPORTED_ERROR_MSG.add(msg);
		}
	}

	/**
	 * Report a warning to logger if it has not been reported by this method.
	 * @param logger Use {@link LogUtils#getLogger} to provide a context-sensitive logger reference
	 * @param msg Warning message.
	 */
	public static void warnOnce(Logger logger, String msg)
	{
		if (!REPORTED_WARNING_MSG.contains(msg))
		{
			logger.warn(msg);
			REPORTED_WARNING_MSG.add(msg);
		}
	}

}
