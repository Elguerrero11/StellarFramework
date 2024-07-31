package com.elguerrero.stellarframework.utils;

import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.config.StellarConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StMessageUtils {

	private StMessageUtils() {
	}

	private static final MiniMessage miniMessage = MiniMessage.miniMessage();

	private static final Map<String, String> legacyColors1 = new HashMap<>();

	static {
		legacyColors1.put("&0", "black");
		legacyColors1.put("&1", "dark_blue");
		legacyColors1.put("&2", "dark_green");
		legacyColors1.put("&3", "dark_aqua");
		legacyColors1.put("&4", "dark_red");
		legacyColors1.put("&5", "dark_purple");
		legacyColors1.put("&6", "gold");
		legacyColors1.put("&7", "gray");
		legacyColors1.put("&8", "dark_gray");
		legacyColors1.put("&9", "blue");
		legacyColors1.put("&a", "green");
		legacyColors1.put("&b", "aqua");
		legacyColors1.put("&c", "red");
		legacyColors1.put("&d", "light_purple");
		legacyColors1.put("&e", "yellow");
		legacyColors1.put("&f", "white");
		legacyColors1.put("&k", "obfuscated");
		legacyColors1.put("&l", "bold");
		legacyColors1.put("&m", "strikethrough");
		legacyColors1.put("&n", "underlined");
		legacyColors1.put("&o", "italic");
		legacyColors1.put("&r", "reset");
	}


	/**
	 * Get a component for send in a message with the colors and placeholders.
	 * TODO: Add placeholderapi placeholders support
	 *
	 * @param message - The message to colorize
	 * @return String - The message colorized
	 */
	private static Component colorizeAndFormat(String message) {

		try {

			String messageFormated = message;

			messageFormated = replacePlaceholders(messageFormated);
			messageFormated = replaceLegacyColorFormat(messageFormated);
			messageFormated = replaceColorsGradientFormat(messageFormated);
			messageFormated = replaceColorsTransitionFormat(messageFormated);
			messageFormated = replaceHexColorFormat(messageFormated);
			messageFormated = replaceHoverShowTextFormat(messageFormated);

			final Component messageComponent = miniMessage.deserialize(messageFormated);

			return processClickEvents(messageComponent);

		} catch (Exception ex) {

			StErrorLogUtils.logErrorException(ex, "Error while trying to colorize and format the message: " + message);
			return null;
		}
	}

	private static String replacePlaceholders(String message) {


		return message.replace("%plugin_prefix%", StellarPlugin.getBasicMessagesInstance().getPluginPrefix())
				.replace("%plugin_prefix_debug%", StellarPlugin.getBasicMessagesInstance().getPluginDebugPrefix());

	}


	private static String replaceLegacyColorFormat(String message) {

		final Map<Character, String> legacyColors2 = new HashMap<>();

		legacyColors2.put('0', "<black>");
		legacyColors2.put('1', "<dark_blue>");
		legacyColors2.put('2', "<dark_green>");
		legacyColors2.put('3', "<dark_aqua>");
		legacyColors2.put('4', "<dark_red>");
		legacyColors2.put('5', "<dark_purple>");
		legacyColors2.put('6', "<gold>");
		legacyColors2.put('7', "<gray>");
		legacyColors2.put('8', "<dark_gray>");
		legacyColors2.put('9', "<blue>");
		legacyColors2.put('a', "<green>");
		legacyColors2.put('b', "<aqua>");
		legacyColors2.put('c', "<red>");
		legacyColors2.put('d', "<light_purple>");
		legacyColors2.put('e', "<yellow>");
		legacyColors2.put('f', "<white>");

		legacyColors2.put('k', "<obfuscated>");
		legacyColors2.put('l', "<bold>");
		legacyColors2.put('m', "<strikethrough>");
		legacyColors2.put('n', "<underlined>");
		legacyColors2.put('o', "<italic>");
		legacyColors2.put('r', "<reset>");


		for (Map.Entry<Character, String> entry : legacyColors2.entrySet()) {
			String key = "&" + entry.getKey();
			if (message.contains(key)) {
				message = message.replace(key, entry.getValue());
			}
		}

		return message;
	}

	private static String replaceHexColorFormat(String message) {
		return message.replaceAll("&#([0-9a-fA-F]{6})", "<#$1>");
	}

	private static String replaceColorsGradientFormat(String message) {

		Pattern pattern = Pattern.compile("<gradient:([^>]+)>");
		Matcher matcher = pattern.matcher(message);

		StringBuilder buffer = new StringBuilder();

		while (matcher.find()) {
			String gradient = matcher.group(1);
			gradient = gradient.replaceAll("&#([A-Fa-f0-9]{6})", "#$1");
			for (Map.Entry<String, String> entry : legacyColors1.entrySet()) {
				gradient = gradient.replace(entry.getKey(), entry.getValue());
			}
			matcher.appendReplacement(buffer, "<gradient:" + gradient + ">");
		}

		matcher.appendTail(buffer);

		return buffer.toString();
	}

	private static String replaceColorsTransitionFormat(String message) {

		Pattern pattern = Pattern.compile("<transition:([^>]+)>");
		Matcher matcher = pattern.matcher(message);

		StringBuilder buffer = new StringBuilder();

		while (matcher.find()) {
			String transition = matcher.group(1);
			transition = transition.replaceAll("&#([A-Fa-f0-9]{6})", "#$1");
			for (Map.Entry<String, String> entry : legacyColors1.entrySet()) {
				transition = transition.replace(entry.getKey(), entry.getValue());
			}
			matcher.appendReplacement(buffer, "<transition:" + transition + ">");
		}

		matcher.appendTail(buffer);

		return buffer.toString();
	}


	private static String replaceHoverShowTextFormat(String message) {

		Pattern pattern = Pattern.compile("\\[hover:show_text:(.*?)]");
		Matcher matcher = pattern.matcher(message);
		StringBuilder buffer = new StringBuilder();

		while (matcher.find()) {
			String replacement = "<hover:show_text:'" + matcher.group(1) + "'>";
			matcher.appendReplacement(buffer, replacement);
		}

		matcher.appendTail(buffer);

		return buffer.toString();
	}

	private static Component processClickEvents(Component component) {

		final Pattern clickEventPattern = Pattern.compile("\\[click:(open_url|run_command|suggest|copy_to_clipboard):([^]]+)]");

		TextComponent.Builder newComponentBuilder = Component.text();

		final String message = getComponentText(component);
		final Matcher matcher = clickEventPattern.matcher(message);

		int lastEnd = 0;
		while (matcher.find()) {
			if (matcher.start() != lastEnd) {
				newComponentBuilder.append(Component.text(message.substring(lastEnd, matcher.start())));
			}

			final String action = matcher.group(1);
			final String value = matcher.group(2);

			ClickEvent event;
			switch (action) {
				case "open_url" -> event = ClickEvent.openUrl(value);
				case "run_command" -> event = ClickEvent.runCommand(value);
				case "suggest" -> event = ClickEvent.suggestCommand(value);
				case "copy_to_clipboard" -> event = ClickEvent.copyToClipboard(value);
				default -> {
					newComponentBuilder.append(Component.empty());
					lastEnd = matcher.end();
					continue;
				}
			}

			newComponentBuilder.append(Component.text(value).clickEvent(event));
			lastEnd = matcher.end();
		}

		if (lastEnd != message.length()) {
			newComponentBuilder.append(Component.text(message.substring(lastEnd)));
		}

		return newComponentBuilder.build();
	}

	private static String getComponentText(Component component) {

		if (component instanceof TextComponent textComponent) {
			return textComponent.content();
		} else {
			StringBuilder sb = new StringBuilder();
			for (Component child : component.children()) {
				sb.append(getComponentText(child));
			}
			return sb.toString();
		}
	}

	// Methods for sending messages to the players


	/**
	 * Send a message to the player Use colorize() method to colorize the message
	 *
	 * @param player  - The player to send the message
	 * @param message - The message to send to the player
	 */
	public static void sendMessagePlayer(Player player, String message) {

		getAudiencePlayer(player).sendMessage(Objects.requireNonNull(colorizeAndFormat(message)));

	}

	public static void sendMessageToAllPlayers(String message) {

		getAudienceAllPlayers().sendMessage(Objects.requireNonNull(colorizeAndFormat(message)));
	}

	public static void sendMessageToAllPlayersInWorld(String worldName, String message) {

		Objects.requireNonNull(getAudienceWorld(worldName)).sendMessage(Objects.requireNonNull(colorizeAndFormat(message)));
	}

	public static void sendMessageToPlayersWithPermission(String message, String requiredPermission) {

		getAudienceRequiredPermission(requiredPermission).sendMessage(Objects.requireNonNull(colorizeAndFormat(message)));
	}

	public static void sendMessageToPlayersWithFilter(String message, Predicate<CommandSender> filter) {

		getAudienceFiltered(filter).sendMessage(Objects.requireNonNull(colorizeAndFormat(message)));
	}

	/**
	 * Send an info message to the console with gray color Use colorize() method to colorize the message
	 *
	 * @param message - The message to send to the console
	 */
	public static void sendConsoleInfoMessage(String message) {
		StellarPlugin.getPluginInstance().getConsoleLogger().info(replacePlaceholders(message)); // NOSONAR
	}

	/**
	 * Send a warning message to the console with yellow color Use colorize() method to colorize the message
	 *
	 * @param message - The message to send to the console
	 */
	public static void sendConsoleWarnMessage(String message) {
		StellarPlugin.getPluginInstance().getConsoleLogger().warn(replacePlaceholders(message)); // NOSONAR
	}

	/**
	 * Send a severe message to the console with red color Use colorize() method to colorize the message
	 *
	 * @param message - The message to send to the console
	 */
	public static void sendConsoleErrorMessage(String message) {
		StellarPlugin.getPluginInstance().getConsoleLogger().error(replacePlaceholders(message)); // NOSONAR
	}

	/**
	 * Send the error message to the console when a SMALL error ocurred with the plugin
	 *
	 * @param message - The message to send to the console
	 */
	public static void sendErrorMessageConsole(String message) {
		sendConsoleErrorMessage(message);
	}

	/**
	 * Send a DEBUG message to the console with the DEBUG prefix
	 *
	 * @param message - The message to send to the console
	 */
	public static void sendDebugMessage(String message) {
		if (StellarConfig.getDebug()) { // NOSONAR
			StellarPlugin.getPluginInstance().getConsoleLogger().debug(replacePlaceholders(message)); //  NOSONAR
			StMixUtils.logDebugMessage(message);
		}
	}

	public static void sendMessageDebugStatus() {

		if (StellarConfig.getDebug()) { // NOSONAR
			sendConsoleInfoMessage("&ei &7Debug mode is enabled V");
		} else {
			sendConsoleInfoMessage("&ei &7Debug mode is disabled X");
		}
	}

	// Methods related to audience for send messages and other stuff to players

	public static Audience getAudiencePlayer(Player player) {
		return StellarPlugin.getPluginInstance().getAdventureAudience().player(player);
	}

	public static Audience getAudienceAllPlayers() {

		return StellarPlugin.getPluginInstance().getAdventureAudience().players();
	}

	public static Audience getAudienceRequiredPermission(String permission) {

		return StellarPlugin.getPluginInstance().getAdventureAudience().permission(permission);
	}

	@SuppressWarnings("all")
	public static Audience getAudienceWorld(String worldName) {

		World world = Bukkit.getServer().getWorld(worldName);

		if (world != null) {
			Key worldKey = Key.key(world.getName());
			return StellarPlugin.getPluginInstance().getAdventureAudience().world(worldKey);
		} else {

			StErrorLogUtils.logErrorException(new NullPointerException(), "Error while trying to get the world audience, the world may not exist or be loaded.");
			return null;
		}
	}

	public static Audience getAudienceFiltered(Predicate<CommandSender> predicateFilter) {

		return StellarPlugin.getPluginInstance().getAdventureAudience().filter(predicateFilter);
	}

	// ActionBar related methods

	public static void sendActionBar(Player player, String message) {

		getAudiencePlayer(player).sendActionBar(Objects.requireNonNull(colorizeAndFormat(message)));

	}

	public static void clearActionBar(Player player) {

		getAudiencePlayer(player).sendActionBar(Component.empty());

	}

}
