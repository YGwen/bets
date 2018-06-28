package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import javax.persistence.Tuple;

public interface CustomBetQuestionRepository  extends JpaRepository<CustomBetQuestion,Long> {
    List<CustomBetQuestion> findAll();

    @Query("select c from CustomBetQuestion c order by c.kickOffDate desc")
    List<CustomBetQuestion> findAllOrderByKickOffDate();

    @Query(nativeQuery = true, value = "select user_name, sum(q.points) from custom_bet_question q join custom_bet b "
            + "on b.custom_bet_question_id=q.custom_bet_question_id where b.custom_bet_answer_id=q.answer_id "
            + "group by user_name ")
    List<Object[]> getBonusPoints();
}
