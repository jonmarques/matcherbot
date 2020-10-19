package br.com.sauran.matcher.commands.info;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.sauran.matcher.Matcher;
import br.com.sauran.matcher.commands.Command;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.lang.Field;
import br.com.sauran.matcher.lang.Language;
import br.com.sauran.matcher.utils.AsyncInfoMonitor;
import br.com.sauran.matcher.utils.TimerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Info extends Command {

	private NumberFormat nf;

	public Info() {
		super();
		nf = NumberFormat.getInstance(new Locale("pt", "BR"));

	}

	@Override
	public void fireCommand(MessageReceivedEvent e, String... args) {

		e.getTextChannel().sendTyping().queue();

		JDA jda = Matcher.getJda();

		final Integer[] length = {0,0};

		List<Guild> guilds = jda.getGuilds();
		int servers = guilds.size();

		jda.getGuilds().forEach(t -> {
			length[0] += t.getMemberCount();
			length[1] += t.getChannels().size();
		});

		String ping = TimerManager.toYYYYHHmmssS(jda.getGatewayPing());
		String uptime = TimerManager.toYYYYHHmmssS(System.currentTimeMillis() - Matcher.getFirstTime());

		Language lang = GuildInfo.getGuilds().get(e.getGuild()).getLang();

		EmbedBuilder eb = new EmbedBuilder();

		int mb = 1024 * 1024;

		long usedMemory = (AsyncInfoMonitor.getTotalMemory() - AsyncInfoMonitor.getFreeMemory()) / mb;
		long maxMemory = AsyncInfoMonitor.getMaxMemory() / mb;

		eb.setAuthor(lang.info_embed_author, "https://discord.gg/cuM6mvC", "https://imgur.com/LYje1SH.png");
		eb.setThumbnail("https://imgur.com/xo5U3p2.png");
		eb.setColor(Color.decode("#db9a27"));
		for (Field field : lang.info_embed_fields) {
			eb.addField(field.getTitle(), field.getDescription().replace("{INVITE}", "https://discord.com/oauth2/authorize?client_id=694992068589912084&permissions=355392&scope=bot").replace("{VOTE}", "https://top.gg/bot/694992068589912084/vote").replace("{DISCORD}", "https://discord.gg/cuM6mvC").replace("{DONATE}", "https://donatebot.io/checkout/701168663629267005").replace("{USERS}", nf.format(length[0])).replace("{GUILDS}", nf.format(servers)).replace("{CHANNELS}", nf.format(length[1])).replace("{UPTIME}", uptime).replace("{JDAVERSION}", "JDA 4.2.0_187").replace("{PING}", ping).replace("{DEV}", "Sauran #2820").replace("{CPU}", String.format("%.2f", AsyncInfoMonitor.getInstanceCPUUsage()) + "%").replace("{RAM}", usedMemory + "MB / " + maxMemory + "MB"), field.isInline());
		}

		e.getChannel().sendMessage(eb.build()).queue();
	}

}
