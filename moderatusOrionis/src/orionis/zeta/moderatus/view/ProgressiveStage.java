package orionis.zeta.moderatus.view;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import orionis.zeta.moderatus.model.ProgressionStatus;

public class ProgressiveStage extends ProgressStage {
	private static final ImageView SUCCESS = new ImageView(getImage("/success.png"));
	private static final ImageView FAILURE = new ImageView(getImage("/failure.png"));
	private static final ImageView CANCEL = new ImageView(getImage("/success.png"));
	private Button action = new Button("Cancelar");
	
	public ProgressiveStage(Stage owner) {
		super(owner);
		getRoot().getChildren().add(action);
		action.setDisable(true);
		action.setPrefWidth(90);
		getMessageLabel().textProperty().addListener((obs, old, newValue) -> {
			Platform.runLater(() -> {
				sizeToScene();
				centerOnScreen();
			});
		});
	}

	public void bindMessage(ObservableValue<String> messageProperty) {
		getMessageLabel().textProperty().bind(messageProperty);
	}
	
	public void unbindMessage() {
		getMessageLabel().textProperty().unbind();
	}
	
	@Override
	public void finish() {
		changeActionStatus(true);
		action.setText("OK");
		action.setOnAction(e -> hide());
	}
	
	public void finish(ProgressionStatus status) {
		finish();
		
		ObservableList<Node> children = getIndicatorContainer().getChildren();
		ImageView image;
		switch (status) {
		case SUCCEEDED:
			image = SUCCESS;
			break;
		case FAILED:
			image = FAILURE;
			break;
		case CANCELLED:
			image = CANCEL;
			break;
		default:
			image = null;
			break;
		}
		ProgressIndicator indicator = getIndicator();
		FadeTransition first = new FadeTransition(Duration.millis(500), getIndicator());
		first.setFromValue(1);
		first.setToValue(0);
		first.play();
		first.setOnFinished(e -> {
			children.remove(indicator);
			first.setFromValue(0);
			first.setToValue(1);
			first.setOnFinished(null);
			first.setNode(image);
			
			image.setOpacity(0);
			children.add(image);
			first.play();
		});
	}
	
	@Override
	public void start(String message) {
		super.start(message);
		changeActionStatus(false);
	}
	
	public void start(String message, Runnable onCancel) {
		super.start(message);
		changeActionStatus(true);
		action.setText("Cancelar");
		action.setOnAction(e -> onCancel.run());
	}
	
	public void changeActionStatus(boolean allow) {
		action.setDisable(!allow);
	}
	
	private static Image getImage(String path) {
		return new Image(ProgressiveStage.class.getResourceAsStream(path), INDICATOR_SIZE, INDICATOR_SIZE, true, true);
	}
}
