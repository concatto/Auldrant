package orionis.epsilon.symphonia.model;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Client {
	public static final AudioFormat FORMAT = new AudioFormat(44100.0F, 16, 2, true, true);
	private TargetDataLine recordLine;
	private Thread thread;
	private Socket socket;
	private OutputStream out;

	public Client() throws LineUnavailableException {
		recordLine = (TargetDataLine) AudioSystem.getLine(new Info(TargetDataLine.class));
		thread = new Thread(() -> {
			try {
				socket = new Socket("localhost", 4000);
				out = socket.getOutputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte[] data = new byte[8192];
			int read;
			while (true) {
				if (Thread.currentThread().isInterrupted()) break;
				read = recordLine.read(data, 0, data.length);
				try {
					out.write(data, 0, read);
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void start() throws LineUnavailableException {
		recordLine.open(FORMAT);
		recordLine.start();
		thread.start();
	}
	
	public void finish() {
		recordLine.close();
		recordLine.stop();
		thread.interrupt();
		try {
			socket.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write() throws IOException {
//		File target = new File("C:\\test.wav");
//		System.out.println(data.length);
//		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target));
//		out.write(data);
//		out.flush();
	}
}
