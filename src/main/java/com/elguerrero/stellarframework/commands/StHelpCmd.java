package com.elguerrero.stellarframework.commands;

import com.elguerrero.stellarframework.StellarPlugin;
import com.elguerrero.stellarframework.config.StellarMessages;
import com.elguerrero.stellarframework.systems.CacheManagers;
import com.elguerrero.stellarframework.utils.StErrorLogUtils;
import com.elguerrero.stellarframework.utils.StMessageUtils;
import com.elguerrero.stellarframework.utils.StMixUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

import java.util.List;


public final class StHelpCmd {

	private StHelpCmd() {
	}

	private static int maxPages = 0;

	private static int maxLinesPerPage = 15;


	// Never must hexed more than 5 lines but I need to check exactly how much lines

	public static void registerInfoCmd() {
		try {
			CacheManagers.addAddonCommand(new CommandAPICommand("help")
					.withRequirement(sender -> StMixUtils.checkCommandRequirement(sender, "help"))
					.withHelp("Show the plugin commands", "")
					.withArguments(new IntegerArgument("page", 1, maxPages))
					.executesPlayer((player, args) -> {

						int argumentPage = 0;

						if (args.get(0) != null){
							argumentPage = (Integer) args.get(0);
						}

						if (argumentPage > 0 && argumentPage <= maxPages){
							sendHelpPageMessages(player, argumentPage);
						} else {
							sendHelpPageMessages(player, 1);
						}
					}));

		} catch (Exception ex) {
			StErrorLogUtils.logErrorException(ex, "default");
		}
	}



	private static void sendHelpPageMessages(Player player, int page){

		final int indexPage = page - 1;

		StellarPlugin.getBasicMessagesInstance().getHelpPages().get(indexPage).forEach(message -> {
			StMessageUtils.sendMessagePlayer(player, message);
		});
		sendHelpPageArrowsMessage(page);

	}

	private static Component sendHelpPageArrowsMessage(int page){

		final StellarMessages messagesInstance = StellarPlugin.getBasicMessagesInstance();

		final TextComponent helpPreviousPageArrow = Component.text()
				.content(StellarPlugin.getBasicMessagesInstance().getHelpPreviousPageArrow())
				.hoverEvent(HoverEvent.showText(Component.text(replacePageNumberPlaceholder(messagesInstance.getPreviousPageArrowHover(), page - 1))))
				.hoverEvent(HoverEvent.hoverEvent(ClickEvent.Action.RUN_COMMAND, net.kyori.adventure.text.event.ClickEvent.runCommand("f")))
				.build();

		return helpPageArrows;
	}

	// TODO: Me equivoque y el envio de mensajes incluyendo las flechas, deben ser varios metodos tipo cadena
	// TODO: Osea cada metodo debe enviar una pagina y sus flechas correspondientes, y cada el comando solo llama al primer metodo

	private static String replacePlaceholders(String message) {

		return message.replace("%max_pages%", String.valueOf(maxPages))
				.replace("%page_first_line%", StellarPlugin.getBasicMessagesInstance().getHelpPageFirstLine())
				.replace("%page_last_line%", StellarPlugin.getBasicMessagesInstance().getHelpPageLastLine())
				.replace("%previous_page_arrow%", StellarPlugin.getBasicMessagesInstance().getHelpPreviousPageArrow())
				.replace("%next_page_arrow%", StellarPlugin.getBasicMessagesInstance().getHelpNextPageArrow());
	}

	private static String replacePageNumberPlaceholder(String message, int page) {
		return message.replace("%page%", String.valueOf(page));
	}

	public static void addHelpPage(String configListPath) {

		try {

			final StellarMessages stellarMessageInstance = StellarPlugin.getBasicMessagesInstance();
			final List<String> newHelpPageList = stellarMessageInstance.getMessagesFile().getStringList(configListPath);

			if (newHelpPageList.size() <= maxLinesPerPage) {

				stellarMessageInstance.getHelpPages().add(newHelpPageList);
				maxPages++;

			} else {

				throw new IndexOutOfBoundsException();
			}

		} catch (IndexOutOfBoundsException ex) {
			StErrorLogUtils.logErrorException(ex, "The help page number " + maxPages++ + " has more than " + maxLinesPerPage + " lines in the lang file.");
		}

	}

}
