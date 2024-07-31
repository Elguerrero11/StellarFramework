package com.elguerrero.stellarframework.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Predicate;

public final class StSoundUtils {

	private StSoundUtils() {
	}

	/**
	 * Play a sound to a player
	 * Note: Due to MC-138832, the volume and pitch may be ignored when using soundFollowToPlayer
	 */
	public static void playSoundToPlayer(Player player, String soundID, Sound.Source source, float volume, float pitch, boolean soundFollowToPlayer) {

		Sound sound = Sound.sound(Key.key(soundID), source != null ? source : Sound.Source.MASTER, volume, pitch);
		final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);

		if (soundFollowToPlayer)
			playerAudience.playSound(sound, Sound.Emitter.self());
		else
			playerAudience.playSound(sound);

	}

	/**
	 * Play a sound to a collection of players
	 * Note: Due to MC-138832, the volume and pitch may be ignored when using soundFollowToPlayers.
	 */
	public static void playSoundToPlayersList(Collection<Player> players, String soundID, Sound.Source source, float volume, float pitch, boolean soundFollowToPlayers) {

		Sound sound = Sound.sound(Key.key(soundID), source != null ? source : Sound.Source.MASTER, volume, pitch);

		for (Player player : players) {
			final Audience playerAudience = StMessageUtils.getAudiencePlayer(player);
			if (soundFollowToPlayers) {
				playerAudience.playSound(sound, Sound.Emitter.self());
			} else {
				playerAudience.playSound(sound);
			}
		}
	}

	/**
	 * Play a sound to the players that pass the filter
	 * Note: Due to MC-138832, the volume and pitch may be ignored when using soundFollowToPlayers.
	 */
	public static void playSoundToPlayersFiltered(String soundID, Sound.Source source, float volume, float pitch, boolean soundFollowToPlayers, Predicate<CommandSender> predicateFilter) {

		Sound sound = Sound.sound(Key.key(soundID), source != null ? source : Sound.Source.MASTER, volume, pitch);

		final Audience filteredAudience = StMessageUtils.getAudienceFiltered(predicateFilter);

		if (soundFollowToPlayers)
			filteredAudience.playSound(sound, Sound.Emitter.self());
		else
			filteredAudience.playSound(sound);
	}

	public static void playSoundAtLocation(Player player, String soundID, Sound.Source source, float volume, float pitch, double x, double y, double z) {

		Sound sound = Sound.sound(Key.key(soundID), source != null ? source : Sound.Source.MASTER, volume, pitch);

		StMessageUtils.getAudiencePlayer(player).playSound(sound, x, y, z);
	}

	public static void playSoundAtLocation(Player player, String soundID, Sound.Source source, float volume, float pitch, Location location) {

		Sound sound = Sound.sound(Key.key(soundID), source != null ? source : Sound.Source.MASTER, volume, pitch);
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		StMessageUtils.getAudiencePlayer(player).playSound(sound, x, y, z);
	}

	public static void stopSound(Player player, String soundID, Sound.Source source, float volume, float pitch) {

		Sound sound = Sound.sound(Key.key(soundID), source != null ? source : Sound.Source.MASTER, volume, pitch);

		StMessageUtils.getAudiencePlayer(player).stopSound(sound);
	}

}
