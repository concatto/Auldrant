package br.concatto.remoteaudio;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Receiver implements Runnable {
	@Override
	public void run() {
		try {
			Socket s = new Socket("localhost", 4000);
			AudioFormat format = new AudioFormat(8000, 8, 2, true, false);
			
			SourceDataLine line = AudioSystem.getSourceDataLine(format);
			line.open(format, 32768);
			line.start();
			
			DataInputStream in = new DataInputStream(s.getInputStream());
			byte[] data = new byte[256];
			int read;
			while (true) {
				read = in.read(data);
				line.write(data, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
