package br.com.sauran.matcher.database;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import br.com.sauran.matcher.Matcher;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.entities.Guild;

public class MongoDB {

	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;

	public MongoDB() {
		openConnection();
	}

	private void openConnection() {
		mongoClient = MongoClients.create("MONGODB LINK");
		database = mongoClient.getDatabase("matcherdb");
		collection = database.getCollection("guilds");

		for (Document documents : collection.find()) {

			String guildId = documents.getString("ID");
			Guild guild = Matcher.getJda().getGuildById(guildId);
			if (guild == null) {
				if (!Matcher.getConfig().isTest()) collection.deleteOne(documents);
				continue;
			}

			String textchannel = documents.getString("Text");
			Language lang = null;
			if (documents.containsKey("Lang")) {
				lang = LangManager.getLang(documents.getString("Lang"));
			}
			
			boolean spoiler = false;
			if (documents.containsKey("Spoiler")) {
				spoiler = documents.getBoolean("Spoiler", false);
			}

			int tm = documents.getInteger("TimeZone", 0);

			List<String> games = documents.getList("Games", String.class);
			List<String> gamesteams = documents.getList("Teams", String.class);

			List<Game> gamesg = new ArrayList<Game>();
			List<String> gamesteamsg = new ArrayList<String>();

			if (games != null && !games.isEmpty()) {
				for (String s : games) {
					gamesg.add(Game.valueOf(s.toUpperCase()));
				}
			}

			if (gamesteams != null && !gamesteams.isEmpty()) {
				for (String s : gamesteams) {
					gamesteamsg.add(s);
				}
			}

			new GuildInfo(guild, textchannel, gamesg, gamesteamsg, lang, tm, spoiler);
		}

	}

	public void updateDocument(GuildInfo guildInfo) {
		Document document = collection.find(new Document("ID", guildInfo.getGuild().getId())).first();

		List<String> gameslist = new ArrayList<String>();

		if (guildInfo.getGames() != null) {
			for (Game game : guildInfo.getGames()) {
				gameslist.add(game.name().toUpperCase());
			}
		}

		if (document == null) {

			document = new Document("ID", guildInfo.getGuild().getId())
					.append("Text", guildInfo.getTextChannel())
					.append("Games", gameslist)
					.append("Teams", guildInfo.getGamesTeams())
					.append("Lang", LangManager.getKey(guildInfo.getLang()))
					.append("TimeZone", guildInfo.getTm())
					.append("Spoiler", guildInfo.isSpoiler());
			collection.insertOne(document);

		} else {

			Bson update = new Document("Text", guildInfo.getTextChannel())
					.append("Games", gameslist)
					.append("Teams", guildInfo.getGamesTeams())
					.append("Lang", LangManager.getKey(guildInfo.getLang()))
					.append("TimeZone", guildInfo.getTm())
					.append("Spoiler", guildInfo.isSpoiler());
			Bson operation = new Document("$set", update);
			collection.updateOne(document, operation);

		}
	}

	public void deleteDocument(long id) {
		Document document = collection.find(new Document("ID", id)).first();
		if (document == null) return;
		collection.deleteOne(document);

	}

}
