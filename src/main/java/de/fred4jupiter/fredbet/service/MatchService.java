package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.repository.TeamRepository;
import de.fred4jupiter.fredbet.web.MatchCommand;

@Service
public class MatchService {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private TeamRepository teamRepository;

	public List<Match> findAll() {
		return matchRepository.findAll();
	}

	public MatchCommand findByMatchId(String matchId) {
		Match match = matchRepository.findOne(matchId);
		return toMatchCommand(match);
	}
	
	public Match findMatchByMatchId(String matchId) {
		return matchRepository.findOne(matchId);
	}

	private MatchCommand toMatchCommand(Match match) {
		MatchCommand matchCommand = new MatchCommand();
		matchCommand.setMatchId(match.getId());
		matchCommand.setTeamNameOne(match.getTeamOne().getName());
		matchCommand.setTeamNameTwo(match.getTeamTwo().getName());
		matchCommand.setTeamResultOne(match.getGoalsTeamOne());
		matchCommand.setTeamResultTwo(match.getGoalsTeamTwo());
		return matchCommand;
	}

	public Match createAndSaveMatch(String teamNameOne, String teamNameTwo, Integer goalsTeamOne, Integer goalsTeamTwo) {
		return createAndSaveMatch(teamNameOne, teamNameTwo, goalsTeamOne, goalsTeamTwo, null);
	}
	
	public Match createAndSaveMatch(String teamNameOne, String teamNameTwo, Integer goalsTeamOne, Integer goalsTeamTwo, String group) {
		Team teamOne = findOrCreate(teamNameOne);
		Team teamTwo = findOrCreate(teamNameTwo);
		
		Match match = new Match(teamOne, teamTwo);
		match.setGoalsTeamOne(goalsTeamOne);
		match.setGoalsTeamTwo(goalsTeamTwo);
		if (group != null) {
			match.setGroup(group);
		}
		
		match = matchRepository.save(match);
		return match;
	}

	private Team findOrCreate(String teamName) {
		Team team = teamRepository.findByName(teamName);
		if (team == null) {
			team = new Team(teamName);
		}

		return teamRepository.save(team);
	}

	public String save(MatchCommand matchCommand) {
		Match match = matchRepository.findOne(matchCommand.getMatchId());
		if (match == null) {
			match = new Match(matchCommand.getTeamNameOne(), matchCommand.getTeamNameTwo());
		} else {
			match.getTeamOne().setName(matchCommand.getTeamNameOne());
			match.getTeamTwo().setName(matchCommand.getTeamNameTwo());
		}

		match.setGoalsTeamOne(matchCommand.getTeamResultOne());
		match.setGoalsTeamTwo(matchCommand.getTeamResultTwo());

		match = this.matchRepository.save(match);
		matchCommand.setMatchId(match.getId());
		return match.getId();
	}

}