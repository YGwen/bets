package de.fred4jupiter.fredbet.web.team;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.domain.TeamBuilder;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.RankingService;
import de.fred4jupiter.fredbet.service.TeamAlreadyExistsException;
import de.fred4jupiter.fredbet.service.TeamService;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/team")
public class TeamController {

	private static final String CREATE_TEAM_PAGE = "team/create";
	private static final String JOIN_TEAM_PAGE = "team/join";
	@Autowired
	private SecurityService securityService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private UserService userService;
	@Autowired
	private WebMessageUtil messageUtil;
	@Autowired
	private RankingService rankingService;

	@Autowired
	private Environment environment;

	@ModelAttribute("availableTeams")
	public List<Team> availableTeams() {
		return teamService.findAvailableTeams();
	}

	@ModelAttribute("availableShirtNumbers")
	public List<ShirtNumberDto> availableShirtNumbers() {
		return IntStream.rangeClosed(1, 11).boxed().map(ShirtNumberDto::new).collect(Collectors.toList());
	}

	@RequestMapping
	public ModelAndView list(ModelMap modelMap) {
		return queryTeam(modelMap);
	}

	private boolean userHasAlreadyTeam() {
		AppUser currentUser = securityService.getCurrentUser();
		return currentUser.getTeam() != null;
	}

