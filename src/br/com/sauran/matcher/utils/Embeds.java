package br.com.sauran.matcher.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.entities.Match;
import br.com.sauran.matcher.entities.Player;
import br.com.sauran.matcher.entities.Team;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.entities.enums.MatchStatus;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Embeds {

	public static EmbedTeam sendEmbed(GuildInfo gi, MessageChannel mc, Language lang, @Nonnull Game game, int page, String team, String league) {

		List<Match> matches = new ArrayList<Match>();

		Team t = null;

		HashSet<Match> matchesSet = new HashSet<Match>(Match.getMatches(game));
		for (Match match : matchesSet) {

			if (league != null) {
				if (match != null && (match.getLeagueName().equalsIgnoreCase(league) || match.getLeagueName().contains(league) || (match.getLeagueName() + " " + match.getSerieName()).equalsIgnoreCase(league) || (match.getLeagueName() + " " + match.getSerieName()).contains(league))) {
					matches.add(match);
				}
			} else if (team != null) {
				if (match.getTime1() != null) {
					if (match.getTime1().getName().equalsIgnoreCase(team) || match.getTime1().getName().contains(team)) {
						matches.add(match);
						t = match.getTime1();
						continue;
					}
				}
				if (match.getTime2() != null) {
					if (match.getTime2().getName().equalsIgnoreCase(team) || match.getTime2().getName().contains(team)) {
						matches.add(match);
						t = match.getTime2();
						continue;
					}
				}
			} else {
				matches.add(match);
			}
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

		StringBuilder description = new StringBuilder();

		if (matches == null || matches.isEmpty()) {
			description.append(lang.matches_embed_nogames);
		} else {
			description.append(lang.matches_embed_legend + "\n \n");
		}
		
		if (t != null) {
			
			if (t.getPlayers() != null && !t.getPlayers().isEmpty()) {
				
				TreeMap<Integer, String> linhas = new TreeMap<Integer, String>();
				
				int i1 = 0;
				int i2 = 0;
				int i3 = 0;
				int i4 = 0;
				int i5 = 0;
				
				for (Player p : t.getPlayers()) {
					
					String role = p.getRole() == null ? "" : p.getRole() + " ┃ ";
					String flag = p.getLocation() == null ? ":pirate_flag:" : ":flag_" + p.getLocation().toLowerCase() + ":";
					String firstname = p.getFirstname() == null ? "" : " " + p.getFirstname() + " ";
					String lastname = p.getLastname() == null ? "\n" : " " + p.getLastname() + "\n";

					int add = 0;
					
					if (p.getRole() != null && game == Game.LOL) {
						if (p.getRole().equalsIgnoreCase("top")) {
							role = "<:top:770307504088940564> ┃ ";
							add = i1++;
						}
						else if (p.getRole().equalsIgnoreCase("jun")) {
							role = "<:jungle:770307207366967317> ┃ ";
							add = 10 + i2++;
						}
						else if (p.getRole().equalsIgnoreCase("mid")) {
							role = "<:mid:770307226174488607> ┃ ";
							add = 20 + i3++;
						}
						else if (p.getRole().equalsIgnoreCase("adc")) {
							role = "<:adc:770307284327858186> ┃ ";
							add = 30 + i4++;
						}
						else if (p.getRole().equalsIgnoreCase("sup")) {
							role = "<:support:770307253969485907> ┃ ";
							add = 40 + i5++;
						}
						
						linhas.put(add, role + flag + " " + firstname + "**\"" + p.getSlug() + "\"**" + lastname);
					} else {
						int linha = linhas.isEmpty() ? 0 : linhas.lastKey() + 1;
						linhas.put(linha, role + flag + " " + firstname + "**\"" + p.getSlug() + "\"**" + lastname);
					}
				}
				
				for (String s : linhas.values()) {
					description.append(s);
				}
			}
			description.append(" \n");
			embed.setThumbnail(t.getImage());

		} else {
			embed.setThumbnail(game.getLogo());
		}

		embed.setDescription(description.toString());

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
		if (t != null) {
			embed.setFooter(t.getName() + " - " + game.getName() + " - " + "GMT" + s + String.valueOf(gi.getTm()));
		} else if (league != null) {
			embed.setFooter(league + " - " + game.getName() + " - " + "GMT" + s + String.valueOf(gi.getTm()));

		} else {
			embed.setFooter(lang.matches_embed_footer.replace("{TIMEZONE}", "GMT" + s + String.valueOf(gi.getTm())).replace("{GAME}", game.getName()).replace("{PAGE}", String.valueOf(page)).replace("{MAXPAGE}", String.valueOf(maxpage)));
		}
		embed.setColor(game.getColor());

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
			String flag1 = match.getTime1() == null || match.getTime1().getLocation() == null ? ":pirate_flag:" : ":flag_" + match.getTime1().getLocation() + ":";
			String flag2 = match.getTime2() == null || match.getTime2().getLocation() == null ? ":pirate_flag:" : ":flag_" + match.getTime2().getLocation() + ":";
			String name1 = match.getTime1() == null ? "TBA" : match.getTime1().getName();
			name1 = name1.length() > 19 ? name1.substring(0, 19) : name1;
			String name2 = match.getTime2() == null ? "TBA" : match.getTime2().getName();
			name2 = name2.length() > 19 ? name2.substring(0, 19) : name2;
			String points1 = gi.isSpoiler() ? "||` " + String.valueOf(match.getPoints1()) + " `||" :  String.valueOf(match.getPoints1()), points2 = gi.isSpoiler() ? "||` " + String.valueOf(match.getPoints2()) + " `||" : String.valueOf(match.getPoints2()), liveurl = "";

			if (match.getLiveUrl() != null && !match.getLiveUrl().equals("")) {
				liveurl = " | [" + lang.matches_watch + "](" + match.getLiveUrl() + ")";
			}

			String field1 = "";
			String field2 = "";

			if (match.getStatus() == MatchStatus.INGAME) {
				field1 = emote + " | " + hour + " | " + numbergames + " | " + tournament;
				field2 = emote + " | " + flag1 + " ` " + name1 + " ` **" + points1 + " x " + points2 + "** ` " + name2 + " ` " + flag2 + liveurl;
			} else if (match.getStatus() == MatchStatus.SOON) {
				field1 = emote + " | " + hour + " | " + numbergames + " | " + tournament;
				field2 = emote + " | " + flag1 + " ` " + name1 + " ` **x** ` " + name2 + " ` " + flag2 + liveurl;
			} else if (match.getStatus() == MatchStatus.CANCELED) {
				field1 = emote + " | " + hour + " | " + numbergames + " | " + tournament;
				field2 = emote + " | " + flag1 + " ` " + name1 + " ` **x** ` " + name2 + " ` " + flag2;
			} else if (match.getStatus() == MatchStatus.FINISHED) {
				field1 = emote + " | " + hour + " | " + numbergames + " | " + tournament;
				field2 = emote + " | " + flag1 + " ` " + name1 + " ` **" + points1 + " x " + points2 + "** ` " + name2 + " ` " + flag2;
			}

			if (field1.length() + field2.length() + embed.length() > MessageEmbed.EMBED_MAX_LENGTH_BOT) break;

			embed.addField(field1, field2, false);
		}

		return new EmbedTeam(embed, t);

	}

	/*public static MessageAction sendEmbedMessageAction(GuildInfo gi, MessageChannel mc, Language lang, @Nonnull Game game, int page, String team, String league) {
		ImageIO.setUseCache(false);

		EmbedTeam eb = sendEmbed(gi, mc, lang, game, page, team, league);

		long time = System.currentTimeMillis();

		EmbedBuilder embed = eb.getEb();
		Team t = eb.getTeam();
		if (t == null) {
			return mc.sendMessage(embed.build());
		}

		BufferedImage image = null;

		if (t != null && t.getPlayers() != null && !t.getPlayers().isEmpty()) {

			image = new BufferedImage(150 * t.getPlayers().size(), 150, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = (Graphics2D) image.getGraphics();

			g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			int i = 0;
			try {

				for (Player p : t.getPlayers()) {


					if (p.getImage() == null) {

						g2d.drawImage(ImageIO.read(Matcher.class.getResourceAsStream("/images/user.png")), 150 * i, 0, 150, 150, null);

					} else {

						g2d.drawImage(p.getImage(), 150 * i, 0, 150, 150, null);

					}
					i++;
				}

				embed.setImage("attachment://" + t.getId() + "_players.png");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (image != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "png", os);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return mc.sendMessage(embed.build()).addFile(new ByteArrayInputStream(os.toByteArray()), t.getId() + "_players.png");
		} else {
			return mc.sendMessage(embed.build());
		}
	} */

}
