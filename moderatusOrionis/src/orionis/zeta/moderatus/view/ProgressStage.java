package orionis.zeta.moderatus.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressStage extends Stage {
	public static final double INDICATOR_SIZE = 30;
	private Label messageLabel = new Label();
	private ProgressIndicator indicator = new ProgressIndicator();
	private StackPane indicatorContainer = new StackPane(indicator);
	private VBox root = new VBox(10, indicatorContainer, messageLabel);
	private Scene scene = new Scene(root);
	
	public ProgressStage() {
		indicatorContainer.setPrefSize(INDICATOR_SIZE, INDICATOR_SIZE);
		root.setPadding(new Insets(15, 30, 15, 30));
		root.setAlignment(Pos.CENTER);
		setScene(scene);
		setTitle("Processando");
		setResizable(false);
		setMinWidth(210);
	}
	
	public ProgressStage(Stage owner) {
		this();
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
	}
	
	public void start(String message) {
		if (isShowing()) return;
		Platform.runLater(() -> {
			setMessage(message);
			show();
		});
	}
	
	public void finish() {
		Platform.runLater(this::hide);
	}
	
	public void setMessage(String message) {
		if (!messageLabel.textProperty().isBound()) messageLabel.setText(message);
	}
	
	public Label getMessageLabel() {
		return messageLabel;
	}
	
	public VBox getRoot() {
		return root;
	}
	
	public StackPane getIndicatorContainer() {
		return indicatorContainer;
	}
	
	public ProgressIndicator getIndicator() {
		return indicator;
	}
}
