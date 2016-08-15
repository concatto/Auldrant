package orionis.epsilon.symphonia.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Server {
	private ServerSocket socket;
	private SourceDataLine player;
	private Thread thread;
	
	public Server(int port) throws IOException, LineUnavailableException {
		player = (SourceDataLine) AudioSystem.getLine(new Info(SourceDataLine.class));
		socket = new ServerSocket(port);
		thread = new Thread(() -> {
			try {
				Socket s = socket.accept();
				InputStream in = s.getInputStream();
				byte[] data = new byte[8192];
				int read;
				while (true) {
					if (Thread.currentThread().isInterrupted()) break;
					read = in.read(data, 0, data.length);
					player.write(data, 0, read);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public void start() throws IOException, LineUnavailableException {
		player.open(Client.FORMAT);
		player.start();
		thread.start();
	}
	
	public void finish() throws IOException {
		thread.interrupt();
		player.close();
		player.stop();
		socket.close();
	}
}
