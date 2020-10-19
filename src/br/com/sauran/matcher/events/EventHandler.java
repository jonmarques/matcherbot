package br.com.sauran.matcher.events;

import java.util.HashSet;
import java.util.Set;

import br.com.sauran.matcher.events.event.MatchEvent;

public class EventHandler {

    private Set<ListenerAdapter> listeners;

    public EventHandler() {
		this.listeners =  new HashSet<ListenerAdapter>();
	}
    
    public synchronized void addListener(ListenerAdapter listener){
       listeners.add(listener);
    }

    public void notify(MatchEvent matchEvent){
       for (ListenerAdapter listeners : listeners) {
    	   try { 
    		   listeners.fireEvent(matchEvent);
    	   } catch (Throwable e) {
    		   e.printStackTrace();
    	   }
       }
    }
	
}
