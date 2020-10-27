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

	public static Match createOrUpdateMatch(Game game, int id, long begin_at, long ended_at, Team time1, Team time2, short points1, short points2, MatchStatus status, short numberOfGames, String leagueName, String serieName, String liveUrl) {
		createHash();
		Match match = getMatch(game, id);
		if (match != null) {
			EventType event = null;

			if (match.getTime1() == null || match.getTime1() != time1) match.setTime1(time1);
			if (match.getTime2() == null || match.getTime2() != time2) match.setTime2(time2);

			if (liveUrl != null && !liveUrl.isEmpty() && !liveUrl.equals(match.getLiveUrl())) match.setLiveUrl(liveUrl);
			if (match.getBegin_at() != begin_at) match.setBegin_at(begin_at);
			if (match.getEnded_at() != ended_at) match.setEnded_at(ended_at);

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
			match = new Match(game, id, begin_at, ended_at, time1, time2, points1, points2, status, numberOfGames, leagueName, serieName, liveUrl);
		}
		return match;
	}

	private Game game;
	private int id;
	private long begin_at;
	private long ended_at;
	private Team time1, time2;
	private short points1, points2;
	private MatchStatus status;
	private short numberOfGames;
	private String leagueName;
	private String serieName;
	private String liveUrl;

	private Match(Game game, int id, long begin_at, long ended_at, Team time1, Team time2, short points1, short points2, MatchStatus status, short numberOfGames, String leagueName, String serieName, String liveUrl) {
		this.game = game;
		this.id = id;
		this.begin_at = begin_at;
		this.ended_at = ended_at;
		this.time1 = time1;
		this.time2 = time2;
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

	public Team getTime1() {
		return time1;
	}
	
	public void setTime1(Team time1) {
		this.time1 = time1;
	}
	
	public Team getTime2() {
		return time2;
	}
	
	public void setTime2(Team time2) {
		this.time2 = time2;
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

}
