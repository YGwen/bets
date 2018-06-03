package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.CustomBet;
import de.fred4jupiter.fredbet.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomBetRepository extends JpaRepository<CustomBet,Long> {
    List<CustomBet> findByUserName(String currentUsername);

    List<Bet> findByQuestionId(Long answerId);
}
