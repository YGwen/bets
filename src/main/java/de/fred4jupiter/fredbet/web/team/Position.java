package de.fred4jupiter.fredbet.web.team;

public enum Position {

	Goalkeeper, Defender, Midfielder, Striker;

	public static Position from(int number) {
		if (number == 1) {
			return Goalkeeper;
		} else if (number <= 5) {
			return Defender;
		} else if (number <= 8) {
			return Midfielder;
		} else {
			return Striker;
		}
	}

}
