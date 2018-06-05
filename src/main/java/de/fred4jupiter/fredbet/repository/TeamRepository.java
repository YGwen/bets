package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

	Team findByName(String name);

	@Query("select t from Team t where size(t.members) < 11")
	List<Team> findTeamsNotFull();

	@Query("select t from Team t join fetch t.members where t.id = :id")
	Team fetchTeamInfo(@Param("id") Long id);



}
