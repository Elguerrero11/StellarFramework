package com.elguerrero.stellarframework.utils;

import com.elguerrero.stellarframework.StellarPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class StErrorLogUtils {

	private StErrorLogUtils() {
	}


	/**
	 * Log an exception in the errors.log file
	 * Check if the errors.log file exists, if not, create it
	 * If there is an error while logging the exception, log it in the console
	 * Both, the error exception and the argument exception of the method
	 * Too send a message to the console saying that an error ocurred with the plugin
	 *
	 * @param ex - The error exception to log
	 */
	public static void logErrorException(Exception ex, String consoleErrorMsg) {

		if (mustSendConsoleErrorMsg()){
			sendPluginErrorConsole(ex);
			return;
		}

		final File errorsLog = StellarPlugin.getPluginInstance().getErrorsLog();

		if (!StMixUtils.filePluginExist(errorsLog, false)) {
			sendPluginErrorConsole(ex);
			return;
		}

		String formattedDate = getFormatDate();
		String exceptionStack = getExceptionStackTrace(ex);

		writeToErrorsLog(errorsLog, formattedDate, ex, exceptionStack);
		sendConsoleErrorMsg(consoleErrorMsg);

	}

	private static boolean mustSendConsoleErrorMsg(){
		return StellarPlugin.getPluginInstance() == null || StellarPlugin.getPluginInstance().getPluginFolder() == null;
	}

	private static String getFormatDate() {
		Date date = new Date();
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	private static String getExceptionStackTrace(Exception ex) {
		return Arrays.stream(ex.getStackTrace())
				.map(StackTraceElement::toString)
				.collect(Collectors.joining("\n"));
	}

	private static void writeToErrorsLog(File errorsLog, String formattedDate, Exception ex, String exceptionStack) {

		try {

			try (FileWriter writer = new FileWriter(errorsLog, true);
				 PrintWriter printWriter = new PrintWriter(writer)) {

				printWriter.println("### <span style='color:#046E70'>Error date</span>");
				printWriter.println("**<span style='color:#1D7C7E'>" + formattedDate + "</span>**");
				printWriter.println("### <span style='color:#F1C232'>Exception type</span>");
				printWriter.println("**<span style='color:#F1C232'>" + ex + "</span>**");
				printWriter.println("### <span style='color:#F22424'>Exception StackTrace</span>");
				printWriter.println("```");
				printWriter.println(exceptionStack);
				printWriter.println("```");
				printWriter.println("# ");
			}

		} catch (Exception e) {
			sendPluginErrorConsole(e);
		}

	}

	private static void sendConsoleErrorMsg(String consoleErrorMsg){

		final String defaultConsoleMessage = "An error ocurred with the plugin, please check the errors.log file in the plugin folder.";

		if ("default".equals(consoleErrorMsg)) {
			StMessageUtils.sendConsoleErrorMessage(defaultConsoleMessage);
		} else {
			StMessageUtils.sendConsoleErrorMessage(consoleErrorMsg);
		}
	}

	/**
	 * Method used for manage some errors in the plugin that I cant always manage with errors.log
	 *
	 * @param ex - The exception to send to the console
	 */
	private static void sendPluginErrorConsole(Exception ex) {
		StMessageUtils.sendConsoleWarnMessage("---------------------------------------");
		StMessageUtils.sendConsoleWarnMessage("          " + StellarPlugin.getPluginInstance().getPluginName());
		StMessageUtils.sendConsoleWarnMessage("              Error ocurred:");

		Arrays.stream(ex.getStackTrace())
				.map(StackTraceElement::toString)
				.forEach(StMessageUtils::sendConsoleWarnMessage);

		StMessageUtils.sendConsoleWarnMessage("---------------------------------------");
	}

}
