package br.com.sauran.matcher.commands;

import java.util.HashMap;

import br.com.sauran.matcher.Matcher;
import br.com.sauran.matcher.commands.esports.Matches;
import br.com.sauran.matcher.commands.info.Help;
import br.com.sauran.matcher.commands.info.Info;
import br.com.sauran.matcher.commands.notify.Notify;
import br.com.sauran.matcher.commands.setup.Lang;
import br.com.sauran.matcher.commands.setup.Spoiler;
import br.com.sauran.matcher.commands.setup.TimeZone;
import br.com.sauran.matcher.data.Config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

	private Config config;
	private HashMap<String, Command> commands;

	public CommandManager() {
		this.config = Matcher.getConfig();
		this.commands = new HashMap<String, Command>();

		addCommand(new Help(), "help", "ajuda");
		addCommand(new Info(), "info");
		addCommand(new Notify(), "notify", "notificar");
		addCommand(new Matches(), "csgo", "lol", "dota2", "rl", "ow", "codmw", "pubg", "r6", "fifa");
		addCommand(new Lang(), "lang", "language", "idioma");
		addCommand(new TimeZone(), "tm", "timezone", "horario");
		addCommand(new Spoiler(), "spoiler");

	}
	
	public void addCommand(Command command, String... args) {
		for (String arg : args) {
			commands.put(arg, command);
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getAuthor().isBot()) return;
		if (!e.isFromGuild()) return;

		String[] content = e.getMessage().getContentRaw().split(" ");
		if (content.length == 0) return;

		String prefix = config.getPrefix();
		
		if (!content[0].startsWith(prefix)) return;
		Command command = commands.get(content[0].replace(prefix, ""));
		if (command == null) return;

		content[0] = content[0].replace(prefix, "");
		
		command.fireCommand(e, content);
	}

}
