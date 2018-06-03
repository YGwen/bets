package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.CustomBetQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomBetQuestionRepository  extends JpaRepository<CustomBetQuestion,Long> {
    List<CustomBetQuestion> findAll();
}
