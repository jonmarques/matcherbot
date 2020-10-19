package br.com.sauran.matcher.listener;

import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoin extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Language lang = e.getGuild().getRegion() == Region.BRAZIL || e.getGuild().getRegion() == Region.VIP_BRAZIL ? LangManager.getLang("pt-br") : LangManager.getLang("en-us");
		new GuildInfo(e.getGuild(), null, null, null, lang, 0, false);
		
		Guild guild = e.getGuild();
		TextChannel a = guild.getDefaultChannel();
		
		a.sendMessage(lang.join_message).queue();
	}
	
}
