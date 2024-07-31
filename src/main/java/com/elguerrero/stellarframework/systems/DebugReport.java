package com.elguerrero.stellarframework.systems;

import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.utils.StErrorLogUtils;
import com.elguerrero.stellarframework.utils.StMessageUtils;
import com.elguerrero.stellarframework.utils.StMixUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class DebugReport {

	private static DebugReport instance = null;
	private final File debugReportsFolder = new File(StellarPlugin.getPluginInstance().getPluginFolder(), "DebugReports");

	private final Path pluginFolderPath = StellarPlugin.getPluginInstance().getPluginFolder().toPath();
	private final String separator = File.separator;


	private final List<Path> filesToCopy = Arrays.asList(
			Paths.get(pluginFolderPath + separator +  "DebugLogs"),
			Paths.get(pluginFolderPath + separator + "errors.md"),
			Paths.get(pluginFolderPath + separator + "debugReportLog.md")
	);

	private DebugReport() {
	}

	private void setVariables() {

		StMixUtils.filePluginExist(debugReportsFolder, true);

	}

	public void generateDebugReport() {

		try {

			final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			final File debugReportFolder = new File(debugReportsFolder, "DebugReport_" + LocalDateTime.now().format(dateFormatter));
			final Path debugReportFolderPath = debugReportFolder.toPath();


			if (!StMixUtils.filePluginExist(debugReportsFolder, true)) {
				return;
			}

			if (!StMixUtils.filePluginExist(debugReportFolder, true)) {
				return;
			}

			getInstance().setVariables();

			createDebugReportLog(debugReportFolder);
			copyFilesToCopy(debugReportFolderPath);

			zipDebugReportFolder(debugReportFolder);

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}
	}

	private void createDebugReportLog(File debugReportFolder) {

		try {

			final File debugLogFile = new File(debugReportFolder, "debugReportLog.md");
			final String pluginsList = Arrays.stream(Bukkit.getPluginManager().getPlugins())
					.filter(Plugin::isEnabled)
					.map(Plugin::getName)
					.collect(Collectors.joining(", "));


			StMixUtils.filePluginExist(debugLogFile, true);

			StMixUtils.writeToFile(debugLogFile, "<span style='color:#1D7C7E'>[Java version]</span> <span style='color:#465678'>" + System.getProperty("java.version") + "</span>");
			StMixUtils.writeToFile(debugLogFile, "<span style='color:#1D7C7E'>[Server platform and version]</span> <span style='color:#465678'>" + Bukkit.getVersion() + "</span>");
			StMixUtils.writeToFile(debugLogFile, "<span style='color:#1D7C7E'>[Version of " + StellarPlugin.getPluginInstance().getPluginName() + "]</span> <span style='color:#465678'>" + StellarPlugin.getPluginInstance().getPluginVersion() + "</span>");
			StMixUtils.writeToFile(debugLogFile, "<span style='color:#1D7C7E'>[Plugins]</span> <span style='color:#465678'>" + pluginsList + "</span>");
			StMixUtils.writeToFile(debugLogFile, "<span style='color:#1D7C7E'>[Operating System]</span> <span style='color:#465678'>" + System.getProperty("os.name") + "</span>");


		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	private void copyFilesToCopy(Path debugReportFolderPath) {

		try {

			for (Path file : filesToCopy) {

				Files.copy(file, debugReportFolderPath, StandardCopyOption.COPY_ATTRIBUTES);
				StMessageUtils.sendDebugMessage("File copied: " + file + " to " + debugReportFolderPath);

			}

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}

	private void zipDebugReportFolder(File debugReportFolder) {
		final File parentFolder = debugReportFolder.getParentFile();
		final File debugZippedFolder = new File(parentFolder, debugReportFolder.getName() + ".zip");

		try (FileOutputStream fos = new FileOutputStream(debugZippedFolder);
			 ZipOutputStream zos = new ZipOutputStream(fos)) {

			try (Stream<Path> filesStream = Files.walk(debugReportFolder.toPath())) {
				filesStream.filter(path -> !Files.isDirectory(path))
						.forEach(path -> {
							String relativePath = debugReportFolder.toPath().relativize(path).toString();
							ZipEntry zipEntry = new ZipEntry(relativePath);
							try {
								zos.putNextEntry(zipEntry);
								Files.copy(path, zos);
								zos.closeEntry();
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
			}

			try (Stream<Path> filesStream = Files.walk(debugReportFolder.toPath())) {
				filesStream.sorted(Comparator.reverseOrder())
						.map(Path::toFile)
						.forEach(file -> {
							try {
								Files.delete(file.toPath());
							} catch (IOException e) {
								StErrorLogUtils.logErrorException(e, "default");
							}
						});
			}

			StMessageUtils.sendConsoleInfoMessage("&aDebug report generated with success in: " + debugReportsFolder);

		} catch (IOException ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}
	}

	public static DebugReport getInstance() {
		if (instance == null) {
			instance = new DebugReport();
		}
		return instance;
	}


}
