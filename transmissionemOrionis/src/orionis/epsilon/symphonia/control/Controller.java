package orionis.epsilon.symphonia.control;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;

import orionis.epsilon.symphonia.model.Client;
import orionis.epsilon.symphonia.model.Server;
import orionis.epsilon.symphonia.view.MainStage;

public class Controller extends Application {
	private Server server;
	private MainStage stage = new MainStage(this);
	private Client client;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		server = new Server(4000);
		client = new Client();
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public void startRecording() {
		try {
			client.start();
			try {
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void finishRecording() {
		client.finish();
		try {
			server.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeRecording() {
		try {
			client.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
