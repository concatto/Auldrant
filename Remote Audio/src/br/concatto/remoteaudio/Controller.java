package br.concatto.remoteaudio;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Controller {
	public Controller() {
		AudioFormat format = new AudioFormat(8000, 8, 2, true, false);
		
		try {
			TargetDataLine line = AudioSystem.getTargetDataLine(format);
			line.open(format, 32768);
			line.start();
			
			ServerSocket server = new ServerSocket(4000);
			
			CompletableFuture.runAsync(new Receiver());
			
			Socket s = server.accept();
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			
			int read;
			byte[] data = new byte[256];
			while (true) {
				read = line.read(data, 0, data.length);
				out.write(data, 0, read);
				out.flush();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Controller();
	}
}
