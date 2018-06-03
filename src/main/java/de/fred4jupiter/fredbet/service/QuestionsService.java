package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.CustomBet;
import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.CustomBetAnswerRepository;
import de.fred4jupiter.fredbet.repository.CustomBetQuestionRepository;
import de.fred4jupiter.fredbet.repository.CustomBetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionsService {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionsService.class);

    @Autowired
    private CustomBetAnswerRepository customBetAnswerRepository;

    @Autowired
    private CustomBetQuestionRepository customBetQuestionRepository;

    @Autowired
    private CustomBetRepository customBetRepository;

    public List<CustomBetQuestion> findQuestionsToBet(String username) {
        List<CustomBet> userBets = customBetRepository.findByUserName(username);
        List<Long> questionIds = userBets.stream().map(customBet -> customBet.getAnswerId()).collect(Collectors.toList());

        List<CustomBetQuestion> questionsToAnswer = new ArrayList<>();
        List<CustomBetQuestion> allQuestions = customBetQuestionRepository.findAll();
        for (CustomBetQuestion question : allQuestions) {
            if (!questionIds.contains(question.getId()) && question.isBetable()) {
                questionsToAnswer.add(question);
            }
        }

        return questionsToAnswer;
    }
}
