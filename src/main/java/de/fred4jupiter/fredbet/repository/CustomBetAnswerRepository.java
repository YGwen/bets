package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.CustomBetAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomBetAnswerRepository  extends JpaRepository<CustomBetAnswer,Long> {
}
