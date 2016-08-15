package br.concatto.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Sound {
	public Sound() {
		try {
			AudioFormat format = new AudioFormat(11025, 16, 1, true, true);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(new Info(SourceDataLine.class, format));
			
			line.open();
			line.start();
			
			byte[] bytes = new byte[20 * 11025];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) (128 + 127 * Math.sin(i));
			}
			
			while (true) {
				line.write(bytes, 0, bytes.length);
			}
			
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Sound();
	}
}
