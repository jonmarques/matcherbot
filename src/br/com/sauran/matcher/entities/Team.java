package br.com.sauran.matcher.entities;

import java.util.HashMap;
import java.util.List;

import br.com.sauran.matcher.entities.enums.Game;

public class Team {

	public static HashMap<Integer, Team> times = new HashMap<>();
	
	public static HashMap<Integer, Team> getTimes() {
		return times;
	}
	
	public static Team getTime(int i) {
		return times.get(i);
	}
	
	private int id;
	private String tag;
	private Game game;
	private String name;
	private String image;
	private String location;
	private long modified;
	private List<Player> players;

	public Team(int id, String tag, Game game, String name, String image, String location, long modified, List<Player> players) {
		this.id = id;
		this.tag = tag;
		this.game = game;
		this.name = name;
		this.image = image;
		this.location = location;
		this.modified = modified;
		this.players = players;
		times.put(id, this);
	}
	
	public int getId() {
		return id;
	}
	
	public String getTag() {
		return tag;
	}
	
	public Game getGame() {
		return game;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getLocation() {
		return location;
	}
	
	public long getModified() {
		return modified;
	}
	
	public void setModified(long modified) {
		this.modified = modified;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
}
