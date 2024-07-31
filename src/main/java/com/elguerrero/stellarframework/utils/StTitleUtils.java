package com.elguerrero.stellarframework.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class StTitleUtils {

	private StTitleUtils() {
	}

	/**
	 * Send a title with subtitle to a player with default times.
	 */
	public static void playerSendFullTitle(Player player, String mainTitleParam, String subtitleParam) {

		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);
		final Component mainTitle = Component.text(mainTitleParam);
		final Component subtitle = Component.text(subtitleParam);

		final Title title = Title.title(mainTitle, subtitle);
		playerAudience.showTitle(title);
	}

	/**
	 * Send a title without subtitle to a player with default times.
	 */
	public static void playerSendTitle(Player player, String mainTitleParam) {

		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);
		final Component mainTitle = Component.text(mainTitleParam);
		final Component subtitle = Component.empty();

		final Title title = Title.title(mainTitle, subtitle);
		playerAudience.showTitle(title);
	}

	/**
	 * Send a subtitle without title to a player with default times.
	 */
	public static void playerSendSubtitle(Player player, String subtitleParam) {

		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);
		final Component mainTitle = Component.empty();
		final Component subtitle = Component.text(subtitleParam);

		final Title title = Title.title(mainTitle, subtitle);
		playerAudience.showTitle(title);
	}

	/**
	 * Send a title with subtitle to a player with the specified time.
	 */
	public static void playerSendTempFullTitle(Player player, String mainTitleParam, String subtitleParam, int fadeInSeconds, int stayOnScreenSeconds, int fadeOutSeconds) {

		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);
		final Component mainTitle = Component.text(mainTitleParam);
		final Component subtitle = Component.text(subtitleParam);

		final long fadeInMilliseconds = fadeInSeconds/1000;
		final long stayOnScreenMilliseconds = stayOnScreenSeconds/1000;
		final long fadeOutMilliseconds = fadeOutSeconds/1000;

		final Title.Times times = Title.Times.times(Duration.ofMillis(fadeInMilliseconds), Duration.ofMillis(stayOnScreenMilliseconds), Duration.ofMillis(fadeOutMilliseconds));
		final Title title = Title.title(mainTitle, subtitle, times);

		playerAudience.showTitle(title);
	}

	/**
	 * Send a title without subtitle to a player with the specified time.
	 */
	public static void playerSendTempTitle(Player player, String mainTitleParam, int fadeInSeconds, int stayOnScreenSeconds, int fadeOutSeconds) {

		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);
		final Component mainTitle = Component.text(mainTitleParam);
		final Component subtitle = Component.empty();

		final long fadeInMilliseconds = fadeInSeconds/1000;
		final long stayOnScreenMilliseconds = stayOnScreenSeconds/1000;
		final long fadeOutMilliseconds = fadeOutSeconds/1000;

		final Title.Times times = Title.Times.times(Duration.ofMillis(fadeInMilliseconds), Duration.ofMillis(stayOnScreenMilliseconds), Duration.ofMillis(fadeOutMilliseconds));
		final Title title = Title.title(mainTitle, subtitle, times);

		playerAudience.showTitle(title);
	}

	/**
	 * Send a subtitle without title to a player with the specified time.
	 */
	public static void playerSendTempSubtitle(Player player, String subtitleParam, int fadeInSeconds, int stayOnScreenSeconds, int fadeOutSeconds) {

		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);

		final Component mainTitle = Component.empty();
		final Component subtitle = Component.text(subtitleParam);

		final long fadeInMilliseconds = fadeInSeconds/1000;
		final long stayOnScreenMilliseconds = stayOnScreenSeconds/1000;
		final long fadeOutMilliseconds = fadeOutSeconds/1000;

		final Title.Times times = Title.Times.times(Duration.ofMillis(fadeInMilliseconds), Duration.ofMillis(stayOnScreenMilliseconds), Duration.ofMillis(fadeOutMilliseconds));

		final Title title = Title.title(mainTitle, subtitle, times);

		playerAudience.showTitle(title);
	}

	public static void playerClearTitle(Player player) {

		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);
		playerAudience.clearTitle();
	}

}
