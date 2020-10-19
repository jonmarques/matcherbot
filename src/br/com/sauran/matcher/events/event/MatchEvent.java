package br.com.sauran.matcher.events.event;

import javax.annotation.Nonnull;

import br.com.sauran.matcher.entities.Match;

public class MatchEvent {

	private Match match;
	private EventType type;

	public MatchEvent(@Nonnull Match match, EventType type) {
		this.match = match;
		this.type = type;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public EventType getType() {
		return type;
	}

}
