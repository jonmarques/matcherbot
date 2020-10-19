package br.com.sauran.matcher.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.sauran.matcher.Matcher;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.entities.enums.MatchStatus;
import br.com.sauran.matcher.events.event.EventType;
import br.com.sauran.matcher.events.event.MatchEvent;

public class Match {

	private static HashMap<Game, List<Match>> matches = new HashMap<Game, List<Match>>();

	private static void createHash() {
		if (matches == null || matches.isEmpty()) {
			for (Game game : Game.values()) {
				matches.put(game, new ArrayList<Match>());
			}
		}
	}

	public static List<Match> getMatches(Game game) {
		createHash();
		return matches.get(game);
	}

	public static void resetMatches() {
		for (Game game : Game.values()) {
			matches.put(game, new ArrayList<Match>());
		}
	}

	public static Match getMatch(Game game, int id) {
		createHash();
		return matches.get(game).stream().filter(t -> t.getId() == id).findAny().orElse(null);
	}

	public static Match createOrUpdateMatch(Game game, int id, long begin_at, long ended_at, String name1, String name2, String image1, String image2, String location1, String location2, short points1, short points2, MatchStatus status, short numberOfGames, String leagueName, String serieName, String liveUrl) {
		createHash();
		Match match = getMatch(game, id);
		if (match != null) {
			EventType event = null;
			if (location1 != null && !location1.isEmpty() && !location1.equals(match.getLocation1())) match.setLocation1(location1);
			if (location2 != null && !location2.isEmpty() && !location2.equals(match.getLocation2())) match.setLocation2(location2);
			if (name1 != null && !name1.isEmpty() && !name1.equals(match.getName1())) match.setName1(name1);
			if (name2 != null && !name2.isEmpty() && !name2.equals(match.getName2())) match.setName2(name2);
			if (image1 != null && !image1.isEmpty() && !image1.equals(match.getImage1())) match.setImage1(image1);
			if (image2 != null && !image2.isEmpty() && !image2.equals(match.getImage2())) match.setImage2(image2);
			if (liveUrl != null && !liveUrl.isEmpty() && !liveUrl.equals(match.getLiveUrl())) match.setLiveUrl(liveUrl);
			if (match.getBegin_at() != begin_at) match.setBegin_at(begin_at);
			if ( match.getEnded_at() != ended_at) match.setBegin_at(ended_at);

			if (match.getPoints1() != points1) {
				match.setPoints1(points1);
				event = EventType.POINTS;
			}
			if (match.getPoints2() != points2) {
				match.setPoints2(points2);
				event = EventType.POINTS;
			}
			if (match.getStatus() != status) {
				match.setStatus(status);
				event = EventType.STATUS;
			}
			if (event != null) Matcher.getEventHandler().notify(new MatchEvent(match, event));
		} else {
			match = new Match(game, id, begin_at, ended_at, name1, name2, image1, image2, location1, location2, points1, points2, status, numberOfGames, leagueName, serieName, liveUrl);
		}
		return match;
	}

	private Game game;
	private int id;
	private long begin_at;
	private long ended_at;
	private String name1, name2;
	private String image1, image2;
	private String location1, location2;
	private short points1, points2;
	private MatchStatus status;
	private short numberOfGames;
	private String leagueName;
	private String serieName;
	private String liveUrl;

	private Match(Game game, int id, long begin_at, long ended_at, String name1, String name2, String image1, String image2, String location1, String location2, short points1, short points2, MatchStatus status, short numberOfGames, String leagueName, String serieName, String liveUrl) {
		this.game = game;
		this.id = id;
		this.begin_at = begin_at;
		this.ended_at = ended_at;
		this.name1 = name1;
		this.name2 = name2;
		this.image1 = image1;
		this.image2 = image2;
		this.location1 = location1;
		this.location2 = location2;
		this.points1 = points1;
		this.points2 = points2;
		this.status = status;
		this.numberOfGames = numberOfGames;
		this.leagueName = leagueName;
		this.serieName = serieName;
		this.liveUrl = liveUrl;
		matches.get(game).add(this);
	}

	public int getId() {
		return id;
	}

	public Game getGame() {
		return game;
	}

	public long getBegin_at() {
		return begin_at;
	}

	public void setBegin_at(long begin_at) {
		this.begin_at = begin_at;
	}
	
	public long getEnded_at() {
		return ended_at;
	}
	
	public void setEnded_at(long ended_at) {
		this.ended_at = ended_at;
	}

	public String getName1() {
		return name1;
	}

	public String getName2() {
		return name2;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public String getLocation1() {
		return location1;
	}

	public String getLocation2() {
		return location2;
	}

	public short getPoints1() {
		return points1;
	}

	public void setPoints1(short points1) {
		this.points1 = points1;
	}

	public short getPoints2() {
		return points2;
	}

	public void setPoints2(short points2) {
		this.points2 = points2;
	}

	public MatchStatus getStatus() {
		return status;
	}

	public void setStatus(MatchStatus status) {
		this.status = status;
	}

	public short getNumberOfGames() {
		return numberOfGames;
	}

	public String getLeagueName() {
		return leagueName;
	}

	public String getSerieName() {
		return serieName;
	}

	public String getLiveUrl() {
		return liveUrl;
	}

	public void setLiveUrl(String liveUrl) {
		this.liveUrl = liveUrl;
	}

	public void setLocation1(String location1) {
		this.location1 = location1;
	}

	public void setLocation2(String location2) {
		this.location2 = location2;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

}
