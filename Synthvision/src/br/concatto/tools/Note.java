package br.concatto.tools;

import java.util.Objects;

public class Note {
	private static final char[] NOTES = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};
	private static int noteShift = -1;
	private static int octaveShift;
	private static int startingOctave;
	private Hue hue;
	private int index;
	private boolean sharp;
	
	public Note(Hue hue, int index, boolean sharp) {
		if (noteShift == -1) throw new IllegalStateException("Use defineFirstNote() before instantiating.");
		
		this.hue = hue;
		this.index = index;
		this.sharp = sharp;
	}

	public Hue getHue() {
		return hue;
	}

	public int getIndex() {
		return index;
	}

	public boolean isSharp() {
		return sharp;
	}
	
	public String getNote() {
		int noteIndex = noteShift + index % NOTES.length;
		int octave = index + octaveShift;
		octave = (int) (octave > 0 ? Math.ceil(octave / NOTES.length) : Math.floor(octave / NOTES.length));
		octave += startingOctave + 1;
		
		StringBuilder b = new StringBuilder();
		b.append(Character.toUpperCase(NOTES[noteIndex]));
		if (sharp) b.append("#");
		b.append(octave);
		return b.toString();
	}
	
	public static void defineFirstNote(char note, int octave) {
		int index = -1;
		while (NOTES[++index] != note);
		noteShift = index;
		octaveShift = noteShift - 2;
		startingOctave = octave;
	}
	
	@Override
	public String toString() {
		return String.format("[Note = %s, Hue = %s]", getNote(), hue);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getNote(), hue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Note) {
			Note other = (Note) obj;
			return this.hue == other.hue && this.getNote().equals(other.getNote());
		}
		return false;
	}
}