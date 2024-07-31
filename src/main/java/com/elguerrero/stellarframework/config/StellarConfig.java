package com.elguerrero.stellarframework.config;

import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.utils.StErrorLogUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.serialization.YamlSerializer;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.MergeRule;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public abstract class StellarConfig {

	@Getter
	private static String lang = "";
	@Getter
	private static Boolean debug = false;
	@Getter
	private static Boolean bStats = null;
	@Getter
	private static Boolean autoUpdater = false;
	@Getter
	protected YamlDocument configFile = null;
	protected String configFilePath;
	protected InputStream inputStream;
	protected String configVersionKeyPath;
	protected Boolean thisConfigAutoUpdateEnabled;
	protected YamlSerializer serializer = null;

	protected StellarConfig(Boolean thisConfigAutoUpdateEnabled) {

		this.thisConfigAutoUpdateEnabled = thisConfigAutoUpdateEnabled;
		configFilePath = "config.yml";
		inputStream = StellarPlugin.getPluginInstance().getResource("config.yml");
		configVersionKeyPath = "Config_Version";

		createConfigFile();

	}

	protected StellarConfig(Boolean thisConfigAutoUpdateEnabled, String configFilePath, String inputStream, String configVersionKeyPath) {

		this.thisConfigAutoUpdateEnabled = thisConfigAutoUpdateEnabled;
		this.configFilePath = configFilePath;
		this.inputStream = StellarPlugin.getPluginInstance().getResource(inputStream);
		this.configVersionKeyPath = configVersionKeyPath;

		createConfigFile();

	}

	protected StellarConfig(Boolean thisConfigAutoUpdateEnabled, String configFilePath, String inputStream, String configVersionKeyPath, YamlSerializer serializer) {

		this.thisConfigAutoUpdateEnabled = thisConfigAutoUpdateEnabled;
		this.configFilePath = configFilePath;
		this.inputStream = StellarPlugin.getPluginInstance().getResource(inputStream);
		this.configVersionKeyPath = configVersionKeyPath;
		this.serializer = serializer;

		createConfigFile();

	}

	public void loadConfigFile() {

		try {

			createConfigFile();
			callLoadConfigVariables();

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	protected void createConfigFile() {

		try {

			final GeneralSettings generalSettingsDefault = GeneralSettings.DEFAULT;
			final GeneralSettings generalSettings2 = GeneralSettings.builder().setSerializer(serializer).build();

			final LoaderSettings loaderSettingsDefault = LoaderSettings.builder().setAutoUpdate(false).setAllowDuplicateKeys(false).build();
			final LoaderSettings loaderSettings2 = LoaderSettings.builder().setAutoUpdate(autoUpdater).setAllowDuplicateKeys(false).build();

			final DumperSettings dumperSettingsDefault = DumperSettings.DEFAULT;

			final UpdaterSettings updaterSettingsDefault = UpdaterSettings.builder().setEnableDowngrading(false).build();
			final UpdaterSettings updaterSettings2 = UpdaterSettings.builder().setVersioning(new BasicVersioning(configVersionKeyPath)).setEnableDowngrading(false)
					.setMergeRule(MergeRule.MAPPINGS, true).setMergeRule(MergeRule.MAPPING_AT_SECTION, true).setMergeRule(MergeRule.SECTION_AT_MAPPING, true)
					.setKeepAll(true).build();


			if (thisConfigAutoUpdateEnabled) {

				if (serializer != null) {

					configFile = YamlDocument.create(new File(StellarPlugin.getPluginInstance().getPluginFolder(), configFilePath), inputStream,
							generalSettings2,
							loaderSettings2,
							dumperSettingsDefault,
							updaterSettings2);

				} else {

					configFile = YamlDocument.create(new File(StellarPlugin.getPluginInstance().getPluginFolder(), configFilePath), inputStream,
							generalSettingsDefault,
							loaderSettings2,
							dumperSettingsDefault,
							updaterSettings2);

				}

			} else {

				if (serializer != null) {

					configFile = YamlDocument.create(new File(StellarPlugin.getPluginInstance().getPluginFolder(), configFilePath), inputStream,
							generalSettings2,
							loaderSettingsDefault,
							dumperSettingsDefault,
							updaterSettingsDefault);

				} else {

					configFile = YamlDocument.create(new File(StellarPlugin.getPluginInstance().getPluginFolder(), configFilePath), inputStream,
							generalSettingsDefault,
							loaderSettingsDefault,
							dumperSettingsDefault,
							updaterSettingsDefault);

				}

			}

		} catch (IOException ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	public static void setGeneralVariables() {

		try {

			lang = StellarPlugin.getBasicConfigInstance().getConfigFile().getString("Lang");
			debug = StellarPlugin.getBasicConfigInstance().getConfigFile().getBoolean("Debug_Mode");
			bStats = StellarPlugin.getBasicConfigInstance().getConfigFile().getBoolean("BStats");
			autoUpdater = StellarPlugin.getBasicConfigInstance().getConfigFile().getBoolean("Auto_Update_Configs");

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	protected void callLoadConfigVariables() {

		try {

			loadConfigVariables();

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	abstract void loadConfigVariables();

}