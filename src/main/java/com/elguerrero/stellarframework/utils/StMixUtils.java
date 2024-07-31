package com.elguerrero.stellarframework.utils;

import com.elguerrero.stellarframework.systems.CacheManagers;
import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.commands.StDebugReportCmd;
import com.elguerrero.stellarframework.commands.StHelpCmd;
import com.elguerrero.stellarframework.commands.addonscommands.StDisableCmd;
import com.elguerrero.stellarframework.commands.addonscommands.StEnableCmd;
import com.elguerrero.stellarframework.commands.addonscommands.StListCmd;
import com.elguerrero.stellarframework.commands.addonscommands.StReloadCmd;
import com.elguerrero.stellarframework.config.StellarConfig;
import com.elguerrero.stellarframework.config.StellarMessages;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StMixUtils {

	private StMixUtils() {
	}


	public static boolean senderIsConsole(Object sender) {
		return sender instanceof Console;
	}

	public static boolean checkPlayerPermission(Player player, String permission) {

		if (player.hasPermission(StellarPlugin.getPluginInstance().getPluginName() + ".*") || player.hasPermission(StellarPlugin.getPluginInstance().getPluginName() + "." + permission)) {
			player.sendMessage(StellarPlugin.getBasicMessagesInstance().getNoPermission());
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Check if a file or folder exists, if not, create it
	 *
	 * @param file     - The file or folder to check
	 * @param isFolder - If the file is a folder or not
	 */
	public static boolean filePluginExist(File file, boolean isFolder) {

		try {
			if (!file.exists()) {

				boolean result;

				if (isFolder) {
					StMessageUtils.sendDebugMessage("Creating folder named: " + file.getName() + " in path: " + file.getParent());
					result = file.mkdir();
					StMessageUtils.sendDebugMessage("Folder created: " + result);
				} else {
					StMessageUtils.sendDebugMessage("Creating file named: " + file.getName() + " in path: " + file.getParent());
					result = file.createNewFile();
					StMessageUtils.sendDebugMessage("File created: " + result);
				}
				return result;

			} else {
				StMessageUtils.sendDebugMessage("File or folder named: " + file.getName() + " already exists in path: " + file.getParent());
				return true;
			}

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
			return false;
		}

	}

	public static void writeToFile(File file, String content) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(content);
		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}
	}

	private static File checkDebugLogExist(File folderPath) {

		final String currentDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
		final File debugLog = new File(folderPath, "debugLog-" + currentDate + ".md");

		if (!debugLog.exists()) {
			filePluginExist(debugLog, false);
		}
		return debugLog;

	}

	public static void logDebugMessage(String message) {

		try {

			final File debugLogsFolder = new File(StellarPlugin.getPluginInstance().getPluginFolder(), "DebugLogs");

			filePluginExist(debugLogsFolder, true);

			final File todayDebugLog = checkDebugLogExist(debugLogsFolder);

			writeToFile(todayDebugLog, "<span style='color:#046E70'> [DEBUG] </span>" + "<span style='color:#07B7BA'>" + message + "</span>");

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	/**
	 * Check if a plugin is enabled
	 *
	 * @return - If the plugin is enabled or not
	 */
	public static boolean isPluginEnabled(String pluginName) {
		Plugin plugin = StellarPlugin.getPluginInstance().getBukkitPluginsManager().getPlugin(pluginName);
		return plugin != null && plugin.isEnabled();
	}

	// Plugin specified methods

	/**
	 * Load the plugin configs and messages with the lang system
	 */
	public static void loadPluginConfigs() {

		final StellarConfig stellarConfig = StellarPlugin.getBasicConfigInstance();
		final StellarMessages stellarMessages = StellarPlugin.getBasicMessagesInstance();

		if (stellarConfig != null){
			StellarConfig.setGeneralVariables();
			stellarConfig.loadConfigFile();
		}

		if (stellarMessages != null){
			stellarMessages.loadMessagesFile();
		}

	}

	public static boolean checkCommandRequirement(CommandSender sender, String permission) {
		return senderIsConsole(sender) || checkPlayerPermission((Player) sender, permission);
	}

	public static void disableThisPlugin() {
		StellarPlugin.getPluginInstance().getBukkitPluginsManager().disablePlugin(StellarPlugin.getPluginInstance());
	}

	public static void registerCommands() {

		// TODO: Separar el metodo en al menos 1 o 2 mas pequeños, como el registro de los comandos de los addons y el registro de los comandos del plugin principal
		// TODO: Esto porque esta poco leible ahora mismo

		StHelpCmd.registerInfoCmd();
		StReloadCmd.registerReloadAddonCmd();

		if (StellarPlugin.getPluginInstance().isDebugReportEnabled()) {
			StDebugReportCmd.registerDebugReportCmd();
		}


		CommandAPICommand pluginCommands = new CommandAPICommand(StellarPlugin.getPluginInstance().getPluginName())
				.withAliases(StellarPlugin.getPluginInstance().getPluginAliase());
				/*.executes((sender, args) -> {
					StHelpCmd.sendHelpMessage(sender);
				});*/

		CacheManagers.getPluginCommandsList().forEach(pluginCommands::withSubcommand);

		if (StellarPlugin.getPluginInstance().isAddonsEnabled()){

			StDisableCmd.registerDisableAddonCmd();
			StEnableCmd.registerEnableAddonCmd();
			StListCmd.registerListAddonsCmd();
			StReloadCmd.registerReloadAddonCmd();

			CommandAPICommand addonsCommands = new CommandAPICommand("addon");

			CacheManagers.getPluginAddonsCommands().forEach(addonsCommands::withSubcommand);

			pluginCommands.withSubcommand(addonsCommands);

		}

		pluginCommands.register();

		clearCommandsListHashmapCache();

	}

	private static void clearCommandsListHashmapCache() {
		CacheManagers.getPluginCommandsList().clear();
		CacheManagers.getPluginAddonsCommands().clear();
	}

}
