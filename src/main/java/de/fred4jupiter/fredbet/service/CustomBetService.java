package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.repository.CustomBetAnswerRepository;
import de.fred4jupiter.fredbet.repository.CustomBetQuestionRepository;
import de.fred4jupiter.fredbet.repository.CustomBetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomBetService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomBetService.class);

    @Autowired
    private CustomBetRepository customBetRepository;

    @Autowired
    private CustomBetAnswerRepository customBetAnswerRepository;

    @Autowired
    private CustomBetQuestionRepository customBetQuestionRepository;


}
