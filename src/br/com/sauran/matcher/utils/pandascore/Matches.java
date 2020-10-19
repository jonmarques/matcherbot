package br.com.sauran.matcher.utils.pandascore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sauran.matcher.entities.Match;
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

	protected static void getMatches() {

		long millis = System.currentTimeMillis();

		Calendar dateNow = Calendar.getInstance();
		dateNow.setTimeInMillis(millis);
		dateNow.add(Calendar.DATE, -1);

		Calendar dateNextDay = Calendar.getInstance();
		dateNextDay.setTimeInMillis(millis);
		dateNextDay.add(Calendar.DATE, 1);

		SimpleDateFormat formatterJson = new SimpleDateFormat("yyyy-MM-dd");

		for (Game jogo : Game.values()) {

			String jsonString = JSON.json("https://api.pandascore.co/" + jogo.getInitials() + "/matches?token=UKqyEwch9sYtzSBsMKFsbgw-mYtTPTREqkbGKAt_IW-ulRC_oL4&per_page=100&range[begin_at]=" + formatterJson.format(dateNow.getTime()) + "T00:00:00Z," + formatterJson.format(dateNextDay.getTime()) + "T23:59:00Z&sort=begin_at");

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

					String name1 = "TBA", name2 = "TBA";
					String image1 = null, image2 = null;
					String location1 = null, location2 = null;
					short points1 = 0, points2 = 0;
					MatchStatus status = MatchStatus.getByName(match.getString("status"));
					short numbergames = (short) match.getInt("number_of_games");
					String leagueName = league.getString("name");
					String serieName = serie.isNull("name") ? serie.getString("full_name") : serie.getString("name");
					String liveurl = match.isNull("official_stream_url") ? null : match.getString("official_stream_url");

					for (int i2 = 0; i2 < opponents.length(); i2++) {

						JSONObject opponent = opponents.getJSONObject(i2);
						JSONObject opponentinfo = opponent.getJSONObject("opponent");

						if (i2 == 0) {
							name1 = opponentinfo.isNull("name") ? name1 : opponentinfo.getString("name");
							location1 = opponentinfo.isNull("location") ? null : opponentinfo.getString("location").toLowerCase();
							image1 = opponentinfo.isNull("image_url") ? null : opponentinfo.getString("image_url");
						} else if (i2 == 1) {
							name2 = opponentinfo.isNull("name") ? name2 : opponentinfo.getString("name");
							location2 = opponentinfo.isNull("location") ? null : opponentinfo.getString("location").toLowerCase();
							image2 = opponentinfo.isNull("image_url") ? null : opponentinfo.getString("image_url");
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

					Match.createOrUpdateMatch(jogo, id, begin_at, ended_at, name1, name2, image1, image2, location1, location2, points1, points2, status, numbergames, leagueName, serieName, liveurl);
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
