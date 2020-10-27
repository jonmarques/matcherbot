package br.com.sauran.matcher.listener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.entities.Match;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.utils.BotUtils;
import br.com.sauran.matcher.utils.Embeds;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReaction extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getUser().isBot()) return;
		if (!e.isFromGuild()) return;

		if (!BotUtils.hasPermissions(e.getGuild())) return;

		e.getChannel().retrieveMessageById(e.getMessageId()).queue(t -> {

			if (!t.getAuthor().getId().equals(br.com.sauran.matcher.Matcher.getConfig().getBotId())) return;

			MessageEmbed embed = t.getEmbeds().get(0);

			if (embed == null || embed.getTitle() == null || !(embed.getTitle().startsWith("Lista de partidas profissionais") || embed.getTitle().startsWith("List of professional matches"))) return;

			Calendar dateNow = Calendar.getInstance();
			dateNow.setTimeInMillis(System.currentTimeMillis());

			String[] split = embed.getFooter().getText().split(" - ");

			Game game = Game.getByLongName(split[0]);
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(split[1]);
			m.find();
			Integer page = Integer.valueOf(m.group());

			int maxpage = (int) Math.ceil(Match.getMatches(game).size() / 10) == 0 ? 1 : (int) Math.ceil(Match.getMatches(game).size() / 10);

			GuildInfo gi = GuildInfo.getGuildInfo(e.getGuild());

			if (e.getReactionEmote().isEmoji()) {
				if (e.getReactionEmote().getEmoji().equals("⬅")) {

					int newpage = page - 1 > 0 ? page - 1 : 1;

					t.removeReaction("⬅", e.getUser()).queue();
					if (page > 1) {
						t.editMessage(Embeds.sendEmbed(gi, e.getChannel(),  gi.getLang(), game, newpage, null, null).getEb().build()).queue();
					}

				}

				else if (e.getReactionEmote().getEmoji().equals("🔄")) {

					int newpage = maxpage >= page ? page : 0;

					t.removeReaction("🔄", e.getUser()).queue();

					t.editMessage(Embeds.sendEmbed(gi, e.getChannel(), gi.getLang(), game, newpage, null, null).getEb().build()).queue();

				}
				else if (e.getReactionEmote().getEmoji().equals("➡")) {

					int newpage = maxpage >= page + 1 ? page + 1 : 0;

					t.removeReaction("➡", e.getUser()).queue();

					if (page < Match.getMatches(game).size() / 10) {
						t.editMessage(Embeds.sendEmbed(gi, e.getChannel(), gi.getLang(), game, newpage, null, null).getEb().build()).queue();
					}
				} 
			} else if (e.getReactionEmote().isEmote()) {

				Emote emote = e.getReactionEmote().getEmote();
				String reaction = ":" + emote.getName() + ":" + emote.getId();

				for (Game s : Game.values()) {
					if (reaction.equalsIgnoreCase(s.getReaction())) {
						t.removeReaction(reaction).queue();
						t.removeReaction(reaction, e.getUser()).queue();
						t.addReaction(game.getReaction()).queue();
						t.editMessage(Embeds.sendEmbed(gi, e.getChannel(), gi.getLang(), Game.getByReaction(reaction), 0, null, null).getEb().build()).queue();
						break;
					}
				}
			}
		}, t -> {
			return;
		});
	}
}
