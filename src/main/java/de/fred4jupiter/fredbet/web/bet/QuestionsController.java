package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.CustomBetService;
import de.fred4jupiter.fredbet.service.QuestionsService;
import de.fred4jupiter.fredbet.web.matches.CreateEditMatchCommand;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.print.attribute.standard.RequestingUserName;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/questions")
public class QuestionsController {

    private static final String VIEW_LIST = "bet/questions";
    private static final String EDIT_QUESTION = "bet/edit_question";

    @Autowired
    CustomBetService customBetService;

    @Autowired
    QuestionsService questionsService;

    @Autowired
    private SecurityService securityService;

    @RequestMapping("/open")
    public ModelAndView listStillOpen(ModelMap modelMap){
        List<CustomBetQuestion> questions = questionsService.findQuestionsToBet(securityService.getCurrentUserName());
        return new ModelAndView(VIEW_LIST, "questionsToAnswer", questions);
    }


    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_MATCH + "')")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create() {
        return EDIT_QUESTION;
    }
//
//
//    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    public ModelAndView edit(@PathVariable("id") Long matchId) {
//        Match match = matchService.findByMatchId(matchId);
//
//        Long numberOfBetsForThisMatch = bettingService.countByMatch(match);
//        CreateEditMatchCommand createEditMatchCommand = toCreateEditMatchCommand(match);
//        if (numberOfBetsForThisMatch == 0) {
//            createEditMatchCommand.setDeletable(true);
//        }
//
//        return new ModelAndView(VIEW_EDIT_MATCH, "createEditMatchCommand", createEditMatchCommand);
//    }
//
//
    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH + "')")
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView save(@Valid CreateEditMatchCommand createEditMatchCommand, BindingResult result, RedirectAttributes redirect,
                             ModelMap modelMap) {
        if (result.hasErrors()) {
            return new ModelAndView(VIEW_EDIT_MATCH, "createEditMatchCommand", createEditMatchCommand);
        }

        save(createEditMatchCommand);

        if (createEditMatchCommand.getMatchId() == null) {
            messageUtil.addInfoMsg(redirect, "msg.match.created", createEditMatchCommand.getTeamNameOne(),
                    createEditMatchCommand.getTeamNameTwo());
        } else {
            messageUtil.addInfoMsg(redirect, "msg.match.updated", createEditMatchCommand.getTeamNameOne(),
                    createEditMatchCommand.getTeamNameTwo());
        }

        return new ModelAndView("redirect:/matches#" + createEditMatchCommand.getMatchId());
    }


}
