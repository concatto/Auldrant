package br.univali.pushsynth.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		SynthScene synthScene = new SynthScene();
		
		primaryStage.setScene(synthScene);
		primaryStage.setTitle("Push Synth");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
