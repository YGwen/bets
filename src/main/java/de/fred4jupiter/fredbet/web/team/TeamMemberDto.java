package de.fred4jupiter.fredbet.web.team;

public class TeamMemberDto {

	private String userName;
	private Integer shirtNumber;
	private Integer points;
	private String cssRankClass = "label-default";

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getShirtNumber() {
		return shirtNumber;
	}

	public void setShirtNumber(Integer shirtNumber) {
		this.shirtNumber = shirtNumber;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Position getPosition() {
		return Position.from(shirtNumber);
	}

	public String getCssRankClass() {
		return cssRankClass;
	}

	public void setCssRankClass(String cssRankClass) {
		this.cssRankClass = cssRankClass;
	}
}