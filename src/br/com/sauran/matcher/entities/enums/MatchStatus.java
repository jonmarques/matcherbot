package br.com.sauran.matcher.entities.enums;

public enum MatchStatus {

	SOON(":white_large_square:"), INGAME(":red_square:"), FINISHED(":green_square:"), CANCELED(":yellow_square:");

	private final String emote;

	MatchStatus(String emote) {
		this.emote = emote;
	}

	public String getEmote() {
		return emote;
	}

	public static MatchStatus getByName(String name) {
		switch (name.toLowerCase()) {
		case "canceled":
			return MatchStatus.CANCELED;
		case "finished":
			return MatchStatus.FINISHED;
		case "not_started":
			return MatchStatus.SOON;
		case "running":
			return MatchStatus.INGAME;
		case "postponed":
			return MatchStatus.CANCELED;
		}
		return null;
	}


}
