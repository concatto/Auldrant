package orionis.zeta.nexus.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Controller extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerSocket server = new ServerSocket(2000);
		ListView<Socket> sockets = new ListView<>();
		
		Runnable listener = () -> {
			while (true) {
				try {
					Socket socket = server.accept();
					Platform.runLater(() -> sockets.getItems().add(socket));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		new Thread(listener).start();
		
		Button connect = new Button("Conectar");
		connect.setOnAction(e -> {		
			try {
				Socket s = new Socket("localhost", 2000);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				Runnable reader = () -> {
					while (true) {
						try {
							String line = in.readLine();
							if (line != null) {
								System.out.println(String.format("Eu sou o socket %s e recebi esta mensagem: \"%s\"", s, line));
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				};
				new Thread(reader).start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		Button write = new Button("Escrever");
		write.setOnAction(e -> {
			Socket s = sockets.getSelectionModel().getSelectedItem();
			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				out.write("Hello brother\n");
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		TextField vanish = new TextField();
		Label full = new Label("Campo bom", vanish);
		full.setContentDisplay(ContentDisplay.BOTTOM);
		VBox root = new VBox(5, sockets, connect, write, full);
		primaryStage.setOnCloseRequest(e -> {
			try {
				server.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
