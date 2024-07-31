package com.elguerrero.stellarframework.utils;

import com.elguerrero.stellarframework.StellarPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StAddonsUtils {

	private StAddonsUtils() {
	}

	public static void sendMsgAddonIsNotRegistered(CommandSender sender, String addonName) {

		if (StMixUtils.senderIsConsole(sender)) {
			sendMsgInfoConsoleWithAddonName("&cThe addon %addon% is in the addons folder but it is not registered, restart the server or reload the plugin to register it.", addonName);
		} else {
			sendMsgWithAddonName(sender, StellarPlugin.getBasicMessagesInstance().getAddonNotRegistered(), addonName);
		}

	}

	public static void sendMsgAddonNotFound(CommandSender sender, String addonName) {

		if (StMixUtils.senderIsConsole(sender)) {
			sendMsgInfoConsoleWithAddonName("&cThe addon %addon% can't be found in the addons folder.", addonName);
		} else {
			sendMsgWithAddonName(sender, StellarPlugin.getBasicMessagesInstance().getAddonNotFound(), addonName);
		}

	}

	public static void sendMsgWithAddonName(CommandSender sender, String message, String addonName) {

		StMessageUtils.sendMessagePlayer((Player) sender, message.replace("%addon%", addonName));
	}

	public static void sendMsgInfoConsoleWithAddonName(String message, String addonName) {

		StMessageUtils.sendConsoleInfoMessage(message.replace("%addon%", addonName));
	}

}
