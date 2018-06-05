package de.fred4jupiter.fredbet.service;

public class TeamAlreadyExistsException extends RuntimeException {

    public TeamAlreadyExistsException(String message) {
        super(message);
    }

}
