package br.com.sauran.matcher.commands.notify;

import java.util.List;

import br.com.sauran.matcher.commands.Command;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.entities.enums.Game;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Notify extends Command {

	@Override
	public void fireCommand(MessageReceivedEvent e, String... args) {

		User user = e.getAuthor();
		MessageChannel mc = e.getChannel();
		Guild guild = e.getGuild();

		mc.sendTyping().queue();

		Language lang = GuildInfo.getGuilds().get(e.getGuild()).getLang();

		if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			mc.sendMessage(lang.only_admin.replace("{USER}", user.getAsMention())).queue();
			return;
		}

		if (args.length > 1) {

			if (args[1].equalsIgnoreCase("info")) {

				GuildInfo guildinfo = GuildInfo.getGuilds().get(guild);
				if (guildinfo == null) {
					mc.sendMessage(lang.notify_not_active.replace("{USER}", user.getAsMention())).queue();
					return;
				}

				List<Game> games = guildinfo.getGames();
				List<String> teams = guildinfo.getGamesTeams();

				if (games == null && teams == null || (games.isEmpty() && teams.isEmpty())) {
					mc.sendMessage(lang.notify_not_active.replace("{USER}", user.getAsMention())).queue();
					return;
				}

				StringBuilder gamesstring = new StringBuilder();
				for (Game game : games) {
					gamesstring.append(" • " + game.getName() + "\n");
				}

				StringBuilder teamsstring = new StringBuilder();
				for (String team : teams) {
					String[] split = team.split(";");
					Game game = Game.valueOf(split[0]);
					teamsstring.append(" • " + split[1] + " | " + game.getName() + "\n");
				}

				if (gamesstring.toString().isEmpty()) {
					gamesstring.append(lang.notify_none + "\n");
				}

				if (teamsstring.toString().isEmpty()) {
					teamsstring.append(lang.notify_none + "\n");
				}

				String txt =  guild.getTextChannelById(guildinfo.getTextChannel()) == null ? guildinfo.getTextChannel() : guild.getTextChannelById(guildinfo.getTextChannel()).getAsMention();

				mc.sendMessage(lang.notify_info_message.replace("{USER}", user.getAsMention()).replace("{CHANNEL}", txt).replace("{GAMES}", String.valueOf(games.size())).replace("{GAMESTOTAL}", String.valueOf(Game.values().length)).replace("{TEAMS}", String.valueOf(teams.size()).replace("{TEAMSTOTAL}", "20")).replace("{GAMESSTRING}", gamesstring.toString()).replace("{TEAMSSTRING}", teamsstring.toString())).queue();
				return;

			}

			if (args[1].equalsIgnoreCase("channel") || args[1].equalsIgnoreCase("canal")) {

				GuildInfo guildinfo = GuildInfo.getGuilds().get(guild);

				if (guildinfo == null) {
					guildinfo = new GuildInfo(e.getGuild(), e.getTextChannel().getId(), null, null, null, 0, false);
				} else {
					guildinfo.setTextchannel(e.getTextChannel().getId());
				}

				mc.sendMessage(lang.notify_channel_changed.replace("{USER}", user.getAsMention()).replace("{CHANNEL}", e.getTextChannel().getAsMention())).queue();
				return;

			}

			if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("adicionar")) {

				if (args.length == 4) {

					if (args[2].equalsIgnoreCase("game") || args[2].equalsIgnoreCase("jogo")) {

						GuildInfo guildinfo = GuildInfo.getGuilds().get(guild);

						if (guildinfo == null) {
							guildinfo = new GuildInfo(e.getGuild(), e.getTextChannel().getId(), null, null, null, 0, false);
						}

						Game game = Game.getEnum(args[3]);
						if (game == null) {
							mc.sendMessage(lang.notify_game_unsupported.replace("{USER}", user.getAsMention()).replace("{GAME}", args[3])).queue();
							return;
						}

						if (guildinfo.addGames(game)) {
							mc.sendMessage(lang.notify_game_added.replace("{USER}", user.getAsMention()).replace("{GAME}", game.getName())).queue();
						} else {
							mc.sendMessage(lang.notify_game_added_again.replace("{USER}", user.getAsMention()).replace("{GAME}", game.getName())).queue();
						}
						return;
					}
				}

				if (args.length >= 5) {

					if (args[2].equalsIgnoreCase("team") || args[2].equalsIgnoreCase("time")) {

						GuildInfo guildinfo = GuildInfo.getGuilds().get(guild);

						if (guildinfo == null) {
							guildinfo = new GuildInfo(e.getGuild(), e.getTextChannel().getId(), null, null, null, 0, false);
						}

						if (guildinfo.getGamesTeams().size() == 20) {
							mc.sendMessage(lang.notify_team_limit.replace("{USER}", user.getAsMention()).replace("{LIMIT}", "20")).queue();
							return;
						}

						Game game = Game.getEnum(args[3]);
						if (game == null) {
							mc.sendMessage(lang.notify_game_unsupported.replace("{USER}", user.getAsMention()).replace("{GAME}", args[3])).queue();
							return;
						}

						StringBuilder sb = new StringBuilder();
						for (int i = 4; i < args.length; i++) {
							sb.append(args[i]);
							if (i != args.length - 1) sb.append(" ");
						}

						if (guildinfo.addGamesteams(game.name().toUpperCase() + ";" + sb.toString())) {
							mc.sendMessage(lang.notify_team_added.replace("{USER}", user.getAsMention()).replace("{TEAM}", sb.toString()).replace("{GAME}", args[3])).queue();
						} else {
							mc.sendMessage(lang.notify_team_added_again.replace("{USER}", user.getAsMention()).replace("{TEAM}", sb.toString()).replace("{GAME}", args[3])).queue();
						}
						return;
					}
				}
			}  else if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("remover")) {

				if (args.length == 4) {

					if (args[2].equalsIgnoreCase("game") || args[2].equalsIgnoreCase("jogo")) {

						Game game = Game.getEnum(args[3]);
						if (game == null) {
							mc.sendMessage(lang.notify_game_unsupported.replace("{USER}", user.getAsMention()).replace("{GAME}", args[3])).queue();
							return;
						}

						GuildInfo guildinfo = GuildInfo.getGuilds().get(guild);

						if (guildinfo != null && guildinfo.removeGames(game)) {
							mc.sendMessage(lang.notify_game_removed.replace("{USER}", user.getAsMention()).replace("{GAME}", game.getName())).queue();
						} else {
							mc.sendMessage(lang.notify_game_removed_none.replace("{USER}", user.getAsMention()).replace("{GAME}", game.getName())).queue();
						}

						return;
					}
				}

				if (args.length >= 5) {

					if (args[2].equalsIgnoreCase("team") || args[2].equalsIgnoreCase("time")) {

						Game game = Game.getEnum(args[3]);
						if (game == null) {
							mc.sendMessage(lang.notify_game_unsupported.replace("{USER}", user.getAsMention()).replace("{GAME}", args[3])).queue();
							return;
						}

						GuildInfo guildinfo = GuildInfo.getGuilds().get(guild);

						StringBuilder sb = new StringBuilder();
						for (int i = 4; i < args.length; i++) {
							sb.append(args[i]);
							if (i != args.length - 1) sb.append(" ");
						}

						if (guildinfo != null && guildinfo.removeGamesteams(game.name().toUpperCase() + ";" + sb.toString())) {
							mc.sendMessage(lang.notify_team_removed.replace("{USER}", user.getAsMention()).replace("{TEAM}", sb.toString()).replace("{GAME}", game.getName())).queue();

						} else {
							mc.sendMessage(lang.notify_team_removed_none.replace("{USER}", user.getAsMention()).replace("{TEAM}", sb.toString()).replace("{GAME}", game.getName())).queue();
						}
						return;
					}
				}
			}
		}
		mc.sendMessage(lang.notify_usage.replace("{USER}", user.getAsMention())).queue();
	}

}
