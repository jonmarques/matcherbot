package br.com.sauran.matcher.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import br.com.sauran.matcher.Matcher;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;

public class GuildInfo {

	private static HashMap<Guild, GuildInfo> guilds = new HashMap<Guild, GuildInfo>();

	public static HashMap<Guild, GuildInfo> getGuilds() {
		return guilds;
	}

	public static GuildInfo getGuildInfo(Guild guild) {
		return guilds.get(guild);
	}

	public static boolean removeGuild(Guild guild) {
		if (guilds.containsKey(guild)) {
			guilds.remove(guild);
			Matcher.getMongoDB().deleteDocument(guild.getIdLong());
			return true;
		}
		return false;
	}

	private Guild guild;
	private List<Game> games;
	private List<String> gamesteams;
	private String textchannel;
	private Language lang;
	private int tm;
	private boolean spoiler;

	public GuildInfo(Guild guild, String textchannel, List<Game> games, List<String> gamesteams, Language lang, int tm, boolean spoiler) {
		this.guild = guild;
		this.textchannel = textchannel;
		this.games = games == null ? new ArrayList<Game>() : games;
		this.gamesteams = gamesteams == null ? new ArrayList<String>() : gamesteams;
		this.spoiler = spoiler;
		if (lang == null) {
			if (guild.getRegion() == Region.BRAZIL || guild.getRegion() == Region.VIP_BRAZIL) this.lang = LangManager.getLang("pt-br");
			else this.lang = LangManager.getLang("en-us");
		} else {
			this.lang = lang;
		}

		this.tm = tm;

		guilds.put(guild, this);
	}

	public Guild getGuild() {
		return guild;
	}

	public boolean isSpoiler() {
		return spoiler;
	}

	public void setSpoiler(boolean spoiler) {
		if (this.spoiler != spoiler) {
			this.spoiler = spoiler;
			Matcher.getMongoDB().updateDocument(this);
		}
	}

	public String getTextChannel() {
		return textchannel;
	}

	public void setTextchannel(String textchannel) {
		if (this.textchannel == null || !this.textchannel.equals(textchannel)) {
			this.textchannel = textchannel;
			Matcher.getMongoDB().updateDocument(this);
		}
	}

	public Language getLang() {
		return lang;
	}

	public void setLang(Language lang) {
		if (this.lang != lang) {
			this.lang = lang;
			Matcher.getMongoDB().updateDocument(this);
		}
	}

	public TimeZone getTimeZone() {
		String s = tm > 0 ? "+" : "";
		return TimeZone.getTimeZone("GMT" + s + tm);
	}

	public int getTm() {
		return tm;
	}

	public void setTm(int tm) {
		this.tm = tm;
		Matcher.getMongoDB().updateDocument(this);
	}

	public boolean addGames(Game... gs) {
		for (Game g : gs) {
			if (games.contains(g)) return false;
			games.add(g);
		}
		Matcher.getMongoDB().updateDocument(this);
		return true;
	}

	public boolean removeGames(Game...gs) {
		for (Game g : gs) {
			if (!games.contains(g)) return false;
			games.remove(g);
		}
		Matcher.getMongoDB().updateDocument(this);
		return true;
	}

	public List<Game> getGames() {
		return games;
	}

	public boolean addGamesteams(String... gts) {
		for (String g : gts) {
			if (gamesteams.stream().anyMatch(g::equalsIgnoreCase)) return false;
			gamesteams.add(g);
		}
		Matcher.getMongoDB().updateDocument(this);
		return true;
	}

	public boolean removeGamesteams(String... gts) {
		for (String g : gts) {
			if (!gamesteams.stream().anyMatch(g::equalsIgnoreCase)) return false;
			gamesteams.remove(gamesteams.stream().filter(g::equalsIgnoreCase).findFirst().get());
		}
		Matcher.getMongoDB().updateDocument(this);
		return true;
	}

	public List<String> getGamesTeams() {
		return gamesteams;
	}

}
