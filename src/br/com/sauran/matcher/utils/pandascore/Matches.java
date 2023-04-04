package br.com.sauran.matcher.utils.pandascore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sauran.matcher.entities.Match;
import br.com.sauran.matcher.entities.Player;
import br.com.sauran.matcher.entities.Team;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.entities.enums.MatchStatus;
import br.com.sauran.matcher.utils.JSON;

public class Matches {

	public Matches() {

		Calendar dateNow = Calendar.getInstance();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				dateNow.setTimeInMillis(System.currentTimeMillis());

				if (dateNow.get(Calendar.HOUR_OF_DAY) == 0 && dateNow.get(Calendar.MINUTE) == 0) {
					Match.resetMatches();
					System.out.println("Partidas reiniciadas!");
				}

				getMatches();	
			}
		};

		timer.scheduleAtFixedRate(task, 0, 60 * 1000);
	}

	protected void getMatches() {

		long millis = System.currentTimeMillis();

		Calendar dateNow = Calendar.getInstance();
		dateNow.setTimeInMillis(millis);
		dateNow.add(Calendar.DATE, -1);

		Calendar dateNextDay = Calendar.getInstance();
		dateNextDay.setTimeInMillis(millis);
		dateNextDay.add(Calendar.DATE, 1);

		SimpleDateFormat formatterJson = new SimpleDateFormat("yyyy-MM-dd");

		HashMap<Game, List<Team>> update = new HashMap<>();

		for (Game jogo : Game.values()) {

			String jsonString = JSON.json("https://api.pandascore.co/" + jogo.getInitials() + "/matches?token=TOKEN&per_page=100&range[begin_at]=" + formatterJson.format(dateNow.getTime()) + "T00:00:00Z," + formatterJson.format(dateNextDay.getTime()) + "T23:59:00Z&sort=begin_at");

			if (jsonString != null) {
				JSONArray json = new JSONArray(jsonString);

				for (int i = 0; i < json.length(); i++) {

					JSONObject match = json.getJSONObject(i);
					JSONObject league = match.getJSONObject("league");
					JSONObject serie = match.getJSONObject("serie");
					JSONArray opponents = match.getJSONArray("opponents");
					JSONArray results = match.getJSONArray("results");

					int id = match.getInt("id");
					long begin_at = convertTime(match.getString("scheduled_at"));
					long ended_at = match.isNull("end_at") ? 0 : convertTime(match.getString("end_at"));
					Team team1 = null, team2 = null;
					short points1 = 0, points2 = 0;
					MatchStatus status = MatchStatus.getByName(match.getString("status"));
					short numbergames = (short) match.getInt("number_of_games");
					String leagueName = league.getString("name");
					String serieName = serie.isNull("name") ? serie.getString("full_name") : serie.getString("name");
					String liveurl = match.isNull("official_stream_url") ? null : match.getString("official_stream_url");

					for (int i2 = 0; i2 < opponents.length(); i2++) {

						JSONObject opponent = opponents.getJSONObject(i2);
						JSONObject opponentinfo = opponent.getJSONObject("opponent");

						int teamid = opponentinfo.getInt("id");

						String tag = opponentinfo.isNull("acronym") ? null : opponentinfo.getString("acronym");
						String name = opponentinfo.isNull("name") ? "TBA" : opponentinfo.getString("name");
						String location = opponentinfo.isNull("location") ? null : opponentinfo.getString("location").toLowerCase();
						String image = opponentinfo.isNull("image_url") ? null : opponentinfo.getString("image_url");
						long modified = convertTime(opponentinfo.getString("modified_at"));

						Team team = Team.getTime(teamid);
						if (team != null) {

							if (team.getModified() < convertTime(opponentinfo.getString("modified_at"))) {
								if (update.containsKey(jogo)) {
									update.get(jogo).add(team);
								} else {
									List<Team> teams = new ArrayList<>();
									teams.add(team);
									update.put(jogo, teams);
								}
							}

							if (i2 == 0) team1 = team;
							else if (i2 == 1) team2 = team;

						} else {

							team = new Team(teamid, tag, jogo, name, image, location, modified, null);

							if (update.containsKey(jogo)) {
								update.get(jogo).add(team);
							} else {
								List<Team> teams = new ArrayList<>();
								teams.add(team);
								update.put(jogo, teams);
							}							

							if (i2 == 0) team1 = team;
							else if (i2 == 1) team2 = team;
						}

					}

					for (int i2 = 0; i2 < results.length(); i2++) {

						JSONObject opponent = results.getJSONObject(i2);
						if (i2 == 0) {
							points1 = (short) opponent.getInt("score");
						} else if (i2 == 1) {
							points2 = (short) opponent.getInt("score");
						}

					}

					Match.createOrUpdateMatch(jogo, id, begin_at, ended_at, team1, team2, points1, points2, status, numbergames, leagueName, serieName, liveurl);
				}
			}

		}

		if (!update.isEmpty()) {
			for (Entry<Game, List<Team>> game : update.entrySet()) {

				StringBuilder sb = new StringBuilder();
				for (Team team : game.getValue()) {
					sb.append(team.getId() + ",");
				}
				sb.delete(sb.length() - 1, sb.length());

				String jsonString = JSON.json("https://api.pandascore.co/teams?TOKEN&per_page=100&filter[id]=" + sb.toString());

				if (jsonString != null) {

					JSONArray json = new JSONArray(jsonString);

					for (int i = 0; i < json.length(); i++) {

						JSONObject t = json.getJSONObject(i);

						int id = t.getInt("id");

						List<Player> players = new ArrayList<>();
						JSONArray p = t.getJSONArray("players");

						Team team = Team.getTime(id);

						for (int i2 = 0; i2 < p.length(); i2++) {

							JSONObject player = p.getJSONObject(i2);

							String slug = player.isNull("name") ? null : player.getString("name");
							String nationality = player.isNull("nationality") ? null : player.getString("nationality");
							String firstname = player.isNull("first_name") ? null : player.getString("first_name");
							String lastname = player.isNull("last_name") ? null : player.getString("last_name");
							String role = player.isNull("role") ? null : player.getString("role");
							
							players.add(new Player(slug, nationality, firstname, lastname, role));
						}

						team.setPlayers(players);
					}
				}
			}
		}
	}

	private static long convertTime(String text) {
		String[] datafull = text.split("T");
		String data = datafull[0].replace("T", "").replace("Z", "");
		String time = datafull[1].replace("T", "").replace("Z", "");

		Calendar date = Calendar.getInstance();
		date.setTimeZone(TimeZone.getTimeZone("Etc/GMT-0"));
		date.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]) - 1, Integer.valueOf(data.split("-")[2]), Integer.valueOf(time.split(":")[0]), Integer.valueOf(time.split(":")[1]), Integer.valueOf(time.split(":")[2]));

		return date.getTimeInMillis();
	}

}
