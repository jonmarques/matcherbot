package br.com.sauran.matcher.commands.esports;

import java.util.concurrent.TimeUnit;

import br.com.sauran.matcher.commands.Command;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.lang.Language;
import br.com.sauran.matcher.utils.BotUtils;
import br.com.sauran.matcher.utils.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Matches extends Command {

	public Matches() {
		super();
	}

	@Override
	public void fireCommand(MessageReceivedEvent e, String... args) {

		e.getTextChannel().sendTyping().queue();

		Language lang = GuildInfo.getGuilds().get(e.getGuild()).getLang();

		if (args.length == 1 && (Game.getByName(args[0]) != null)) {

			Game game = Game.getByName(args[0]);


			EmbedBuilder embed = Embeds.sendEmbed(GuildInfo.getGuildInfo(e.getGuild()), lang, game, 0);

			e.getChannel().sendMessage(embed.build()).queue(t -> {
				if (BotUtils.hasPermissions(e.getGuild())) {
					if (BotUtils.hasPermissions(e.getGuild(), e.getTextChannel())) {
						t.addReaction("⬅").queue();
						t.addReaction("🔄").queue();
						t.addReaction("➡").queue();
						for (Game g : Game.values()) {
							if (game == g) continue;
							t.addReaction(g.getReaction()).queue();
						}

					}
				} else {
					e.getChannel().sendMessage(lang.more_permissions.replace("{INVITE}", "<https://cutt.ly/zybNwPh>")).queue(t2 -> t2.delete().queueAfter(15, TimeUnit.SECONDS));
				}
			});

			return;
		}

		e.getChannel().sendMessage(lang.matches_usage).queue();		
	}

}
