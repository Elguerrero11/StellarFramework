package com.elguerrero.stellarframework.addonsystem;


import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.api.AddonsInterfaz;
import com.elguerrero.stellarframework.utils.StErrorLogUtils;
import com.elguerrero.stellarframework.utils.StMessageUtils;
import com.elguerrero.stellarframework.utils.StMixUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.jorel.commandapi.CommandAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class AddonsManager {

	private HashMap<String, StellarAddon> enabledAddons = new HashMap<>();
	private HashMap<String, StellarAddon> disabledAddons = new HashMap<>();

	@Getter
	private final Path jarPath = null;

	private static AddonsManager INSTANCE = null;

	public AddonsManager() {
	}

	// Methods for utility

	public void loadAllAddons() {


		if (StellarPlugin.getPluginInstance().isAddonsEnabled()) {

			StMessageUtils.sendConsoleInfoMessage("&aLoading plugin addons...");

			try {

				File addonsFolder = StellarPlugin.getPluginInstance().getAddonsFolder();
				StMixUtils.filePluginExist(StellarPlugin.getPluginInstance().getAddonsFolder(), true);
				File[] addonsFiles = addonsFolder.listFiles();


				if (addonsFiles.length != 0) {


					for (File addonFile : addonsFiles) {

						String addonFileName = addonFile.getName();

						if (addonFile.isFile() && addonFileName.endsWith(".jar")) {

							loadAddon(addonFileName);
						}
					}
				} else {

					StMessageUtils.sendConsoleInfoMessage("&cNo addons founds in the plugin folder!");

				}

			} catch (Exception ex) {
				StErrorLogUtils.logErrorException(ex, "default");
				StMessageUtils.sendConsoleWarnMessage("&cError loading the plugin addons!");
			}


		} else {

			StMessageUtils.sendDebugMessage("&cThis plugin have not a addon system!");

		}

	}

	public void loadAddon(String jarFileName) {

		File addonFile = new File(StellarPlugin.getPluginInstance().getAddonsFolder(), jarFileName);

		try (URLClassLoader addonClassLoader = new URLClassLoader(new URL[]{addonFile.toURI().toURL()}, AddonsManager.class.getClassLoader())) {

			InputStream inputStream = addonClassLoader.getResourceAsStream("addon.yml");
			if (inputStream == null) {

			    // If the yaml file is not found in the addon.jar
				StMessageUtils.sendConsoleWarnMessage("&cThe plugin addons folder have the " + jarFileName + " but it is not a addon as it have not addon.yml inside the jar!");
				return;
			}

			YamlDocument yamlDocument = YamlDocument.create(new File("addon.yml"), inputStream);

			String addonName = yamlDocument.getString("name");
			String addonVersion = yamlDocument.getString("version");
			String mainClass = yamlDocument.getString("main");
			String plugin = yamlDocument.getString("plugin");
			Boolean config = yamlDocument.getBoolean("config");
			List<String> addonPluginDependencies = yamlDocument.getStringList("dependencies");
			List<String> addonAuthors = yamlDocument.getStringList("authors");

			// Check if the addon is for this plugin and continue the code or not
			if (!addonIsForThisPlugin(jarFileName, plugin)) {
				StMessageUtils.sendConsoleWarnMessage("&cAddon " + jarFileName + " is not intended for this plugin!");
				return;
			}

			// Check if all the addon dependencies are enabled or not and if not add the addon to the disabled addons list
			if (!areAddonDependenciesEnabled(addonPluginDependencies, jarFileName)) {
				StMessageUtils.sendConsoleWarnMessage("&cDisabling addon " + jarFileName + " as it does not have all the required addon dependencies installed!");
				INSTANCE.disabledAddons.put(addonName, new StellarAddon(addonName, addonVersion, mainClass, plugin, addonPluginDependencies, addonAuthors, null, null));
				return;
			}

			// Check if the addon have config or messages file so it need to generate a addon folder
			if (config){
				StMixUtils.filePluginExist(new File(StellarPlugin.getPluginInstance().getAddonsFolder(), addonName), true);
			}

			// Check if the addon have a main class and load some methods from the addon from the interface
			Class<?> thisMainClass = addonClassLoader.loadClass(mainClass);
			Method getInstanceMethod = thisMainClass.getMethod("getInstance");

			if (AddonsInterfaz.class.isAssignableFrom(thisMainClass)) {
				AddonsInterfaz addonInstance = (AddonsInterfaz) getInstanceMethod.invoke(null);

				addonInstance.addonLoad();

				// Set the minigame name variable in the addon class

				addonInstance.setAddonMinigameName(StellarPlugin.getPluginInstance().getPluginName());

				// Save in variables the addon eventListeners and commandsNames
				Set<Listener> addonEventListeners = addonInstance.getEventListeners();
				Set<String> addonCommandsNames = addonInstance.getCommandNames();


				// Build the addon object and add it to the enabled addons list
				new StellarAddon(addonName, addonVersion, mainClass, plugin, addonPluginDependencies, addonAuthors, addonEventListeners, addonCommandsNames);

			}


			StMessageUtils.sendConsoleInfoMessage("&aAddon " + addonName + " loaded V");

		} catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
				 InvocationTargetException ex) {
			StErrorLogUtils.logErrorException(ex, "default");
			StMessageUtils.sendConsoleWarnMessage("&cError loading supposed addon " + jarFileName + " !");
		}
	}

	/**
	 * Check if the addon dependencies are installed
	 *
	 * @param addonPluginDependencies - The addon dependencies
	 * @param jarFileName             - The addon jar file name
	 *
	 * @return true if all dependencies are installed, false otherwise
	 */
	public boolean areAddonDependenciesEnabled(List<String> addonPluginDependencies, String jarFileName) {

		Set<String> enabledPluginNames = Arrays.stream(Bukkit.getPluginManager().getPlugins())
				.filter(Plugin::isEnabled)
				.map(Plugin::getName)
				.collect(Collectors.toSet());

		for (String dependency : addonPluginDependencies) {
			if (!enabledPluginNames.contains(dependency)) {
				StMessageUtils.sendDebugMessage("&cThe addon " + jarFileName + " have the dependency " + dependency + " that is not installed!");
				return false;
			} else {
				StMessageUtils.sendDebugMessage("&aThe addon " + jarFileName + " have the dependency " + dependency + " that is installed!");
			}
		}
		StMessageUtils.sendDebugMessage("&aThe addon " + jarFileName + " have all the dependencies installed!");
		return true;
	}

	/**
	 * 1- Check if is a StellarMinigame addon and this plugin is a StellarMinigame plugin 2- Check if the addon is a
	 * addon of this plugin
	 *
	 * @param jarFileName - The addon jar file name
	 * @param plugin      - The plugin name of the plugin that the addon is for
	 */
	public boolean addonIsForThisPlugin(String jarFileName, String plugin) {

		if (StellarPlugin.getPluginInstance().isPluginIsAStellarMinigame() && plugin.equalsIgnoreCase("StellarMinigame")) {

			StMessageUtils.sendDebugMessage("&aThe addon " + jarFileName + " is for this StellarMinigame plugin!");

			return true;

		} else if (plugin.equalsIgnoreCase(StellarPlugin.getPluginInstance().getPluginName())) {

			StMessageUtils.sendDebugMessage("&aThe addon " + jarFileName + " is for this plugin!");

			return true;

		} else {

			StMessageUtils.sendConsoleWarnMessage("&cThe addon " + jarFileName + " is not for this plugin!");
			StMessageUtils.sendDebugMessage("&cThe addon is for " + plugin + " plugin.");

			return false;
		}

	}

	public boolean addonJarExists(String addonName) {
		File addonFile = new File(StellarPlugin.getPluginInstance().getAddonsFolder(), addonName + ".jar");
		return addonFile.exists();
	}

	// Short methods

	public void reloadAddonConfig(StellarAddon addon) {
		try {
			ClassLoader pluginClassLoader = StellarPlugin.getPluginInstance().getClass().getClassLoader();
			Class<?> thisMainClass = pluginClassLoader.loadClass(addon.getMainClass());
			Method getInstanceMethod = thisMainClass.getMethod("getInstance");

			if (AddonsInterfaz.class.isAssignableFrom(thisMainClass)) {
				AddonsInterfaz addonInstance = (AddonsInterfaz) getInstanceMethod.invoke(null);
				addonInstance.addonConfigReload();
			}
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			StErrorLogUtils.logErrorException(e, "default");
		}
	}

	public void registerAddon(StellarAddon addon) {

		enabledAddons.put(addon.getAddonName(), addon);

		registerAddonEvents(addon);

	}

	public void unregisterAddon(StellarAddon addon) {

		String addonName = addon.getAddonName();

		enabledAddons.remove(addonName);
		disabledAddons.put(addonName, addon);

		unregisterAddonEvents(addon);
	    unregisterAddonCommands(addon);

	}

	private void registerAddonEvents(StellarAddon addon) {

		Set<Listener> addonEventListeners = addon.getEventsListeners();
		for (Listener listener : addonEventListeners) {

			Bukkit.getPluginManager().registerEvents(listener, StellarPlugin.getPluginInstance());
		}

	}

	private void unregisterAddonEvents(StellarAddon addon) {

		Set<Listener> addonEventListeners = addon.getEventsListeners();
		for (Listener listener : addonEventListeners) {

			HandlerList.unregisterAll(listener);
		}

	}

	private void unregisterAddonCommands(StellarAddon addon) {

		Set<String> addonCommandsNames = addon.getCommandsNames();
		for (String commandName : addonCommandsNames) {

			CommandAPI.unregister(commandName);
		}

	}

	public Map<String, StellarAddon> getEnabledAddons() {
		return new HashMap<>(enabledAddons);
	}

	public Map<String, StellarAddon> getDisabledAddons() {
		return new HashMap<>(disabledAddons);
	}

	public static AddonsManager getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new AddonsManager();
		}

		return INSTANCE;

	}


}
