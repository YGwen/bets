package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOM_BET")
public class CustomBet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOM_BET_ID")
    private Long id;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "CUSTOM_BET_QUESTION_ID")
    private Long questionId;

    @Column(name = "CUSTOM_BET_ANSWER_ID")
    private Long answerId;

    @Column(name = "POINTS")
    private Integer points;

    public CustomBet(String userName, Long questionId, Long answerId) {
        this.userName = userName;
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        CustomBet customBet = (CustomBet) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(id, customBet.id);
        builder.append(userName, customBet.userName);
        builder.append(questionId, customBet.questionId);
        builder.append(answerId, customBet.answerId);
        return builder.isEquals();
    }


    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(id);
        builder.append(userName);
        builder.append(questionId);
        builder.append(answerId);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("userName", userName);
        builder.append("questionId", questionId);
        builder.append("answerId", answerId);
        return builder.toString();
    }
}
