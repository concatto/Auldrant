package orionis.zeta.conciliatus.visao;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SynchronizationStage extends AbstractStage {
	private SimpleIntegerProperty countSuccess = new SimpleIntegerProperty(0);
	private SimpleIntegerProperty countSkipped = new SimpleIntegerProperty(0);
	private SimpleDoubleProperty currentProgress = new SimpleDoubleProperty(0);
	
	private ProgressBar bar = new ProgressBar();
	private Button close = new Button("Fechar");
	private TextFlow successContainer = constructStatusText(true);
	private TextFlow skippedContainer = constructStatusText(false);
	private VBox textContainer = new VBox(2, successContainer, skippedContainer);
	private VBox root = new VBox(10, bar, textContainer, close);
	private Scene scene = new Scene(root);
	
	public SynchronizationStage(int dataLength) {
		bar.setStyle("-fx-accent: dodgerblue");
		bar.progressProperty().bind(currentProgress.divide(dataLength));
		
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(15, 30, 15, 30));
		textContainer.setPadding(new Insets(0, 0, 5, 0));
		
		close.setDisable(true);
		close.setOnAction(e -> close());
		
		setScene(scene);
	}
	
	public void incrementCounter(boolean success) {
		SimpleIntegerProperty target = success ? countSuccess : countSkipped;
		target.set(target.get() + 1);
		currentProgress.set(currentProgress.get() + 1);
		
		if (bar.getProgress() == 1) {
			SimpleDialog.showMessage(this, "Sincronização realizada com sucesso!");
			close.setDisable(false);
		}
	}
	
	private TextFlow constructStatusText(boolean success) {
		Text value = new Text();
		Text start = new Text();
		String startValue;
		String finishValue;
		
		if (success) {
			startValue = " inseridas";
			finishValue = " com sucesso.";
			value.textProperty().bind(countSuccess.asString());
			start.setFill(Color.FORESTGREEN);
		} else {
			startValue = " ignoradas";
			finishValue = " por serem duplicatas.";
			value.textProperty().bind(countSkipped.asString());
			start.setFill(Color.GOLDENROD);
		}
		
		Text entries = new Text(" entradas");
		Text finish = new Text(finishValue);
		start.setText(startValue);
		
		return new TextFlow(value, entries, start, finish);
	}
}
