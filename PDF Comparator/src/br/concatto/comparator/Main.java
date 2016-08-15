package br.concatto.comparator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
	public static void main(String[] args) {
		Decompresser.parse(new File("C:/Users/Fernando/Downloads/Presenças na Semana Acadêmica (Computação).pdf"));
		
		System.exit(0);
	}
	
	
	
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox(5);
		root.setPadding(new Insets(10));
		Scene scene = new Scene(root, 300, 100);
		
		scene.setOnDragOver(event -> {
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasFiles()) event.acceptTransferModes(TransferMode.COPY);
		});
		
		scene.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasFiles()) {
				for (File file : dragboard.getFiles()) {
					root.getChildren().add(new Label(file.getName()));
				}
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
