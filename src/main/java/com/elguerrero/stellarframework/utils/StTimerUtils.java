package com.elguerrero.stellarframework.utils;

import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.systems.CacheManagers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public final class StTimerUtils {

	private StTimerUtils() {
	}

	public void setExperienceTimer(Player player, int seconds, boolean staticBar) {

		final Map<Player, BukkitTask> timerTasks = CacheManagers.getTimerTask();
		final StellarPlugin plugin = StellarPlugin.getPluginInstance();

		if (timerTasks.containsKey(player)) {
			timerTasks.get(player).cancel();
		}


		BukkitTask task = new BukkitRunnable() {
			int counter = seconds;
			int tickCounter = 0;
			final float expPerTick = 1.0f / 10.0f;

			@Override
			public void run() {

				if (counter == 0) {
					Bukkit.getScheduler().runTask(plugin, () -> {
						player.setLevel(0);
						player.setExp(0);
						timerTasks.remove(player);
					});
					this.cancel();
				} else {
					Bukkit.getScheduler().runTask(plugin, () -> {
						player.setLevel(counter);
						if (!staticBar) {
							player.setExp(Math.max(0, player.getExp() - expPerTick));
						}
					});
				}
				tickCounter++;

				if (tickCounter % 2 == 0) {
					counter--;
					tickCounter = 0;
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 10L);

		timerTasks.put(player, task);
	}

}
