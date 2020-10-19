package br.com.sauran.matcher.commands.setup;

import br.com.sauran.matcher.commands.Command;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.lang.Language;
import br.com.sauran.matcher.utils.NumberUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TimeZone extends Command {

	@Override
	public void fireCommand(MessageReceivedEvent e, String... args) {

		User user = e.getAuthor();
		Member m = e.getMember();
		MessageChannel mc = e.getChannel();

		GuildInfo guildinfo = GuildInfo.getGuilds().get(e.getGuild());
		java.util.TimeZone tmatual = guildinfo.getTimeZone();
		Language lang = guildinfo.getLang();

		if (!m.hasPermission(Permission.ADMINISTRATOR)) {
			mc.sendMessage(lang.only_admin.replace("{USER}", user.getAsMention())).queue();
			return;
		}

		if (args.length == 2) {
			
			
			if (!NumberUtil.isInteger(args[1])) {
				mc.sendMessage(lang.not_number.replace("{USER}", user.getAsMention()).replace("{ARG}", args[1])).queue();
				return;
			}
			
			int number = Integer.valueOf(args[1]);
			
			if (!(number >= -12 && number <= 12)) {
				mc.sendMessage(lang.not_number.replace("{USER}", user.getAsMention()).replace("{ARG}", args[1])).queue();
				return;
			}
			
			java.util.TimeZone tm = java.util.TimeZone.getTimeZone("GMT" + Math.abs(number));

			if (tmatual == tm) {
				mc.sendMessage(lang.tz_equals.replace("{USER}", user.getAsMention()).replace("{TIMEZONE}", args[1])).queue();
				return;
			}

			mc.sendMessage(lang.tz_changed.replace("{USER}", user.getAsMention()).replace("{NEWTIMEZONE}", args[1])).queue();
			guildinfo.setTm(number);
		} else {
			mc.sendMessage(lang.tz_usage.replace("{USER}", user.getAsMention())).queue();
		}
	}
	
}
