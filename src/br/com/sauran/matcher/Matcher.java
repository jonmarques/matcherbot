package br.com.sauran.matcher;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Timer;
import java.util.TimerTask;

import org.discordbots.api.client.DiscordBotListAPI;

import br.com.sauran.matcher.commands.CommandManager;
import br.com.sauran.matcher.data.Config;
import br.com.sauran.matcher.database.MongoDB;
import br.com.sauran.matcher.entities.GuildInfo;
import br.com.sauran.matcher.events.EventHandler;
import br.com.sauran.matcher.lang.LangManager;
import br.com.sauran.matcher.listener.GuildJoin;
import br.com.sauran.matcher.listener.GuildLeave;
import br.com.sauran.matcher.listener.MessageReaction;
import br.com.sauran.matcher.listener.matcher.MatchStatusListener;
import br.com.sauran.matcher.utils.AsyncInfoMonitor;
import br.com.sauran.matcher.utils.pandascore.Matches;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Matcher {

	private static JDA jda;
	private static EventHandler eh;

	private static long firstTime;

	private static Config config;
	
	private static MongoDB mongodb;
	private static Twitter twitter;
	private static Font font;

	public static void main(String[] args) throws Exception {

		firstTime = System.currentTimeMillis();

		AsyncInfoMonitor.start();

		eh = new EventHandler();

		config = new Config();
		new Matches();
		
		new LangManager();
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		font = Font.createFont(Font.TRUETYPE_FONT, Matcher.class.getResourceAsStream("/fonts/BebasNeue.ttf"));
		ge.registerFont(font);

		JDABuilder jdab = JDABuilder.createDefault(config.getBotToken());

		jdab.setStatus(OnlineStatus.ONLINE);
		jdab.setActivity(Activity.of(ActivityType.DEFAULT ,"\n" + config.getPrefix() + "help"));

		jdab.addEventListeners(new CommandManager());
		jdab.addEventListeners(new MessageReaction());
		jdab.addEventListeners(new GuildLeave());
		jdab.addEventListeners(new GuildJoin());

		jdab.setAutoReconnect(true);
		jda = jdab.build();
		jda.awaitReady();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("okhBNEHXgvi4lSKn0GfZQRmSv")
		.setOAuthConsumerSecret("pMPuKYpBh3nliX1JVeHLv4I6TRcTdhXDvJdfoxr6F5rT86LZdw")
		.setOAuthAccessToken("1284301152142741506-sruy7d6IlXa1TNvLRa8tbfP3Gh4YNq")
		.setOAuthAccessTokenSecret("nGQ0SO0MxMXZNuUill9ykyn8ujer4q5PmHLyzryBtNI0s");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();

		mongodb = new MongoDB();
		for (Guild guild : jda.getGuilds()) {
			GuildInfo gi = GuildInfo.getGuildInfo(guild);
			if (gi == null) new GuildInfo(guild, null, null, null, null, 0, false);
		}
		
		if (!config.isTest()) {

			eh.addListener(new MatchStatusListener());	
			
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {

					DiscordBotListAPI dblapi = new DiscordBotListAPI.Builder().token(config.getDiscordListToken()).botId(config.getBotId()).build();
					int serverCount = jda.getGuilds().size();
					dblapi.setStats(serverCount).whenComplete((a, b) -> {
						jda.getPresence().setActivity(Activity.of(ActivityType.DEFAULT, "\n" + config.getPrefix() + "help | " + serverCount + " guilds!"));
						System.out.println("Guild count stats sent! (" + serverCount + ")");
					});
				}
			};

			timer.scheduleAtFixedRate(task, 10 * 1000, 60 * 60 * 1000);
		}

	}

	
	public static Config getConfig() {
		return config;
	}
	
	public static EventHandler getEventHandler() {
		return eh;
	}

	public static JDA getJda() {
		return jda;
	}

	public static long getFirstTime() {
		return firstTime;
	}

	public static MongoDB getMongoDB() {
		return mongodb;
	}

	public static Twitter getTwitter() {
		return twitter;
	}

	public static Font getFont() {
		return font;
	}
}
