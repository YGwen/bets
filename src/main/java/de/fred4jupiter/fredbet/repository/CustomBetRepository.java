package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.CustomBet;
import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import de.fred4jupiter.fredbet.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomBetRepository extends JpaRepository<CustomBet,Long> {
    List<CustomBet> findByUserName(String currentUsername);

    List<CustomBet> findByQuestionId(Long answerId);

    @Query("select b from CustomBet b where b.questionId=:questionId and b.userName=:userName")
    CustomBet findByQuestionIdAndUserNameForBet(@Param("userName") String userName, @Param("questionId") Long questionId);
}
