package de.fred4jupiter.fredbet.web.team;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailDto {

	private String name;
	private String captainName;
	private List<TeamMemberDto> members = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCaptainName() {
		return captainName;
	}

	public void setCaptainName(String captainName) {
		this.captainName = captainName;
	}

	public List<TeamMemberDto> getMembers() {
		return members;
	}

	public void addMember(TeamMemberDto member) {
		members.add(member);
	}


}
