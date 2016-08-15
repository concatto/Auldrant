package orionis.zeta.moderatus.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogStage extends Stage {
	private static final double BUTTON_WIDTH = 64;
	private Label messageLabel = new Label();
	private HBox messageContainer = new HBox(16, messageLabel);
	private Button confirm = new Button("OK");
	private Button cancel;
	private HBox buttons = new HBox(8);
	private VBox root = new VBox(14, messageContainer);
	private Scene scene = new Scene(root);
	private String input;
	private double originalHeight;
	private boolean success = false;
	
	public DialogStage(Stage owner, String message, DialogOption options, Node... content) {
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
		setScene(scene);
		setMaxWidth(400);
		setTitle("Mensagem");
		setLabelAlignment(Pos.CENTER);
		setButtonAlignment(Pos.CENTER);
		setOnShown(e -> originalHeight = getHeight());
		
		switch (options) {
		case CONFIRM_CANCEL:
			cancel = new Button("Cancelar");
			buttons.getChildren().addAll(confirm, cancel);
			break;
		case CONFIRM:
			buttons.getChildren().add(confirm);
			break;
		}
		
		for (Node node : buttons.getChildren()) {
			((Button) node).setPrefWidth(BUTTON_WIDTH);
		}
		
		root.setAlignment(Pos.TOP_CENTER);
		root.getChildren().addAll(content);
		root.getChildren().add(buttons);
		root.setPadding(new Insets(10, 30, 12, 30));
		messageContainer.setAlignment(Pos.CENTER);
		messageLabel.setWrapText(true);
		messageLabel.setText(message);
	}
	
	public double getOriginalHeight() {
		return originalHeight;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getInput() {
		return input;
	}
	
	public void setConfirmListener(EventHandler<ActionEvent> listener) {
		confirm.setOnAction(listener);
	}
	
	public void setCancelListener(EventHandler<ActionEvent> listener) {
		cancel.setOnAction(listener);
	}
	
	public void setButtonAlignment(Pos alignment) {
		buttons.setAlignment(alignment);
	}
	
	public void setLabelAlignment(Pos alignment) {
		messageContainer.setAlignment(alignment);
	}
	
	public void setMessageIcon(Image icon) {
		messageContainer.getChildren().add(0, new ImageView(icon));
	}
}