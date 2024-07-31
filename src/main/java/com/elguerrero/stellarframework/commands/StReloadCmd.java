package com.elguerrero.stellarframework.commands;

import com.elguerrero.stellarframework.systems.CacheManagers;
import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.utils.*;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public final class StReloadCmd {

	private StReloadCmd() {
	}

	public static void registerReloadCmd() {

		try {

			CacheManagers.addCommand(new CommandAPICommand("reload")
					.withRequirement(sender -> StMixUtils.checkCommandRequirement(sender, "reload"))
					.withHelp("Reload the plugin", "Reload the plugin config and lang files.")
					.executes((sender, args) -> {

						try {

							StMixUtils.loadPluginConfigs();

							if (StMixUtils.senderIsConsole(sender)) {
								StMessageUtils.sendConsoleInfoMessage("&ei &aThe plugin has been reloaded. V");
							} else {

								Player player = (Player) sender;
								StMessageUtils.sendMessagePlayer(player, StellarPlugin.getBasicMessagesInstance().getPluginReloaded());
								StMessageUtils.sendDebugMessage("&ei &aThe plugin has been reloaded by " + player.getName() + ". V");
							}

						} catch (Exception ex) {

							if (!StMixUtils.senderIsConsole(sender)) {
								StMessageUtils.sendMessagePlayer((Player) sender, StellarPlugin.getBasicMessagesInstance().getPluginError());
								StErrorLogUtils.logErrorException(ex, sender.getName() + "has tried to reload the plugin but an error ocurred, please check your errors.log file in the plugin folder.");
							} else {
								StErrorLogUtils.logErrorException(ex, "The console has tried to reload the plugin but an error ocurred, please check your errors.log file in the plugin folder.");
							}
						}
					}));

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

}
