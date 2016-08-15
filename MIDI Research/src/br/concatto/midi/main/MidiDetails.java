package br.concatto.midi.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MidiDetails {
	private int microsecondsPerQuarter;
	private int signatureNumerator;
	private int signatureDenominator;
	private int clocks;
	private int notesPerBeat;
	
	private MidiDetails(int mpq, int signatureNumerator, int signatureDenominator, int clocks, int notesPerBeat) {
		this.microsecondsPerQuarter = mpq;
		this.signatureNumerator = signatureNumerator;
		this.signatureDenominator = signatureDenominator;
		this.clocks = clocks;
		this.notesPerBeat = notesPerBeat;
	}
	
	public int getMicrosecondsPerQuarter() {
		return microsecondsPerQuarter;
	}
	
	public int getClocks() {
		return clocks;
	}
	
	public int getNotesPerBeat() {
		return notesPerBeat;
	}
	
	public int getSignatureDenominator() {
		return signatureDenominator;
	}
	
	public int getSignatureNumerator() {
		return signatureNumerator;
	}
	
	public int getBeatsPerMinute() {
		return Math.round(60000000 / microsecondsPerQuarter);
	}
	
	public static MidiDetails analyze(File midiFile) {
		int microseconds = 0;
		int signatureNumerator = 0;
		int signatureDenominator = 0;
		int clocks = 0;
		int notesPerBeat = 0;
		
		
		try (FileInputStream in = new FileInputStream(midiFile)) {
			boolean willBeMeta = false;
			int length = 0;
			int b;
			MetaEvent event = null;
			
			/* A ordem dos ifs é extremamente importante! */
			while ((b = in.read()) != -1) {
				if (event != null) {
					if (length > 0) {
						switch (event) {
						case TEMPO:
							microseconds += b * Math.pow(16, 2 * (length - 1));
							break;
						case SIGNATURE:
							if (length == 4) {
								signatureNumerator = b;
							} else if (length == 3) {
								signatureDenominator = (int) Math.pow(2, b);
							} else if (length == 2) {
								clocks = b;
							} else {
								notesPerBeat = b;
							}
							break;
						}
						
						length--;
						if (length == 0) event = null;
					} else {
						length = b;
					}
				}
				
				if (willBeMeta) {
					if (b == 81) {
						event = MetaEvent.TEMPO;
						microseconds = 0;
					} else if (b == 88) {
						event = MetaEvent.SIGNATURE;
					} else {
						event = null;
					}
				}
				
				/* Leitura de 255 simboliza um Meta Event */
				willBeMeta = (b == 255);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new MidiDetails(microseconds, signatureNumerator, signatureDenominator, clocks, notesPerBeat);
	}
}
