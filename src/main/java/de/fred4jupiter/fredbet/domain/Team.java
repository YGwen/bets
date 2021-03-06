package de.fred4jupiter.fredbet.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.springframework.data.annotation.PersistenceConstructor;

import de.fred4jupiter.fredbet.repository.UsernamePoints;

@Entity
@Table(name = "TEAM")
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TEAM_ID")
	private Long id;

	@Column(name = "TEAM_NAME", unique = true, length = 30)
	private String name;

	@OneToOne
	@JoinColumn(name = "TEAM_CAPTAIN")
	private AppUser captain;

	@OneToMany(mappedBy = "team")
	@OrderBy("shirtNumber asc")
	private List<AppUser> members = new ArrayList<>();

	@PersistenceConstructor
	protected Team() {
		// for hibernate
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AppUser> getMembers() {
		return members;
	}

	public AppUser getCaptain() {
		return captain;
	}

	public void setCaptain(AppUser captain) {
		this.captain = captain;
	}

	@Override
	public String toString() {
		return String.format("%s (captain : %s)", name, captain.getUsername());
	}

	public Integer getTeamPoints(List<UsernamePoints> usernamePoints) {
		return usernamePoints.stream().sorted(Comparator.comparing(UsernamePoints::getTotalPoints).reversed()).limit(5)
				.mapToInt(UsernamePoints::getTotalPoints).sum();
	}
}
