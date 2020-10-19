package br.com.sauran.matcher.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;

public class BotUtils {

	public static boolean hasPermissions(Guild guild) {
		Member member = guild.getSelfMember();
		return member.hasPermission(Permission.MESSAGE_HISTORY) && member.hasPermission(Permission.MESSAGE_EXT_EMOJI) && member.hasPermission(Permission.MESSAGE_ADD_REACTION) && member.hasPermission(Permission.MESSAGE_WRITE) && member.hasPermission(Permission.MESSAGE_MANAGE);
	}

	public static boolean hasPermissions(Guild guild, GuildChannel gc) {
		Member member = guild.getSelfMember();
		return member.hasPermission(gc, Permission.MESSAGE_HISTORY) && member.hasPermission(gc, Permission.MESSAGE_EXT_EMOJI) && member.hasPermission(gc, Permission.MESSAGE_ADD_REACTION) && member.hasPermission(gc, Permission.MESSAGE_WRITE) && member.hasPermission(gc, Permission.MESSAGE_MANAGE);
	}

}
