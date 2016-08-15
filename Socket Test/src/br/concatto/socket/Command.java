package br.concatto.socket;

public class Command {
	private int code;
	private String description;

	public Command(int code, String description) {
		if (code < 0 || code > 255) throw new IllegalArgumentException("Code must be a byte. [0, 255]");
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
