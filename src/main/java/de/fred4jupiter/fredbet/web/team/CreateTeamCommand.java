package de.fred4jupiter.fredbet.web.team;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateTeamCommand {

	@NotEmpty
	@Size(min = 2, max = 30)
	private String teamName;

	@NotNull
	@Min(1)
	@Max(11)
	private Integer shirtNumber;

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Integer getShirtNumber() {
		return shirtNumber;
	}

	public void setShirtNumber(Integer shirtNumber) {
		this.shirtNumber = shirtNumber;
	}
}
