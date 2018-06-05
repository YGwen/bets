package de.fred4jupiter.fredbet.web.team;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import de.fred4jupiter.fredbet.domain.Team;

public class JoinTeamCommand {

	private Team selectedTeam;

	@Min(1)
	@Max(11)
	private int selectedShirtNumber;

	public Team getSelectedTeam() {
		return selectedTeam;
	}

	public int getSelectedShirtNumber() {
		return selectedShirtNumber;
	}

	public void setSelectedTeam(Team selectedTeam) {
		this.selectedTeam = selectedTeam;
	}

	public void setSelectedShirtNumber(int selectedShirtNumber) {
		this.selectedShirtNumber = selectedShirtNumber;
	}
}
