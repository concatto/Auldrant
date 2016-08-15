package br.concatto.omegabeats;

public enum DrumElement {
	CRASH_CYMBAL(49),
	TOM(48),
	DRUM(36),
	SNARE(40),
	SPLASH_CYMBAL(55),
	HAND_CLAP(39),
	NONE(0),
	LOW_TOM(45);
	
	private int value;

	private DrumElement(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
