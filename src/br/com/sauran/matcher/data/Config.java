package br.com.sauran.matcher.data;

public class Config {

	private String prefix = "mt.";
	private String discordListToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY5NDk5MjA2ODU4OTkxMjA4NCIsImJvdCI6dHJ1ZSwiaWF0IjoxNTg3MzM0MDc1fQ.wBxnJm0Pt9CSMmC4cFWUEZWIPhptjmKm_yT9yaTzSo8";

	private String botToken = "Njk0OTkyMDY4NTg5OTEyMDg0.XtaskQ.XuT-YNm3luWgznpcToOQ-LBAM6g";
	private String botId = "694992068589912084";
	private boolean test = false;

	//private String botToken = "Njk1MzU0OTMyMTUyNTAwMjQ1.XvjayA.CjuEKfCiUaWThG4Phbo2t5py8dA";
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
