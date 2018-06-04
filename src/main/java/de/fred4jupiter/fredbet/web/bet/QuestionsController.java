package de.fred4jupiter.fredbet.web.bet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.CustomBetService;
import de.fred4jupiter.fredbet.service.QuestionsService;

@Controller
@RequestMapping("/questions")
public class QuestionsController {

	private static final Logger LOG = LoggerFactory.getLogger(QuestionsController.class);

	private static final String VIEW_LIST = "bet/questions";
	private static final String VIEW_BET= "bet/bet_question";
	private static final String EDIT_QUESTION = "bet/edit_question";

	@Autowired
	CustomBetService customBetService;

	@Autowired
	QuestionsService questionsService;

	@Autowired
	private SecurityService securityService;

	@ModelAttribute("customBetQuestionCommand")
	public CustomBetQuestionCommand customBetQuestionCommand() {
		return new CustomBetQuestionCommand();
	}

	@ModelAttribute("customBetCommand")
	public CustomBetCommand customBetCommand() {
		return new CustomBetCommand();
	}

	@RequestMapping("/open")
	public ModelAndView listStillOpen() {
		List<CustomBetQuestionCommand> questions = questionsService.findQuestionsToBet(securityService.getCurrentUserName());
		return new ModelAndView(VIEW_LIST, "customBetQuestionCommand", questions);
	}

	@RequestMapping("/bet/{id}")
	public ModelAndView bet(@PathVariable("id") Long questionId) {
		CustomBetCommand betCommand = questionsService.findByIdForBet(questionId, securityService.getCurrentUserName());
		return new ModelAndView(VIEW_BET, "customBetCommand", betCommand);
	}

	@RequestMapping(value = "/bet", method = RequestMethod.POST)
	public ModelAndView savebet(@Valid CustomBetCommand betCommand) {
		CustomBetQuestionCommand question = questionsService.findById(betCommand.getQuestionId());
		if(question.isBetable()) {
			questionsService.savebet(betCommand, securityService.getCurrentUserName());
		}
		return new ModelAndView("redirect:/questions/open");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_MATCH + "')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		CustomBetQuestionCommand customBetQuestionCommand = new CustomBetQuestionCommand();
		customBetQuestionCommand.setKickOffDate(LocalDateTime.now().plusHours(1));
		return new ModelAndView(EDIT_QUESTION, "customBetQuestionCommand", customBetQuestionCommand);
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") Long questionId) {
		CustomBetQuestionCommand customBetQuestionCommand = questionsService.findById(questionId);
		return new ModelAndView(EDIT_QUESTION, "customBetQuestionCommand", customBetQuestionCommand);
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView save(@Valid CustomBetQuestionCommand customBetQuestionCommand, BindingResult result, RedirectAttributes redirect,
			ModelMap modelMap, HttpServletRequest request) {
		for (ObjectError o : result.getAllErrors()) {
			LOG.info(o.toString());
		}

		if (result.hasErrors()) {
			return new ModelAndView(EDIT_QUESTION, "customBetQuestionCommand", customBetQuestionCommand);
		}

		String[] lastF = request.getParameterValues("lastField");
		List<String> answers = new ArrayList<>();
		if (lastF != null) {
			List<String> extraParams = Arrays.asList(lastF);
			int count = extraParams.size() + 1;
			for (int i = 2; i < count + 1; i++) {
				String answer = request.getParameter("p_new_" + String.valueOf(i));
				answers.add(answer);
			}
		}
		questionsService.save(customBetQuestionCommand, answers);

		LOG.info("Redirecting");
		return new ModelAndView("redirect:/questions/open");
	}

}
