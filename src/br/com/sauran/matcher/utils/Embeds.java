package br.com.sauran.matcher.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.entities.Match;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.entities.enums.MatchStatus;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.EmbedBuilder;

public class Embeds {

	public static EmbedBuilder sendEmbed(GuildInfo gi, Language lang, @Nonnull Game game, int page) {

		List<Match> matches = new ArrayList<Match>();

		HashSet<Match> matchesSet = new HashSet<Match>(Match.getMatches(game));
		for (Match match : matchesSet) {
			matches.add(match);
		}

		Collections.sort(matches, new Comparator<Match>() {
			public int compare(Match o1, Match o2) {
				if (o1.getBegin_at() > o2.getBegin_at()) {
					return 1;
				}
				else if (o1.getBegin_at() <  o2.getBegin_at()) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});				


		EmbedBuilder embed = new EmbedBuilder();

		long now = System.currentTimeMillis();

		if (page == 0) {
			int paginadefault = 1;

			int pageverify = 1;
			theloop: while (true) {

				int maxverify = matches.size() > 9 + (10 * (pageverify - 1)) ? 9  + (10 * (pageverify - 1)) : matches.size();

				if (10 * (pageverify - 1) > matches.size()) break;

				List<Match> mverify = matches.subList(10 * (pageverify - 1), maxverify);
				for (Match match : mverify) {
					if (match.getStatus() == MatchStatus.INGAME || now < match.getBegin_at()) {
						paginadefault = pageverify;
						break theloop;
					}
				}

				if (maxverify == matches.size()) break;

				pageverify++;
			}
			page = paginadefault;
		}


		int maxpage = (int) Math.ceil(Match.getMatches(game).size() / 10) == 0 ? 1 : (int) Math.ceil(Match.getMatches(game).size() / 10);
		embed.setTitle(lang.reaction_description);
		String s = gi.getTm() > 0 ? "+" : "";
		embed.setFooter(lang.matches_embed_footer.replace("{TIMEZONE}", s + String.valueOf(gi.getTm())).replace("{GAME}", game.getName()).replace("{PAGE}", String.valueOf(page)).replace("{MAXPAGE}", String.valueOf(maxpage)));

		embed.setColor(game.getColor());
		embed.setThumbnail(game.getLogo());

		if (matches == null || matches.isEmpty()) {
			embed.setDescription(lang.matches_embed_nogames);
			return embed;
		}
		embed.setDescription(lang.matches_embed_legend);

		int initial = 0 + (10 * (page - 1));
		int max = matches.size() < 10 * page ? matches.size() : 10 * page;

		String lastday = "";

		List<Match> matcheshash = matches;
		for (int i = initial; i < max; i++) {

			Match match = matcheshash.get(i);

			Calendar dateNow = Calendar.getInstance();
			dateNow.setTimeInMillis(match.getBegin_at());
			
			SimpleDateFormat format2 = new SimpleDateFormat("dd';'MM';'yyyy");
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			SimpleDateFormat formatdate = null;
			
			if (LangManager.getLang("pt-br") == gi.getLang()) {
				formatdate = new SimpleDateFormat("EEEEE, d 'de' MMMMM 'de' yyyy",  new Locale("pt", "BR"));
			} else {
				formatdate = new SimpleDateFormat("EEEEE, MMMMM d, yyyy",  new Locale("en", "US"));
			}
			
			format2.setTimeZone(gi.getTimeZone());
			format.setTimeZone(gi.getTimeZone());
			formatdate.setTimeZone(gi.getTimeZone());

			String date = format2.format(dateNow.getTime());
			if (!lastday.equalsIgnoreCase(date)) {
				embed.addField(" ", ":calendar_spiral: " + formatdate.format(dateNow.getTime()), false);
				lastday = date;
			}
			
			String emote = match.getStatus().getEmote();
			String hour;
			if (match.getStatus() == MatchStatus.INGAME) {
				hour = lang.matches_live;
			} else {
				hour = format.format(dateNow.getTime());
			}

			String tournament = match.getLeagueName() + " " + match.getSerieName();
			if (tournament.length() > 41) tournament = tournament.substring(0, 41);
			String numbergames = lang.match_bo + match.getNumberOfGames();
			String flag1 = match.getLocation1() == null ? ":pirate_flag:" : ":flag_" + match.getLocation1() + ":";
			String flag2 = match.getLocation2() == null ? ":pirate_flag:" : ":flag_" + match.getLocation2() + ":";
			String name1 = match.getName1().length() > 19 ? match.getName1().substring(0, 19) : match.getName1();
			String name2 = match.getName2().length() > 19 ? match.getName2().substring(0, 19) : match.getName2();
			String points1 = gi.isSpoiler() ? "||` " + String.valueOf(match.getPoints1()) + " `||" :  String.valueOf(match.getPoints1()), points2 = gi.isSpoiler() ? "||` " + String.valueOf(match.getPoints2()) + " `||" : String.valueOf(match.getPoints2()), liveurl = "";

			if (match.getLiveUrl() != null && !match.getLiveUrl().equals("")) {
				liveurl = " | [" + lang.matches_watch + "](" + match.getLiveUrl() + ")";
			}

			if (match.getStatus() == MatchStatus.INGAME) {
				embed.addField(emote + " | " + hour + " | " + numbergames + " | " + tournament, emote + " | " + flag1 + " ` " + name1 + " ` **" + points1 + " x " + points2 + "** ` " + name2 + " ` " + flag2 + liveurl, false);
			} else if (match.getStatus() == MatchStatus.SOON) {
				embed.addField(emote + " | " + hour + " | " + numbergames + " | " + tournament, emote + " | " + flag1 + " ` " + name1 + " ` **x** ` " + name2 + " ` " + flag2 + liveurl, false);
			} else if (match.getStatus() == MatchStatus.CANCELED) {
				embed.addField(emote + " | " + hour + " | " + numbergames + " | " + tournament, emote + " | " + flag1 + " ` " + name1 + " ` **x** ` " + name2 + " ` " + flag2, false);
			} else if (match.getStatus() == MatchStatus.FINISHED) {
				embed.addField(emote + " | " + hour + " | " + numbergames + " | " + tournament, emote + " | " + flag1 + " ` " + name1 + " ` **" + points1 + " x " + points2 + "** ` " + name2 + " ` " + flag2, false);
			}

		}

		return embed;
	}

}
