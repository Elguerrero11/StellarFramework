package com.elguerrero.stellarframework.utils;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class StBossBarUtils {

	private StBossBarUtils() {
	}

	public static BossBar createEmptyBossBar() {

		final Component name = Component.text("");

		return BossBar.bossBar(name, 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
	}

	public static void showBossBarToPlayer(Player player, String bossbarText, float progress, BossBar.Color color, BossBar.Overlay overlay) {

		final Component name = Component.text(bossbarText);
		BossBar bossBar = BossBar.bossBar(name, progress, color, overlay);

		StMessageUtils.getAudiencePlayer(player).showBossBar(bossBar);
	}

	public static void hideBossBarToPlayer(Player player, String bossbarText, float progress, BossBar.Color color, BossBar.Overlay overlay) {

		final Component name = Component.text(bossbarText);
		BossBar bossBar = BossBar.bossBar(name, progress, color, overlay);

		StMessageUtils.getAudiencePlayer(player).hideBossBar(bossBar);
	}

}
