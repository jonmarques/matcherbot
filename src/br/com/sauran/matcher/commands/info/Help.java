package br.com.sauran.matcher.commands.info;

import java.awt.Color;

import br.com.sauran.matcher.commands.Command;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.lang.Field;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help extends Command {

	public Help() {
		super();
	}

	@Override
	public void fireCommand(MessageReceivedEvent e, String... args) {

		e.getTextChannel().sendTyping().queue();

		EmbedBuilder eb = new EmbedBuilder();

		Language lang = GuildInfo.getGuilds().get(e.getGuild()).getLang();

		eb.setAuthor(lang.help_embed_author, null, "https://imgur.com/LYje1SH.png");
		eb.setThumbnail("https://imgur.com/xo5U3p2.png");
		eb.setColor(Color.decode("#db9a27"));

		eb.setDescription(lang.help_embed_description);

		for (Field field : lang.help_embed_fields) {
			eb.addField(field.getTitle(), field.getDescription().replace("{INVITE}", "https://discord.com/oauth2/authorize?client_id=694992068589912084&permissions=355392&scope=bot").replace("{VOTE}", "https://top.gg/bot/694992068589912084/vote").replace("{DISCORD}", "https://discord.gg/cuM6mvC").replace("{DONATE}", "https://donatebot.io/checkout/701168663629267005"), field.isInline());
		}
		
		e.getChannel().sendMessage(eb.build()).queue();		
	}

}
