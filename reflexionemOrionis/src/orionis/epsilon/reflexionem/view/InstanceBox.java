package orionis.epsilon.reflexionem.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

public class InstanceBox extends VBox {
	private Label identifier = new Label();
	private Label type = new Label();
	private Object instance;
	
	public InstanceBox(String identifierText, Object instance) {
		this.instance = instance;
		identifier.setText(identifierText);
		type.setText(instance.getClass().getName());
		
		identifier.getStyleClass().add("instance-identifier");
		type.getStyleClass().add("instance-type");
		
		setSpacing(4);
		getChildren().addAll(identifier, type);
		setAlignment(Pos.CENTER);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		
		getStyleClass().add("instance-box");
		setOnMousePressed(e -> Platform.runLater(this::requestFocus));
		setOnDragDetected(e -> {
			Dragboard board = startDragAndDrop(TransferMode.MOVE);
			WritableImage image = new WritableImage((int) getWidth(), (int) getHeight());
			snapshot(new SnapshotParameters(), image);
			board.setDragView(image);
			ClipboardContent content = new ClipboardContent();
			content.putString(identifierText);
			board.setContent(content);
			e.consume();
		});
		
	}

	public String getIdentifier() {
		return identifier.getText();
	}
	
	public Object getInstance() {
		return instance;
	}
}
