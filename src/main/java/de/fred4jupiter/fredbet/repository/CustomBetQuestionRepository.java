package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomBetQuestionRepository  extends JpaRepository<CustomBetQuestion,Long> {
    List<CustomBetQuestion> findAll();

    @Query("select c from CustomBetQuestion c order by c.kickOffDate desc")
    List<CustomBetQuestion> findAllOrderByKickOffDate();
}
