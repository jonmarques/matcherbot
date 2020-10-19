package br.com.sauran.matcher.entities.enums;

import java.awt.Color;

public enum Game {

	CSGO("Counter-Strike: Global Offensive", "csgo", "https://imgur.com/exJLme6.png", ":csgo:709505583358672957", Color.decode("#e7a707")), 
	LOL("League of Legends", "lol", "https://imgur.com/sfQFMfM.png", ":lol:709505585204428911",Color.decode("#be9138")), 
	DOTA2("Dota 2", "dota2", "https://imgur.com/yVFd5kk.png", ":dota2:709505579508432897", Color.decode("#a32a16")), 
	CODMW("Call of Duty: Modern Warfare", "codmw", "https://imgur.com/qJn8ch0.png", ":codmw:709505589096611851", Color.decode("#d2d2d2")), 
	PUBG("Playerunknown's Battlegrounds", "pubg", "https://imgur.com/pebFr4f.png", ":pubg:709505626556072039",Color.decode("#f47b00")), 
	OW("Overwatch", "ow", "https://imgur.com/OQ0yZZA.png", ":ow:709505584428220456",Color.decode("#f97f00")), 
	RL("Rocket League", "rl", "https://imgur.com/DbO1r1H.png", ":rl:709505584797319189",Color.decode("#3d97cd")),
	R6("Rainbow Six Siege", "r6siege", "https://imgur.com/7QGRHgZ.png", ":r6:726858121166979192", Color.decode("#bd25fd")),
	FIFA("FIFA", "fifa", "https://imgur.com/pPusdRW.png", ":fifa:767468683366760459", Color.decode("#2d2c2e"));

	private String name, initials, logo;
	private Color color;
	private String reaction;

	Game(String name, String initials, String logo, String reaction, Color color) {
		this.name = name;
		this.initials = initials;
		this.logo = logo;
		this.reaction = reaction;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String getLogo() {
		return logo;
	}

	public Color getColor() {
		return color;
	}

	public String getReaction() {
		return reaction;
	}

	public String getInitials() {
		return initials;
	}

	public static Game getByReaction(String reaction) {
		for (Game g : Game.values()) {
			if (reaction.equalsIgnoreCase(g.getReaction())) return g;
		}
		return null;
	}

	public static Game getByName(String name) {
		for (Game g : Game.values()) {
			if (g.name().equalsIgnoreCase(name)) return g;
		}
		return null;
	}

	public static Game getByLongName(String game) {
		for (Game g : Game.values()) {
			if (g.getName().equalsIgnoreCase(game)) return g;
		}
		return null;
	}

	
	public static Game getEnum(String arg0) {
		for (Game game : Game.values()) {
			if (game.toString().equalsIgnoreCase(arg0)) return game;
		}
		return null;

	}
	
}
