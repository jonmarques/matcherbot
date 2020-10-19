package br.com.sauran.matcher.commands.setup;

import br.com.sauran.matcher.commands.Command;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Spoiler extends Command {

	@Override
	public void fireCommand(MessageReceivedEvent e, String... args) {

		User user = e.getAuthor();
		Member m = e.getMember();
		MessageChannel mc = e.getChannel();

		GuildInfo guildinfo = GuildInfo.getGuilds().get(e.getGuild());
		Language lang = guildinfo.getLang();

		if (!m.hasPermission(Permission.ADMINISTRATOR)) {
			mc.sendMessage(lang.only_admin.replace("{USER}", user.getAsMention())).queue();
			return;
		}

		if (args.length == 2) {


			if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("ativar") || args[1].equalsIgnoreCase("desativar")) {

				boolean spoiler2 = args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("ativar") ? true : false;

				if (guildinfo.isSpoiler() == spoiler2) {
					if (spoiler2) {
						mc.sendMessage(lang.spoiler_equals_enabled.replace("{USER}", user.getAsMention())).queue();
					} else {
						mc.sendMessage(lang.spoiler_equals_disabled.replace("{USER}", user.getAsMention())).queue();
					}
					return;
				}

				if (spoiler2) {
					mc.sendMessage(lang.spoiler_enabled.replace("{USER}", user.getAsMention())).queue();
				} else {
					mc.sendMessage(lang.spoiler_disabled.replace("{USER}", user.getAsMention())).queue();
				}

				guildinfo.setSpoiler(spoiler2);
			} else {
				mc.sendMessage(lang.spoiler_not_valid.replace("{USER}", user.getAsMention()).replace("{ARG}", args[1])).queue();
				return;
			}
		} else {
			mc.sendMessage(lang.spoiler_usage.replace("{USER}", user.getAsMention())).queue();
		}
	}

}
