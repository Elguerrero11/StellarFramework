package com.elguerrero.stellarframework.utils;

import com.elguerrero.stellarframework.StellarPluginFramework;
import com.elguerrero.stellarframework.config.StellarConfig;
import com.elguerrero.stellarframework.config.StellarLangManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DebugReport {

	private static final File PLUGIN_DEBUG_REPORTS_FOLDER = new File(StellarPluginFramework.getPLUGIN_FOLDER(),"Debug reports");

	// The lines that contains this strings will be ignored in the debug report
	private static final List<String> ignoreLines = Arrays.asList("# Define the address and port for the database.",
			"# - The standard DB engine port is used by default",
			"#   (MySQL and MariaDB: 3306)",
			"# The name of the database to store LuckPerms data in.",
			"# Credentials for the database.");

	// The lines that contains this strings will be replaced with "# This line is ignored for security" in the debug report
	// That is for the security of the users
	private static final Map<String, String> replaceLines = new HashMap<>() {{
		put("Host:", "# The Host: line is omited for security");
		put("Port:", "# The Port: line is omited for security");
		put("Database_Name:", "# The Database_Name: line is omited for security");
		put("Username:", "# The Username: line is omited for security");
		put("Password:", "# The Password: line is omited for security");
	}};

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(StellarConfig.getDATE_FORMAT());


	public static void generateDebugReport() {

		try {

			final File debugReportFolder = new File(PLUGIN_DEBUG_REPORTS_FOLDER, "Debug_" + LocalDateTime.now().format(DATE_FORMAT));

			checkDebugReportsFolder();
			debugReportFolder.mkdir();

			// Copy the config file to the debug report folder
			// Don't copy the lines that contains the strings in the ignoreLines list
			// Replace the lines that contains the strings in the replaceLines map with the value of the map

			File originalConfigFile = new File(StellarPluginFramework.getPLUGIN_FOLDER().getPath(), "config.yml");
			File debugConfigFile = new File(debugReportFolder.getPath(), "config.yml");
			FileUtils.copyFile(originalConfigFile, debugConfigFile,StandardCopyOption.COPY_ATTRIBUTES);


			final List<String> debugConfigLines = Files.readAllLines(originalConfigFile.toPath(), StandardCharsets.UTF_8).stream()
					.filter(line -> ignoreLines.stream().noneMatch(line::contains))
					.map(line -> replaceLines.getOrDefault(line, line))
					.collect(Collectors.toList());
			Files.write(debugReportFolder.toPath().resolve("config.yml"), debugConfigLines, StandardCharsets.UTF_8);


			// Copy the lang folder to the debug report folder with all the lang files

			final File originalLangFolder = new File(StellarLangManager.getLANG_FOLDER().getPath());
			final File debugLangFolder = new File(debugReportFolder.getPath());

			FileUtils.copyDirectory(originalLangFolder, debugLangFolder, true);

			// If exist, copy the errors.log file to the debug report folder

			final File originalErrorsLogFile = new File(StellarPluginFramework.getPLUGIN_FOLDER() + "errors.log");

			if (originalErrorsLogFile.exists()){

			final File debugReportErrorsLogFile = new File(debugReportFolder.getPath());
			FileUtils.copyFile(originalErrorsLogFile, debugReportErrorsLogFile, StandardCopyOption.COPY_ATTRIBUTES);

			}

			// Create the debug.log file with the debug info about the plugin and server

			final File debugLogFile = new File(debugReportFolder.getPath(), "debug.log");

			final boolean pluginEnabled = StellarPluginFramework.getPLUGIN_MANAGER().isPluginEnabled(StellarPluginFramework.getINSTANCE());
			final String platform = Bukkit.getVersion().replace("git-", "").replace("\\(MC: .+\\)", "");


			String pluginsList = Arrays.stream(Bukkit.getPluginManager().getPlugins())
					.map(Plugin::getName)
					.collect(Collectors.joining(", "));

			FileWriter writer = new FileWriter(debugLogFile);

			writer.write("[Java version] " + System.getProperty("java.version") + "\n");
			writer.write("[Minecraft version] " + Bukkit.getBukkitVersion()  + "\n");
			writer.write("[Server platform] " + platform + "\n");
			writer.write("[Plugin status] " + (pluginEnabled ? "Enabled V" : "Disabled X") + "\n");
			writer.write("[Version of " + StellarPluginFramework.getPLUGIN_NAME() + "] " + StellarPluginFramework.getPLUGIN_VERSION() + "\n");
			writer.write("[Plugins] " + pluginsList + "\n");

			writer.close();

			// Create the debug.zip file with all the debug report files
			final File normalDebugFolder = new File(debugReportFolder.getPath());
			final File debugZippedFolder = new File(normalDebugFolder, normalDebugFolder.getName());


			// Create the ZIP file
			final FileOutputStream fos = new FileOutputStream(debugZippedFolder);
			final ZipOutputStream zos = new ZipOutputStream(fos);

			// Add the original debug report folder to the debug report zip file
			for (File file : FileUtils.listFiles(normalDebugFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
				String relativePath = normalDebugFolder.toURI().relativize(file.toURI()).getPath();
				ZipEntry zipEntry = new ZipEntry(relativePath);
				zos.putNextEntry(zipEntry);
				zos.write(IOUtils.toByteArray(file.toURI()));
				zos.closeEntry();
			}

			// Close the zip objects
			zos.close();
			fos.close();

			// Remove the old debug report folder that is not zipped
			FileUtils.deleteDirectory(debugReportFolder);

			// Send to the console the message of that the debug report was generated with success in the debug report folder
			StellarUtils.sendConsoleInfoMessage("&aDebug report generated with success in: " + PLUGIN_DEBUG_REPORTS_FOLDER);

		} catch (Exception ex) {
			StellarUtils.sendPluginErrorConsole(ex);
		}
	}


	private static void checkDebugReportsFolder() {

		try {
			if (!PLUGIN_DEBUG_REPORTS_FOLDER.exists()) {
				PLUGIN_DEBUG_REPORTS_FOLDER.mkdirs();
			}
		} catch (Exception ex) {
			StellarUtils.sendPluginErrorConsole(ex);
		}

	}

}