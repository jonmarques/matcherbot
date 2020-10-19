package br.com.sauran.matcher.listener.matcher;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import br.com.sauran.matcher.Matcher;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.entities.Match;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.entities.enums.MatchStatus;
import br.com.sauran.matcher.events.ListenerAdapter;
import br.com.sauran.matcher.events.event.EventType;
import br.com.sauran.matcher.events.event.MatchEvent;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MatchStatusListener extends ListenerAdapter {

	@Override
	public void onEvent(MatchEvent e) {

		Match match = e.getMatch();

		Game game = e.getMatch().getGame();

		String tournament = match.getLeagueName() + " " + match.getSerieName();
		String flag1 = match.getLocation1() == null ? ":pirate_flag:" : ":flag_" + match.getLocation1() + ":";
		String flag2 = match.getLocation2() == null ? ":pirate_flag:" : ":flag_" + match.getLocation2() + ":";
		String name1 = match.getName1();
		String name2 = match.getName2();
		String points1 = String.valueOf(match.getPoints1());
		String points2 = String.valueOf(match.getPoints2());
		String liveurl = match.getLiveUrl() == null || match.getLiveUrl().equals("") ? null : match.getLiveUrl();

		if (e.getType() == EventType.STATUS && (match.getStatus() == MatchStatus.INGAME || match.getStatus() == MatchStatus.FINISHED)) {

			BufferedImage image = null;

			try {
				image = ImageIO.read(Matcher.class.getResourceAsStream("/images/" + game.getInitials().toLowerCase() + ".jpg"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			Graphics2D g2d = (Graphics2D) image.getGraphics();

			Twitter twitter = Matcher.getTwitter();
			if (twitter != null) {

				g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				g2d.setColor(Color.WHITE);
				g2d.setFont(Matcher.getFont().deriveFont((float)40));

				FontMetrics fm = g2d.getFontMetrics();

				g2d.drawString(match.getLeagueName(), (512 - (fm.stringWidth(match.getLeagueName()) / 2)), 425);
				g2d.drawString(match.getSerieName(), (512 - (fm.stringWidth(match.getSerieName()) / 2)), 475);

				g2d.setFont(Matcher.getFont().deriveFont((float)50));
				fm = g2d.getFontMetrics();

				if (match.getStatus() == MatchStatus.INGAME) {
					g2d.drawString("LIVE NOW", (512 - (fm.stringWidth("LIVE NOW") / 2)),  75);
				} else if (match.getStatus() == MatchStatus.FINISHED) {
					g2d.drawString("FINISHED", (512 - (fm.stringWidth("FINISHED") / 2)),  75);
				}
				g2d.drawString("BO" + match.getNumberOfGames(), (512 - (fm.stringWidth("BO" + match.getNumberOfGames()) / 2)),  125);

				g2d.setFont(Matcher.getFont().deriveFont((float)70));
				fm = g2d.getFontMetrics();

				if (match.getStatus() == MatchStatus.INGAME) {
					g2d.drawString("VS", (512 - (fm.stringWidth("VS") / 2)),  285);
				} else {
					g2d.drawString(match.getPoints1() + " - " + match.getPoints2(), (512 - (fm.stringWidth(match.getPoints1() + " - " + match.getPoints2()) / 2)),  285);
				}

				g2d.setFont(Matcher.getFont().deriveFont((float)50));
				fm = g2d.getFontMetrics();

				try {
					if (match.getImage1() != null) {
						g2d.drawImage(ImageIO.read(new URL(match.getImage1())), 100, 125, 250, 250, null);

					} else g2d.drawImage(ImageIO.read(Matcher.class.getResourceAsStream("/images/default.png")), 100, 125, 250, 250, null);
					if (match.getImage2() != null) {
						g2d.drawImage(ImageIO.read(new URL(match.getImage2())), 675, 125, 250, 250, null);
					} else g2d.drawImage(ImageIO.read(Matcher.class.getResourceAsStream("/images/default.png")), 675, 125, 250, 250, null);
				} catch (IOException e3) {
					e3.printStackTrace();
				}
				g2d.drawString(name1, (225 - (fm.stringWidth(name1) / 2)),  100);
				g2d.drawString(name2, (800 - (fm.stringWidth(name2) / 2)),  100);

				ByteArrayOutputStream os = new ByteArrayOutputStream();
				try {
					ImageIO.write(image, "png", os);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				InputStream is = new ByteArrayInputStream(os.toByteArray());

				try {
					if (match.getStatus() == MatchStatus.INGAME) {
						twitter.updateStatus(new StatusUpdate("🔴 Game Starting!\n \n" + name1 + " x " + name2 + "\n" + tournament + "\n \n" + match.getGame().getName() + "\n \nWatch in: " + match.getLiveUrl()).media("Imagem", is));
					} else if (match.getStatus() == MatchStatus.FINISHED) {
						twitter.updateStatus(new StatusUpdate("🟢 Game Finished!\n \n" + name1 + " x " + name2 + "\n" + tournament + "\n \n" + match.getGame().getName() + "\n \nWatch in: " + match.getLiveUrl()).media("Imagem", is));

					}
				} catch (TwitterException e1) {
					e1.printStackTrace();
				}
			}

		}

		for (Entry<Guild, GuildInfo> guilds : GuildInfo.getGuilds().entrySet()) {

			EmbedBuilder eb = new EmbedBuilder();

			String author = "";
			String description = "";

			GuildInfo gi= guilds.getValue();

			if (gi.getGamesTeams().stream().anyMatch((game.name() + ";" + match.getName1())::equalsIgnoreCase)) {
				if (match.getImage1() == null || match.getImage1().isEmpty()) eb.setThumbnail(game.getLogo());
				else eb.setThumbnail(match.getImage1());
			} else if (gi.getGamesTeams().stream().anyMatch((game.name() + ";" + match.getName2())::equalsIgnoreCase)) {
				if (match.getImage2() == null || match.getImage2().isEmpty()) eb.setThumbnail(game.getLogo());
				else eb.setThumbnail(match.getImage2());
			} else if (gi.getGames().contains(match.getGame())) {
				eb.setThumbnail(game.getLogo());
			} else continue;


			TextChannel channel = Matcher.getJda().getTextChannelById(gi.getTextChannel());
			if (channel == null || !channel.canTalk()) continue;

			Language lang = gi.getLang();

			String numbergames = lang.match_bo + match.getNumberOfGames();

			String points1_2 = gi.isSpoiler() ? "||` " + points1 + " `||" : points1;
			String points2_2 = gi.isSpoiler() ? "||` " + points2 + " `||" : points2;

			if (e.getType() == EventType.POINTS) {

				author = lang.match_status_points.replace("{GAMES}", numbergames);
				description = flag1 + "  " + name1 + "  " + points1_2 + " x " + points2_2 + "  " + name2 + "  " + flag2;

			} else if (e.getType() == EventType.STATUS) {
				if (match.getStatus() == MatchStatus.INGAME) {

					author = lang.match_status_ingame.replace("{GAMES}", numbergames);
					description = flag1 + "  " + name1 + "  x  " + name2 + "  " + flag2;

				} else if (match.getStatus() == MatchStatus.CANCELED) {

					author = lang.match_status_canceled.replace("{GAMES}", numbergames);
					description = flag1 + "  " + name1 + "  x  " + name2 + "  " + flag2;

				} else if (match.getStatus() == MatchStatus.FINISHED) {

					author = lang.match_status_finished.replace("{GAMES}", numbergames);
					description = flag1 + "  " + name1 + "  " + points1_2 + "  x  " + points2_2 + "  " + name2 + "  " + flag2;

				} else return;
			}

			eb.setTitle(description);
			eb.setAuthor(author, null, game.getLogo());
			eb.setDescription(tournament);
			if (liveurl != null && match.getStatus() == MatchStatus.INGAME) eb.setAuthor(author, liveurl, game.getLogo());
			else eb.setAuthor(author, null, game.getLogo());

			if (match.getStatus() == MatchStatus.INGAME) eb.setColor(Color.red);
			else if (match.getStatus() == MatchStatus.CANCELED) eb.setColor(Color.yellow);
			else if (match.getStatus() == MatchStatus.FINISHED) eb.setColor(Color.green);

			Calendar dateNow = Calendar.getInstance();
			dateNow.setTimeInMillis(match.getBegin_at());
			dateNow.setTimeZone(gi.getTimeZone());
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			SimpleDateFormat formatdate = null;
			if (LangManager.getLang("pt-br") == gi.getLang()) {
				formatdate = new SimpleDateFormat("EEEEE, d 'de' MMMMM 'de' yyyy",  new Locale("pt", "BR"));
			} else {
				formatdate = new SimpleDateFormat("EEEEE, MMMMM d, yyyy",  new Locale("en", "US"));
			}
			format.setTimeZone(gi.getTimeZone());
			formatdate.setTimeZone(gi.getTimeZone());
			
			eb.setFooter(format.format(dateNow.getTime()) + " | " + formatdate.format(dateNow.getTime()));
			
			//Message message = channel.getHistory().getRetrievedHistory().stream().filter(t -> t.getAuthor().getId().equals(br.com.sauran.matcher.Matcher.getConfig().getBotId())).findFirst().orElse(null);

			try {
			//	if (message != null) {
			//		if (!message.getEmbeds().isEmpty()) {
			//			for (MessageEmbed embed : message.getEmbeds()) {
			//				if ((embed.getDescription().equals(flag1 + "  " + name1 + "  x  " + name2 + "  " + flag2) || embed.getDescription().equals(flag1 + "  " + name1 + "  " + points1_2 + "  x  " + points2_2 + "  " + name2 + "  " + flag2))) {
								
			//				}
			//			}
			//		}
			//	}
				channel.sendMessage(eb.build()).queue();
			} catch (InsufficientPermissionException ex) {
				return;
			}
		}
	}

}
