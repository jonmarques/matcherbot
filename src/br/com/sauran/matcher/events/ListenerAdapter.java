package br.com.sauran.matcher.events;

import br.com.sauran.matcher.events.event.MatchEvent;

public abstract class ListenerAdapter {

	public void onEvent(MatchEvent e) {};

	public void fireEvent(MatchEvent matchEvent) {
		onEvent(matchEvent);
	}
	
}
