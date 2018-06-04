package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.CustomBet;
import de.fred4jupiter.fredbet.domain.CustomBetAnswer;
import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import de.fred4jupiter.fredbet.repository.CustomBetAnswerRepository;
import de.fred4jupiter.fredbet.repository.CustomBetQuestionRepository;
import de.fred4jupiter.fredbet.repository.CustomBetRepository;
import de.fred4jupiter.fredbet.web.bet.CustomBetCommand;
import de.fred4jupiter.fredbet.web.bet.CustomBetQuestionCommand;

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

	public List<CustomBetQuestionCommand> findQuestionsToBet(String username) {
		List<CustomBetQuestion> allQuestions = customBetQuestionRepository.findAllOrderByKickOffDate();
		List<CustomBetQuestionCommand> customBetQuestionCommands = new ArrayList<>();
		for (CustomBetQuestion question : allQuestions) {
			CustomBetQuestionCommand customBetQuestionCommand = toCustomBetQuestionCommand(question);
			if (question.getAnswerId() != null) {
				CustomBetAnswer customBetAnswer = customBetAnswerRepository.getOne(question.getAnswerId());
				customBetQuestionCommand.setAnswer(customBetAnswer.getValue());
			} else {
				customBetQuestionCommand.setAnswer("-");
			}
			CustomBetAnswer userAnswer = customBetAnswerRepository.findAnswer(username, question.getQuestionId());
			if (userAnswer != null) {
				customBetQuestionCommand.setUserAnswer(userAnswer.getValue());
			} else {
				customBetQuestionCommand.setUserAnswer("-");
			}
			customBetQuestionCommands.add(customBetQuestionCommand);
		}
		return customBetQuestionCommands;
	}

	public CustomBetQuestionCommand findById(Long id) {
		CustomBetQuestion question = customBetQuestionRepository.getOne(id);
		List<CustomBetAnswer> answers = customBetAnswerRepository.findByIdForBet(id);
		CustomBetQuestionCommand command = toCustomBetQuestionCommand(question);
		command.setAnswers(answers);
		return command;
	}

	public CustomBetCommand findByIdForBet(Long id, String username) {
		CustomBetQuestion question = customBetQuestionRepository.getOne(id);
		List<CustomBetAnswer> answers = customBetAnswerRepository.findByIdForBet(id);
		CustomBet bet = customBetRepository.findByQuestionIdAndUserNameForBet(username, id);
		CustomBetCommand command = toCustomBetCommand(question, answers, bet);
		return command;
	}

	public void save(CustomBetQuestionCommand command, List<String> answers) {
		CustomBetQuestion customBetQuestion = toCustomBetQuestion(command);
		CustomBetQuestion savedEntity = customBetQuestionRepository.save(customBetQuestion);
		for (String answer : answers) {
			CustomBetAnswer customBetAnswer = new CustomBetAnswer();
			customBetAnswer.setQuestionId(savedEntity.getQuestionId());
			customBetAnswer.setValue(answer);
			customBetAnswerRepository.save(customBetAnswer);
		}
		LOG.info("Saved successfully");
	}

	public void savebet(CustomBetCommand command, String userName){
		CustomBet customBet = toCustomBet(command, userName);
		customBetRepository.save(customBet);
	}

	private CustomBet toCustomBet(CustomBetCommand command, String username){
		CustomBet bet = new CustomBet();
		bet.setAnswerId(command.getAnswerId());
		bet.setId(command.getBetId());
		bet.setQuestionId(command.getQuestionId());
		bet.setUserName(username);
		return bet;
	}

	private CustomBetQuestion toCustomBetQuestion(CustomBetQuestionCommand command) {
		CustomBetQuestion customBetQuestion = new CustomBetQuestion();
		customBetQuestion.setQuestionId(command.getQuestionId());
		customBetQuestion.setAnswerId(command.getAnswerId());
		customBetQuestion.setKickOffDate(command.getKickOffDate());
		customBetQuestion.setPoints(command.getPoints());
		customBetQuestion.setValue(command.getValue());
		return customBetQuestion;
	}

	private CustomBetQuestionCommand toCustomBetQuestionCommand(CustomBetQuestion question) {
		CustomBetQuestionCommand command = new CustomBetQuestionCommand();
		command.setKickOffDate(question.getKickOffDate());
		command.setPoints(question.getPoints());
		command.setValue(question.getValue());
		command.setQuestionId(question.getQuestionId());
		command.setAnswerId(question.getAnswerId());
		return command;
	}

	private CustomBetCommand toCustomBetCommand(CustomBetQuestion question, List<CustomBetAnswer> answers, CustomBet customBet){
		CustomBetCommand command = new CustomBetCommand();
		if (customBet != null) {
			command.setAnswerId(customBet.getAnswerId());
			command.setBetId(customBet.getId());
		}
		command.setAnswers(answers);
		command.setQuestionId(question.getQuestionId());
		command.setQuestion(question.getValue());
		return command;
	}
}
