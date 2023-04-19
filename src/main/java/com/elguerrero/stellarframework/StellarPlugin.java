package com.elguerrero.stellarframework;

import com.elguerrero.stellarframework.addonsystem.AddonsManager;
import com.elguerrero.stellarframework.config.StellarConfig;
import com.elguerrero.stellarframework.config.StellarMessages;
import com.elguerrero.stellarframework.utils.StellarUtils;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.logging.Logger;

public abstract class StellarPlugin extends JavaPlugin{

	@Setter(AccessLevel.PROTECTED)
	private static StellarPlugin pluginInstance = null;

	@Getter
	private PluginManager pluginManager = null;
	@Getter
	private Logger pluginLogger = null;
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

			if (pluginInstance == null){
				setPluginInstance(this);
			}


			if (!StellarUtils.pluginFileExist(pluginFolder, true)){
				return;
			}

			setVariables();
			StellarUtils.loadPluginConfigs();
			StellarUtils.sendDebugMessage("The instance of the stellar framework is the plugin:" + pluginInstance.getName() + " , who have the main class:" + pluginInstance.getClass().getName());

			if (addonsEnabled) {
				addonsFolder = new File(getPluginInstance().getDataFolder().getPath() + "Addons");
			}

			StellarUtils.sendMessageDebugStatus();
			CommandAPI.onLoad(new CommandAPIConfig().silentLogs(StellarConfig.getDebug()).verboseOutput(StellarConfig.getDebug()));

		} catch (Exception ex) {
			StellarUtils.logErrorException(ex, "default");
		}
	}


	@Override
	public void onEnable() {
		try {

			CommandAPI.onEnable(pluginInstance);
			StellarUtils.registerCommands();

			if (addonsEnabled){
				AddonsManager.getInstance().loadAllAddons();
			}

			this.consoleSendPluginLoadMessage();

		} catch (Exception ex) {
			StellarUtils.logErrorException(ex, "default");
		}
	}

	@Override
	public void onDisable() {

		try {

			CommandAPI.onDisable();

		} catch (Exception ex) {
			StellarUtils.logErrorException(ex, "default");
		}

	}

	private void setVariables() {

		pluginManager = Bukkit.getPluginManager();
		pluginLogger = pluginInstance.getLogger();
		pluginFolder = pluginInstance.getDataFolder();
		errorsLog = new File(pluginFolder, "errors.log");
		pluginName = pluginInstance.getName();
		pluginDescription = pluginInstance.getDescription().getDescription();
		pluginVersion = pluginInstance.getDescription().getVersion();
		pluginAuthor = pluginInstance.getDescription().getAuthors().toString();

		setVariablesAbstract();

	}

	protected abstract void setVariablesAbstract();
	protected abstract void consoleSendPluginLoadMessage();

	public static StellarPlugin getPluginInstance(){

		if (pluginInstance == null) {
			throw new IllegalStateException("The instance of the plugin is not initialized yet, please contact the developer of the plugin.");
		}
		return pluginInstance;
	}

	public static StellarConfig getConfigInstance(){
		return pluginInstance.configInstance;
	}

	public static StellarMessages getMessagesInstance(){
		return pluginInstance.messagesInstance;
	}

}
