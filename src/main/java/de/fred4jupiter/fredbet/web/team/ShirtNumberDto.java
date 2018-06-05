package de.fred4jupiter.fredbet.web.team;

public class ShirtNumberDto {

	private int number;
	private Position position;

	public ShirtNumberDto(int number) {
		this.number = number;
		this.position = Position.from(number);
	}

	public String getOptionText() {
		return String.format("%d - %s", number, position);
	}

	public Position getPosition() {
		return position;
	}

	public int getNumber() {
		return number;
	}

}
