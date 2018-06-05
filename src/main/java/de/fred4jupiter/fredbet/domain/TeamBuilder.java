package de.fred4jupiter.fredbet.domain;

/**
 * Builder pattern for teams.
 * 
 * @author cmenetri
 *
 */
public class TeamBuilder {

	private final Team team;

	private TeamBuilder() {
		this.team = new Team();
	}

	public static TeamBuilder create() {
		return new TeamBuilder();
	}

	public TeamBuilder withName(String name) {
		this.team.setName(name);
		return this;
	}

	public TeamBuilder withCaptain(AppUser captain) {
		this.team.setCaptain(captain);
		return this;
	}

	public Team build() {
		return this.team;
	}
}
