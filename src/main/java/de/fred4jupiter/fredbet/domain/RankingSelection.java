package de.fred4jupiter.fredbet.domain;

public enum RankingSelection {

	INDIVIDUAL("individual"),

	TEAM("team");

	private String mode;

	RankingSelection(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public static RankingSelection fromMode(String mode) {
		for (RankingSelection rankingSelection : values()) {
			if (rankingSelection.getMode().equals(mode)) {
				return rankingSelection;
			}
		}
		
		throw new IllegalArgumentException("Unsupported mode "+mode);
	}
}
