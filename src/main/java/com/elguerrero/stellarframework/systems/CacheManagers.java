package com.elguerrero.stellarframework.systems;

import dev.jorel.commandapi.CommandAPICommand;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CacheManagers {

	private CacheManagers() {
	}

	// Commands manager
	@Getter
	private static List<CommandAPICommand> pluginCommandsList = new ArrayList<>();

	@Getter
	private static List<CommandAPICommand> pluginAddonsCommands = new ArrayList<>();


	public static void addCommand(CommandAPICommand command){
		pluginCommandsList.add(command);
	}

	public static void addAddonCommand(CommandAPICommand command){
		pluginAddonsCommands.add(command);
	}

	// Experience bar timer manager

	@Getter
	private static final Map<Player, BukkitTask> timerTask = new HashMap<>();

    public static void addTimerTask(Player player, BukkitTask task) {
		timerTask.put(player, task);
	}

	public static void removeTimerTask(Player player) {
		timerTask.remove(player);
	}

	//TODO: Addons manager ?

}
