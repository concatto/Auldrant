package orionis.zeta.jattorney.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	private TextArea dialogArea = new TextArea();
	private ImageView background = new ImageView();
	private ImageView textBackground = new ImageView();
	private Label nameLabel = new Label("Phoenix");
	private VBox textBox = new VBox(nameLabel, dialogArea);
	private StackPane root = new StackPane(background, textBackground, textBox);
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		root.setAlignment(Pos.BOTTOM_CENTER);
		textBox.setAlignment(Pos.BOTTOM_LEFT);
		dialogArea.setMaxHeight(45);
		dialogArea.setEditable(false);
		dialogArea.setFont(Font.font("Consolas", 12));
		nameLabel.setFont(Font.font("Consolas", 10));
		nameLabel.setPadding(new Insets(0, 0, 0, 4));
		
		background.setImage(new Image(getClass().getResource("/bg.png").openStream()));
		textBackground.setImage(new Image(getClass().getResource("/speechbox.png").openStream()));
		
		Scene scene = new Scene(root, 256, 192);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		
		primaryStage.setTitle("JAttorney");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		new Thread(() -> {
			for (char c : "A defesa está preparada,\nvossa excelência.".toCharArray()) {
				Platform.runLater(() -> dialogArea.setText(dialogArea.getText() + c));
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
