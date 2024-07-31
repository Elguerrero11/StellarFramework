package com.elguerrero.stellarframework;

import com.elguerrero.stellarframework.addonsystem.AddonsManager;
import com.elguerrero.stellarframework.config.StellarConfig;
import com.elguerrero.stellarframework.config.StellarMessages;
import com.elguerrero.stellarframework.utils.StErrorLogUtils;
import com.elguerrero.stellarframework.utils.StMessageUtils;
import com.elguerrero.stellarframework.utils.StMixUtils;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class StellarPlugin extends JavaPlugin {

	@Setter(AccessLevel.PROTECTED)
	private static StellarPlugin pluginInstance = null;

	private BukkitAudiences adventureAudience = null;
	@Getter
	private PluginManager bukkitPluginsManager = null;
	@Getter
	private Logger consoleLogger = null;
	@Getter
	private File pluginFolder = null;
	@Getter
	private File addonsFolder = null;
	@Getter
	private File errorsLog = null;

	@Setter(AccessLevel.PROTECTED)
	private StellarConfig configInstance = null;
	@Setter(AccessLevel.PROTECTED)
	private StellarMessages messagesInstance = null;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String pluginName = "";
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String pluginFormat = "";
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String pluginDescription = "";
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String pluginVersion = "";
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String pluginAuthor = "";

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String pluginAliase = "";

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private int helpCommandPages = 2;
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private boolean addonsEnabled = false;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private boolean pluginIsAStellarMinigame = false;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private boolean debugReportEnabled = true;

	private StellarPlugin() {
	}

	@Override
	public void onLoad() {
		try {

			if (pluginInstance == null) {
				setPluginInstance(this);
			}


			if (!StMixUtils.filePluginExist(pluginFolder, true)) {
				return;
			}

			setVariables();
			StMixUtils.loadPluginConfigs();
			StMessageUtils.sendDebugMessage("The instance of the stellar framework is the plugin:" + pluginInstance.getName() + " , who have the main class:" + pluginInstance.getClass().getName());

			if (addonsEnabled) {
				addonsFolder = new File(getPluginInstance().getDataFolder().getPath() + "Addons");
			}

			StMessageUtils.sendMessageDebugStatus();
			CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(StellarConfig.getDebug()).verboseOutput(StellarConfig.getDebug()));

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}
	}


	@Override
	public void onEnable() {
		try {

			CommandAPI.onEnable();
			StMixUtils.registerCommands();

			if (addonsEnabled) {
				AddonsManager.getInstance().loadAllAddons();
			}

			consoleSendPluginEnableMessage();

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}
	}

	@Override
	public void onDisable() {

		try {

			CommandAPI.onDisable();
			consoleSendPluginDisableMessage();

			if(this.adventureAudience != null) {
				this.adventureAudience.close();
				this.adventureAudience = null;
			}

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	private void setVariables() {

		bukkitPluginsManager = Bukkit.getPluginManager();
		this.adventureAudience = BukkitAudiences.create(this);
		consoleLogger = LoggerFactory.getLogger("Logger");
		pluginFolder = pluginInstance.getDataFolder();
		errorsLog = new File(pluginFolder, "errors.md");
		pluginName = pluginInstance.getName();
		pluginDescription = pluginInstance.getDescription().getDescription();
		pluginVersion = pluginInstance.getDescription().getVersion();
		pluginAuthor = pluginInstance.getDescription().getAuthors().toString();

		setVariablesAbstract();

	}

	protected abstract void setVariablesAbstract();

	protected abstract void consoleSendPluginEnableMessage();

	protected abstract void consoleSendPluginDisableMessage();

	public static StellarPlugin getPluginInstance() {

		if (pluginInstance == null) {
			throw new IllegalStateException("The instance of the plugin is not initialized yet, please contact the developer of the plugin.");
		}
		return pluginInstance;
	}

	public BukkitAudiences getAdventureAudience() {

		try {

			if (this.adventureAudience == null) {
				throw new IllegalStateException();
			}
			return this.adventureAudience;

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "Tried to access AdventureAudience when the plugin was disabled!");
		}

		return null;
	}

	public static StellarConfig getBasicConfigInstance() {
		return pluginInstance.configInstance;
	}

	public static StellarMessages getBasicMessagesInstance() {
		return pluginInstance.messagesInstance;
	}

}
