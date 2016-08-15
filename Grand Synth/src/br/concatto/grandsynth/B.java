package br.concatto.grandsynth;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class B extends Application {
	private static final char[] KEYS = {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
		'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
		'z', 'x', 'c', 'v', 'b', 'n', 'm'
	};

	@Override
	public void start(Stage primaryStage) throws Exception {
		HBox keys = new HBox(0);
		int w = 10;
		HBox sharps = new HBox(11, new Rectangle(w / 2, 0));
		StackPane keyboard = new StackPane(keys, sharps);
		keyboard.setAlignment(Pos.TOP_LEFT);
		StackPane root = new StackPane(keyboard);
		root.setAlignment(Pos.CENTER);
		primaryStage.setScene(new Scene(root));
		
		for (int i = 0; i < KEYS.length; i++) {
			if (i < KEYS.length - 2) {
				Rectangle sharp = new Rectangle(w, hasSharp(i) ? 60 : 0, Color.BLACK);
				sharps.getChildren().add(sharp);
			}
			
			Rectangle key = new Rectangle(20, 90, Color.WHITE);
			key.setStroke(Color.BLACK);
			key.setStrokeWidth(0.3);
			keys.getChildren().add(key);
		}
		
		primaryStage.show();
	}
	
	private boolean hasSharp(int index) {
		index++;
		return index % 7 != 0 && (index + 4) % 7 != 0;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
