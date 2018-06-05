package de.fred4jupiter.fredbet.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.TeamRepository;

@Service
@Transactional
public class TeamService {

	@Autowired
	private TeamRepository teamRepository;

	public List<Team> findAll() {
		return teamRepository.findAll();
	}

	public List<Team> findAvailableTeams() {
		return teamRepository.findTeamsNotFull();
	}

	public Team findById(Long teamId) {
		return teamRepository.fetchTeamInfo(teamId);
	}

	public Team createTeam(Team team) throws TeamAlreadyExistsException {
		Team foundTeam = teamRepository.findByName(team.getName());
		if (foundTeam != null) {
			throw new TeamAlreadyExistsException("Team with name=" + team.getName() + " already exists.");
		}

		return teamRepository.save(team);
	}

}
