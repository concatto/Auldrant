package orionis.zeta.moderatus.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Dialogs {
	public static boolean showOption(Stage owner, String message) {
		DialogStage dialog = new DialogStage(owner, message, DialogOption.CONFIRM_CANCEL);
		SimpleBooleanProperty success = new SimpleBooleanProperty(false);
		
		dialog.setConfirmListener(e -> {
			success.set(true);
			dialog.hide();
		});
		
		dialog.setCancelListener(e -> dialog.hide());
		dialog.showAndWait();
		return success.get();
	}
	
	public static String showInput(Stage owner, String message) {
		TextField input = new TextField();
		DialogStage dialog = new DialogStage(owner, message, DialogOption.CONFIRM_CANCEL, input);
		
		EventHandler<ActionEvent> finish = e -> dialog.hide();
		
		input.setOnAction(finish);
		dialog.setConfirmListener(finish);
		dialog.setCancelListener(finish);
		dialog.setMinWidth(330);
		dialog.setLabelAlignment(Pos.CENTER_LEFT);
		dialog.showAndWait();
		String text = input.getText();
		return text.isEmpty() ? null : text;
	}
	
	public static void showMessage(Stage owner, String message) {
		DialogStage dialog = new DialogStage(owner, message, DialogOption.CONFIRM);
		
		dialog.setConfirmListener(e -> dialog.hide());
		dialog.showAndWait();
	}
	
	public static void showError(Stage owner, String message, Throwable error) {
		if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().isEmpty()) {
			message += "\n" + error.getLocalizedMessage();
		}
		message += "\nException: " + error.getClass().getSimpleName();
		TextArea text = new TextArea(createStackTrace(error.getStackTrace()));
		TitledPane stackTrace = new TitledPane("Stack Trace", text);
		DialogStage dialog = new DialogStage(owner, message, DialogOption.CONFIRM, stackTrace);
		
		final double target = 200;
		text.setEditable(false);
		stackTrace.setExpanded(false);
		stackTrace.setAnimated(false);
		stackTrace.expandedProperty().addListener((obs, oldValue, newValue) -> {
			if (!dialog.isMaximized()) {
				if (newValue) {
					if (dialog.getHeight() < target + dialog.getOriginalHeight()) {
						dialog.setHeight(target + dialog.getOriginalHeight());
					}
				} else {
					dialog.setHeight(dialog.getOriginalHeight());
				}
			}
		});
		
		dialog.setLabelAlignment(Pos.CENTER_LEFT);
		dialog.setMessageIcon(new Image(Dialogs.class.getResource("/error.png").toExternalForm()));
		dialog.setConfirmListener(e -> dialog.hide());
		dialog.show();
	}
	
	private static String createStackTrace(StackTraceElement[] elements) {
		StringBuilder traceBuilder = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) traceBuilder.append("\n");
			traceBuilder.append(elements[i].toString());
		}
		
		return traceBuilder.toString();
	}
}
