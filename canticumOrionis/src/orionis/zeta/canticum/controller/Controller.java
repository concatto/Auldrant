package orionis.zeta.canticum.controller;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import orionis.zeta.canticum.model.SequenceReader;
import orionis.zeta.canticum.view.SequencerStage;

public class Controller extends Application {
	private SequenceReader reader;
	private SequencerStage sequencer;
	private Button buttonReader = new Button("Sequence Reader");
	private HBox root = new HBox(20, buttonReader);
	private Scene scene = new Scene(root);
	private static Controller instance;
	
	public Controller() {		
		instance = this;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		root.setPadding(new Insets(30, 55, 30, 55));
		buttonReader.setOnAction(e -> {
			reader = new SequenceReader();
			sequencer = new SequencerStage();
			primaryStage.hide();
			sequencer.show();
		});
		
		primaryStage.setTitle("canticumOrionis");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public static Controller getInstance() {
		return instance;
	}
	
	public void sendByte(byte[] message, int trackNumber) {
		if (message.length > 2 && message[2] > 0) sequencer.sendNote(message[1], trackNumber);
	}

	public void startSequence(File file) {
		sequencer.initChannels(reader.playSequence(file));
	}

	public void stopSequence() {
		reader.stop();
		sequencer.resetBars();
	}
}
