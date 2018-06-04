package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOM_BET_QUESTION")
public class CustomBetQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOM_BET_QUESTION_ID")
    private Long questionId;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "POINTS")
    private Long points;

    @Column(name= "ANSWER_ID")
    private Long answerId;

    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
    @Column(name = "KICK_OFF_DATE")
    private LocalDateTime kickOffDate;


    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(kickOffDate);
    }

    public boolean hasResultSet() {
        return answerId!=null;
    }

    public boolean isBetable() {
        return !hasStarted() && !hasResultSet();
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

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        CustomBetQuestion customBetQuestion = (CustomBetQuestion) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(questionId, customBetQuestion.questionId);
        builder.append(value, customBetQuestion.value);
        builder.append(points, customBetQuestion.points);
        builder.append(kickOffDate, customBetQuestion.kickOffDate);
        return builder.isEquals();
    }


    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(questionId);
        builder.append(value);
        builder.append(points);
        builder.append(kickOffDate);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("questionId", questionId);
        builder.append("userName", value);
        builder.append("points", points);
        builder.append("kickOffDate", kickOffDate);
        return builder.toString();
    }
}