	private ModelAndView queryTeam(ModelMap modelMap) {
		AppUser currentUser = securityService.getCurrentUser();

		if (currentUser.getTeam() == null) {
			messageUtil.addInfoMsg(modelMap, "team.noTeam");
			return new ModelAndView("team/list", "team", null);
		}

		Team team = teamService.findById(currentUser.getTeam().getId());
		TeamDetailDto teamDetailsDto = convert(team);

		teamDetailsDto.getMembers().sort(Comparator.comparing(TeamMemberDto::getPoints).reversed());

		for (TeamMemberDto memberDto : teamDetailsDto.getMembers().subList(0, 5)) {
			memberDto.setCssRankClass("label-success");
		}

		return new ModelAndView("team/list", "team", teamDetailsDto);
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(RedirectAttributes redirect) {

		boolean teamClosed = this.environment.getProperty("team.closed", Boolean.class);
		if (teamClosed) {
			messageUtil.addErrorMsg(redirect, "team.closed");
			return new ModelAndView("redirect:/team");
		}

		if (userHasAlreadyTeam()) {
			messageUtil.addErrorMsg(redirect, "user.has.already.team");
			return new ModelAndView("redirect:/team");
		}
		return new ModelAndView(CREATE_TEAM_PAGE, "createTeamCommand", new CreateTeamCommand());
	}


	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView create(@Valid CreateTeamCommand createTeamCommand, BindingResult bindingResult, RedirectAttributes redirect,
			ModelMap modelMap) {

		boolean teamClosed = this.environment.getProperty("team.closed", Boolean.class);
		if (teamClosed) {
			messageUtil.addErrorMsg(redirect, "team.closed");
			return new ModelAndView("redirect:/team");
		}

		if (bindingResult.hasErrors()) {
			return new ModelAndView(CREATE_TEAM_PAGE, "createTeamCommand", createTeamCommand);
		} else if (userHasAlreadyTeam()) {
			messageUtil.addErrorMsg(redirect, "user.has.already.team");
			return new ModelAndView("redirect:/team");
		}

		try {
			// create new team
			TeamBuilder teamBuilder = TeamBuilder.create()
					.withName(createTeamCommand.getTeamName())
					.withCaptain(securityService.getCurrentUser());
			Team team = teamService.createTeam(teamBuilder.build());
			securityService.getCurrentUser().setTeam(team);
			team.getMembers().add(securityService.getCurrentUser());
			userService.updateUser(securityService.getCurrentUser().getId(), team, createTeamCommand.getShirtNumber());

		}
		catch (TeamAlreadyExistsException e) {
			messageUtil.addErrorMsg(modelMap, "team.name.duplicate");
			return new ModelAndView(CREATE_TEAM_PAGE, "createTeamCommand", createTeamCommand);
		}

		messageUtil.addInfoMsg(redirect, "team.created", createTeamCommand.getTeamName());
		return new ModelAndView("redirect:/team");
	}

	@RequestMapping(value = "/join", method = RequestMethod.GET)
	public ModelAndView join(RedirectAttributes redirect) {

		boolean teamClosed = this.environment.getProperty("team.closed", Boolean.class);
		if (teamClosed) {
			messageUtil.addErrorMsg(redirect, "team.closed");
			return new ModelAndView("redirect:/team");
		}

		if (userHasAlreadyTeam()) {
			messageUtil.addErrorMsg(redirect, "user.has.already.team");
			return new ModelAndView("redirect:/team");
		} else if (availableTeams().isEmpty()) {
			messageUtil.addErrorMsg(redirect, "no.team.available");
			return new ModelAndView("redirect:/team");
		}
		return new ModelAndView(JOIN_TEAM_PAGE, "joinTeamCommand", new JoinTeamCommand());
	}

	@RequestMapping(value = "/join/{teamId}", method = RequestMethod.GET)
	public ModelAndView showTeamDetailsForJoin(Model model, @PathVariable("teamId") Long teamId) {
		Team selectedTeam = teamService.findById(teamId);
		model.addAttribute("selectedTeam", selectedTeam);
		return new ModelAndView("fragments/teamDetails :: teamDetails", "team", selectedTeam);
	}

	@RequestMapping(value = "/{teamId}", method = RequestMethod.GET)
	public ModelAndView showTeamDetails(@PathVariable("teamId") Long teamId) {
		Team team = teamService.findById(teamId);
		TeamDetailDto teamDetailsDto = convert(team);
		teamDetailsDto.getMembers().sort(Comparator.comparing(TeamMemberDto::getPoints).reversed());

		for (TeamMemberDto memberDto : teamDetailsDto.getMembers().subList(0, 5)) {
			memberDto.setCssRankClass("label-success");
		}

		return new ModelAndView("team/details", "team", teamDetailsDto);
	}

	@RequestMapping(value = "/assignedShirts/{teamId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Integer> getAssignedShirts(@PathVariable("teamId") Long teamId) {
		return teamService.findById(teamId).getMembers().stream().map(AppUser::getShirtNumber).collect(Collectors.toList());
	}

	@RequestMapping(value = "/join", method = RequestMethod.POST)
	public ModelAndView join(@Valid JoinTeamCommand joinTeamCommand, BindingResult bindingResult, RedirectAttributes redirect,
			ModelMap modelMap) {

		boolean teamClosed = this.environment.getProperty("team.closed", Boolean.class);
		if (teamClosed) {
			messageUtil.addErrorMsg(redirect, "team.closed");
			return new ModelAndView("redirect:/team");
		}

		if (bindingResult.hasErrors()) {
			return new ModelAndView(JOIN_TEAM_PAGE, "joinTeamCommand", joinTeamCommand);
		} else if (userHasAlreadyTeam()) {
			messageUtil.addErrorMsg(redirect, "user.has.already.team");
			return new ModelAndView("redirect:/team");
		}

		Team team = joinTeamCommand.getSelectedTeam();

		// Check team has less than 11 members
		if (!teamService.findAvailableTeams().contains(team)) {
			messageUtil.addErrorMsg(modelMap, "team.full", team.getName());
			return new ModelAndView(JOIN_TEAM_PAGE, "joinTeamCommand", joinTeamCommand);
		}

		// Save
		try {
			AppUser user = securityService.getCurrentUser();
			userService.updateUser(user.getId(), team, joinTeamCommand.getSelectedShirtNumber());
			user.setTeam(team);
			team.getMembers().add(user);
		}
		catch (DataIntegrityViolationException e) {
			messageUtil.addErrorMsg(modelMap, "shirt.number.already.assigned", joinTeamCommand.getSelectedShirtNumber(), team.getName());
			return new ModelAndView(JOIN_TEAM_PAGE, "joinTeamCommand", joinTeamCommand);
		}

		messageUtil.addInfoMsg(redirect, "team.joined", team.getName());
		return new ModelAndView("redirect:/team");
	}

	private TeamDetailDto convert(Team team) {

		List<UsernamePoints> usernamePoints = rankingService.calculateCurrentRanking(RankingSelection.INDIVIDUAL);

		TeamDetailDto teamDetailsDto = new TeamDetailDto();
		teamDetailsDto.setCaptainName(team.getCaptain().getUsername());
		teamDetailsDto.setName(team.getName());

		for (AppUser appUser : team.getMembers()) {
			TeamMemberDto member = new TeamMemberDto();
			member.setShirtNumber(appUser.getShirtNumber());
			member.setUserName(appUser.getUsername());
			member.setPoints(
					usernamePoints.stream().filter(u -> appUser.getUsername().equals(u.getUserName())).map(UsernamePoints::getTotalPoints)
							.findAny().orElse(0));
			teamDetailsDto.addMember(member);
		}

		return teamDetailsDto;

	}
}
