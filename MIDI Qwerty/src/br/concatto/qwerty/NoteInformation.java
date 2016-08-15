package br.concatto.qwerty;

public class NoteInformation {
	private int note;
	private int x;
	private int y;
	private double width;
	private double height;
	private int program;
	private int track;
	
	public NoteInformation(int note, int x, int y, double width, double height, int program, int track) {
		this.note = note;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.program = program;
		this.track = track;
	}
	
	public int getNote() {
		return note;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public int getProgram() {
		return program;
	}

	public int getTrack() {
		return track;
	}
}
