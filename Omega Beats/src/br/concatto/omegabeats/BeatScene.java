package br.concatto.omegabeats;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class BeatScene extends Scene {
	private VBox root;
	private TextField field;
	private Button play;
	private Slider slider;
	private Label speedLabel;
	
	private BeatPlayer player;
	private BooleanProperty running = new SimpleBooleanProperty(false);
	
	public BeatScene(BeatPlayer player) {
		super(new VBox(5));
		this.player = player;
		
		root = (VBox) getRoot();
		field = new TextField();
		play = new Button();
		slider = new Slider(0, 1000, 500);
		speedLabel = new Label();
		speedLabel.textProperty().bind(slider.valueProperty().asString("%.0f"));
		slider.setMajorTickUnit(500);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.valueProperty().addListener((obs, o, n) -> {
			player.setDelay(n.longValue());
		});
		
		field.disableProperty().bind(running);
		play.textProperty().bind(Bindings.when(running).then("Parar").otherwise("Executar"));
		play.setOnAction(e -> {
			if (running.get()) {
				player.stop();
			} else {
				player.execute(field.getText());
			}
			
			running.set(!running.get());
		});
		
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(5, 10, 5, 10));
		root.getChildren().addAll(field, play, slider, speedLabel);
		
		player.setDelay((long) slider.getValue());
	}
}
