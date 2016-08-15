package br.concatto.socket;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Control {
	private static final int PORT = 4000;
	private Server server;
	
	public Control() {
		try {
			launchServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void launchServer() throws IOException {
		server = new Server(PORT);
		server.onDataReceived((data, id) -> {
			for (byte b : data) {
				if (b == 1) {
					server.writeBack(id, new byte[]{1});
				}
			}
			String s = Arrays.stream(data)
					.map(b -> String.format("%d (%c)", b & 0xFF, b & 0xFF))
					.collect(Collectors.joining(", "));
			
			System.out.println("Data received: " + s);
		});
		
		new Thread(() -> {
			try {
				server.begin();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public static void main(String[] args) throws IOException {
//		new Update("Prime", PORT, "Simples", new Commands().add(12, "Quebra o HD").add(30, "Abre o Grand Synth")).push();
		new Control();
	}
}
