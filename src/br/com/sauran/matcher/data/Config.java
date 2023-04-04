package br.com.sauran.matcher.data;

public class Config {

	private String prefix = "mt.";
	private String discordListToken = "";

	private String botToken = "";
	private String botId = "694992068589912084";
	private boolean test = false;

	//private String botToken = "";
	//private String botId = "695354932152500245";
	//private boolean test = true;

	public Config() {
		// TODO Auto-generated constructor stub
	}

	public boolean isTest() {
		return test;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getBotToken() {
		return botToken;
	}

	public String getBotId() {
		return botId;
	}

	public String getDiscordListToken() {
		return discordListToken;
	}

}
