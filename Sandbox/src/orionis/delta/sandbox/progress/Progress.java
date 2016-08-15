package orionis.delta.sandbox.progress;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Progress extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ProgressBar bar = new ProgressBar(0.5);
		VBox root = new VBox(bar);
		Scene cena = new Scene(root);
		
		root.setPadding(new Insets(30));
		
		new Stream("C:\\Nintendo_Players_Guide_-_Super.pdf", "C:\\testecopy.pdf");
		primaryStage.setTitle("Test");
		primaryStage.setScene(cena);
		primaryStage.show();
	}
}
