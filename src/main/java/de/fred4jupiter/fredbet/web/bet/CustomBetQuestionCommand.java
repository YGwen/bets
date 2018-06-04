package de.fred4jupiter.fredbet.web.bet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

public class CustomBetQuestionCommand {

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";

    private Long questionId;

    @NotNull
    private String value;

    @NotNull
    @Min(value = 1)
    private Long points;

    @NotNull
    private LocalDateTime kickOffDate;

    private Long answerId;

    private String answer;

    private String userAnswer;

    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(kickOffDate);
    }

    public boolean hasResultSet() {
        return answerId!=null;
    }

    public boolean isBetable() {
        return !hasStarted() && !hasResultSet();
    }

    public String getKickOffDateString() {
        if (kickOffDate == null) {
            return "";
        }
        return kickOffDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
    }

    public void setKickOffDateString(String kickOffDateString) {
        if (StringUtils.isBlank(kickOffDateString)) {
            return;
        }
        this.kickOffDate = LocalDateTime.parse(kickOffDateString, DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public LocalDateTime getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(LocalDateTime kickOffDate) {
        this.kickOffDate = kickOffDate;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
}
