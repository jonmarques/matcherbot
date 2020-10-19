package br.com.sauran.matcher.commands.setup;

import br.com.sauran.matcher.commands.Command;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.lang.Language;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Lang extends Command {

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
			Language newlang = LangManager.getLang(args[1]);

			if (newlang == null) {
				String languages = String.join(", ", LangManager.getLanguages().keySet());
				mc.sendMessage(lang.lang_unsupported.replace("{USER}", user.getAsMention()).replace("{LANG}", args[1]).replace("{LANGUAGES}", languages)).queue();
				return;
			} 

			if (newlang == lang) {
				mc.sendMessage(lang.lang_equals.replace("{USER}", user.getAsMention()).replace("{LANG}", args[1].toLowerCase())).queue();
				return;
			}

			mc.sendMessage(newlang.lang_changed.replace("{USER}", user.getAsMention()).replace("{NEWLANG}", LangManager.getKey(newlang))).queue();
			guildinfo.setLang(newlang);
		} else {
			mc.sendMessage(lang.lang_usage.replace("{USER}", user.getAsMention())).queue();
		}
	}

}
