package com.elguerrero.stellarframework.config;

import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.commands.StHelpCmd;
import com.elguerrero.stellarframework.utils.StErrorLogUtils;
import com.elguerrero.stellarframework.utils.StMessageUtils;
import com.elguerrero.stellarframework.utils.StMixUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.MergeRule;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public abstract class StellarMessages {

	@Getter
	protected YamlDocument messagesFile = null;
	protected String messagesFilePath;
	protected InputStream inputStream;
	protected String messagesVersionKeyPath = "Lang_Version";

	protected String selectedLang = "";

	protected final File langFolder = new File(StellarPlugin.getPluginInstance().getPluginFolder(), "Lang");

	@Getter
	protected String pluginPrefix = "";
	@Getter
	protected String pluginDebugPrefix = "";
	@Getter
	protected String pluginReloaded = "";
	@Getter
	protected String pluginError = "";
	@Getter
	protected String noPermission = "";
	@Getter
	protected String debugEnabled = "";
	@Getter
	protected String debugDisabled = "";
	@Getter
	protected String debugStatusEnabled = "";
	@Getter
	protected String debugStatusDisabled = "";
	@Getter
	protected String debugMessageFormat = "";
	@Getter
	protected String addonAlreadyEnabled = "";
	@Getter
	protected String addonAlreadyDisabled = "";
	@Getter
	protected String addonDisabled = "";
	@Getter
	protected String addonEnabled = "";
	@Getter
	protected String addonNotFound = "";
	@Getter
	protected String addonNotRegistered = "";
	@Getter
	protected String addonCannotReload = "";
	@Getter
	protected String addonReloaded = "";

	// Help page

	@Getter
	protected List<List<String>> helpPages = new ArrayList<>();
	@Getter
	protected String helpPageFirstLine = "";
	@Getter
	protected String helpPageLastLine = "";
	@Getter
	protected String helpNextPageArrow = "";
	@Getter
	protected String helpPreviousPageArrow = "";
	@Getter
	protected String previousPageArrowHover = "";
	@Getter
	protected String nextPageArrowHover = "";
	//@Getter
    //protected String helpPageNotExist = "";


	protected StellarMessages() {

		messagesFilePath = langFolder.getPath() + selectedLang + ".yml";
		inputStream = StellarPlugin.getPluginInstance().getResource(langFolder.getPath() + selectedLang);

	}

	protected void loadSelectedLang(){

		final String Lang = StellarConfig.getLang();

		try {

			if (Lang.isBlank()){
				selectedLang = "en_US";
				StMessageUtils.sendConsoleWarnMessage("The language selected in the config is not valid, the default language 'en_US' will be used.");
			}

			if (checkLangFileExist(Lang)){
				selectedLang = Lang;
				StMessageUtils.sendConsoleInfoMessage("&aLanguage " + Lang + " selected V");
			}

		} catch (Exception ex){
			StErrorLogUtils.logErrorException(ex,"default");
		}

	}

	protected boolean checkLangFileExist(String fileName) {

		try {

			final File yamlFile = new File(StellarPlugin.getPluginInstance().getPluginFolder() + "Lang", fileName + ".yml");

			if (StMixUtils.filePluginExist(new File("Lang"),true)) {

				return yamlFile.exists() && yamlFile.isFile();

			}
			return false;

		} catch (Exception ex){
			StErrorLogUtils.logErrorException(ex,"default");
			return false;
		}
	}

	public void loadMessagesFile() {

		try {

			loadSelectedLang();

			if (StellarConfig.getAutoUpdater()){

				messagesFile = YamlDocument.create(new File(StellarPlugin.getPluginInstance().getPluginFolder(), messagesFilePath), inputStream,
						GeneralSettings.DEFAULT,
						LoaderSettings.builder().setAutoUpdate(StellarConfig.getAutoUpdater()).setAllowDuplicateKeys(false).build(),
						DumperSettings.DEFAULT,
						UpdaterSettings.builder().setVersioning(new BasicVersioning(messagesVersionKeyPath)).setEnableDowngrading(false)
								.setMergeRule(MergeRule.MAPPINGS,true).setMergeRule(MergeRule.MAPPING_AT_SECTION,true).setMergeRule(MergeRule.SECTION_AT_MAPPING,true)
								.setKeepAll(true).build());

			} else {

				messagesFile = YamlDocument.create(new File(StellarPlugin.getPluginInstance().getPluginFolder(), messagesFilePath), inputStream,
						GeneralSettings.DEFAULT,
						LoaderSettings.builder().setAutoUpdate(StellarConfig.getAutoUpdater()).setAllowDuplicateKeys(false).build(),
						DumperSettings.DEFAULT,
						UpdaterSettings.builder().setEnableDowngrading(false).build());

			}

			callLoadMessagesVariables();

		} catch (IOException ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	protected void callLoadMessagesVariables() {

		try {

			pluginPrefix = messagesFile.getString("Plugin_Prefix");
			pluginDebugPrefix = messagesFile.getString("Plugin_Debug_Prefix");
			pluginReloaded = messagesFile.getString("Plugin_Reloaded");
			pluginError = messagesFile.getString("Plugin_Error");
			noPermission = messagesFile.getString("No_Permission");
			debugStatusEnabled = messagesFile.getString("Debug_Status_Enabled");
			debugStatusDisabled = messagesFile.getString("Debug_Status_Disabled");
			debugMessageFormat = messagesFile.getString("Debug_Message_Format");
			addonAlreadyEnabled = messagesFile.getString("Addon_Already_Enabled");
			addonAlreadyDisabled = messagesFile.getString("Addon_Already_Disabled");
			addonDisabled = messagesFile.getString("Addon_Disabled");
			addonEnabled = messagesFile.getString("Addon_Enabled");
			addonNotFound = messagesFile.getString("Addon_Not_Found");
			addonNotRegistered = messagesFile.getString("Addon_Not_Registered");
			addonCannotReload = messagesFile.getString("Addon_Cannot_Reload");
			addonReloaded = messagesFile.getString("Addon_Reloaded");

			// Help page

			helpPageFirstLine = messagesFile.getString("Help_Page_First_Line");
			helpPageLastLine = messagesFile.getString("Help_Page_Last_Line");
			helpNextPageArrow = messagesFile.getString("Help_Next_Page_Arrow");
			helpPreviousPageArrow = messagesFile.getString("Help_Previous_Page_Arrow");
			previousPageArrowHover = messagesFile.getString("Previous_Page_Arrow_Hover");
			nextPageArrowHover = messagesFile.getString("Next_Page_Arrow_Hover");
			//helpPageNotExist = messagesFile.getString("Help_Page_Not_Exist");
			StHelpCmd.addHelpPage("Help_Page_1");

			loadMessagesVariables();

		} catch (Exception ex){
			StErrorLogUtils.logErrorException(ex,"default");
		}

	}

	abstract void loadMessagesVariables();

}