package br.com.sauran.matcher.listener;

import br.com.sauran.matcher.entities.GuildInfo;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeave extends ListenerAdapter {

	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		GuildInfo.removeGuild(e.getGuild());
	}
	
}
