package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOM_BET_ANSWER")
public class CustomBetAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOM_BET_ANSWER_ID")
    private Long answerId;

    @Column(name = "CUSTOM_BET_QUESTION_ID")
    private Long questionId;

    @Column(name = "VALUE")
    private String value;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        CustomBetAnswer customBetAnswer = (CustomBetAnswer) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(answerId, customBetAnswer.answerId);
        builder.append(questionId, customBetAnswer.questionId);
        builder.append(value, customBetAnswer.value);
        return builder.isEquals();
    }


    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(answerId);
        builder.append(questionId);
        builder.append(value);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("answerId", answerId);
        builder.append("questionId", questionId);
        builder.append("value", value);
        return builder.toString();
    }
}
