package de.fred4jupiter.fredbet.web.bet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.fred4jupiter.fredbet.domain.CustomBetAnswer;

public class CustomBetCommand {

	private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";

	private LocalDateTime kickOffDate;

	private String question;

	private Long questionId;

	private Long answerId;

	private List<CustomBetAnswer> answers;

	private Long betId;

	public LocalDateTime getKickOffDate() {
		return kickOffDate;
	}

	public void setKickOffDate(LocalDateTime kickOffDate) {
		this.kickOffDate = kickOffDate;
	}

	public String getKickOffDateString() {
		if (kickOffDate == null) {
			return "";
		}
		return kickOffDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
	}

	public String getQuestion() {
		return question;
	}

	public Long getBetId() {
		return betId;
	}

	public void setBetId(Long betId) {
		this.betId = betId;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}

	public List<CustomBetAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<CustomBetAnswer> answers) {
		this.answers = answers;
	}
}
