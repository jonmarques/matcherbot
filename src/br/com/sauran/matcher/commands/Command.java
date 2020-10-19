package br.com.sauran.matcher.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {
	
	public Command() {}
	
	public abstract void fireCommand(MessageReceivedEvent e, String... args);
	
}
