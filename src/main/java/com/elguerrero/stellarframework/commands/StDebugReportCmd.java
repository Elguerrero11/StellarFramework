package com.elguerrero.stellarframework.commands;

import com.elguerrero.stellarframework.systems.CacheManagers;
import com.elguerrero.stellarframework.systems.DebugReport;
import com.elguerrero.stellarframework.utils.StErrorLogUtils;
import com.elguerrero.stellarframework.utils.StMixUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.ConsoleCommandExecutor;

public final class StDebugReportCmd {

	private StDebugReportCmd() {
	}

	public static void registerDebugReportCmd() {

		try {

			CacheManagers.addCommand(new CommandAPICommand("debugreport")
							.withRequirement(StMixUtils::senderIsConsole)
							.executesConsole((ConsoleCommandExecutor) (sender, args) -> DebugReport.getInstance().generateDebugReport()));

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}

	}
}
