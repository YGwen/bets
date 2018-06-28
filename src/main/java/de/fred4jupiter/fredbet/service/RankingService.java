package de.fred4jupiter.fredbet.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.UsernamePoints;

@Service
public class RankingService {

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	public List<UsernamePoints> calculateCurrentRanking(RankingSelection rankingSelection) {

		final List<UsernamePoints> ranking = betRepository.calculateRanging();

		if (RankingSelection.INDIVIDUAL.equals(rankingSelection)) {
			return ranking;
		} else if (RankingSelection.TEAM.equals(rankingSelection)) {

			// Find all team users
			List<AppUser> appUsers = appUserRepository.fetchTeamForAllUsers();
			Map<String, Team> mapUserNameTeam =
					appUsers.stream().filter(u -> u.getTeam() != null).collect(Collectors.toMap(AppUser::getUsername, AppUser::getTeam));

			Map<Team, List<UsernamePoints>> groupByTeam = ranking.stream()
					.filter(up -> mapUserNameTeam.containsKey(up.getUserName()))
					.collect(Collectors.groupingBy(up -> mapUserNameTeam.get(up.getUserName())));

			return groupByTeam.entrySet().stream()
					.sorted(Comparator.comparing((Map.Entry<Team, List<UsernamePoints>> e) -> e.getKey().getTeamPoints(e.getValue())).reversed())
					.map(this::convertToTeamRanking)
					.collect(Collectors.toList());

		} else {
			throw new IllegalArgumentException("Unsupported ranking selection " + rankingSelection);
		}
	}

	private UsernamePoints convertToTeamRanking(Map.Entry<Team, List<UsernamePoints>> teamListEntry) {

		UsernamePoints teamPoints = new UsernamePoints();
		teamPoints.setUserName(teamListEntry.getKey().getName());
		teamPoints.setTotalPoints(teamListEntry.getKey().getTeamPoints(teamListEntry.getValue()));
		teamPoints.setTeamId(teamListEntry.getKey().getId());
		return teamPoints;
	}

}
