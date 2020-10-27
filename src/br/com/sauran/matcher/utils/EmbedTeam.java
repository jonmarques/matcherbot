package br.com.sauran.matcher.utils;

import br.com.sauran.matcher.entities.Team;
import net.dv8tion.jda.api.EmbedBuilder;

public class EmbedTeam {

	private EmbedBuilder eb;
	private Team team;

	public EmbedTeam(EmbedBuilder eb, Team team) {
		this.eb = eb;
		this.team = team;
	}
	
	public EmbedBuilder getEb() {
		return eb;
	}
	
	public Team getTeam() {
		return team;
	}
	
}
