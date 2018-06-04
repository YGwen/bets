package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.CustomBetAnswer;

public interface CustomBetAnswerRepository extends JpaRepository<CustomBetAnswer, Long> {

	@Query("select a from CustomBetAnswer a join CustomBet b on (b.answerId=a.answerId) where b.userName=:userName and a.questionId=:questionId")
	CustomBetAnswer findAnswer(@Param("userName") String userName, @Param("questionId") Long questionId);

	@Query("select a from CustomBetAnswer a where a.questionId=:questionId")
	List<CustomBetAnswer> findByIdForBet(@Param("questionId") Long questionId);
}
