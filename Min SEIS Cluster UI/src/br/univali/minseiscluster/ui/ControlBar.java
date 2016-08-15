package br.univali.minseiscluster.ui;

import java.io.InputStream;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ControlBar extends HBox {
	private static final ImageView POSITION = loadIcon("graph.png");
	private static final ImageView PLAY = loadIcon("play.png");
	private static final ImageView PAUSE = loadIcon("pause.png");
	
	private Runnable playAction;
	private Runnable pauseAction;
	private Button positionButton = new Button();
	private Button playPauseButton = new Button();
	private BooleanProperty paused = new SimpleBooleanProperty(true);
	
	public ControlBar() {
		super(5);
		
		playPauseButton.setOnAction(e -> {
			if (paused.get()) {
				if (playAction != null) playAction.run();
			} else {
				if (pauseAction != null) pauseAction.run();
			}
			
			paused.set(!paused.get());
		});
		
		positionButton.setGraphic(POSITION);
		positionButton.prefHeightProperty().bind(playPauseButton.heightProperty());
		playPauseButton.graphicProperty().bind(Bindings.when(paused).then(PLAY).otherwise(PAUSE));
		getChildren().addAll(positionButton, playPauseButton);
	}
	
	private static ImageView loadIcon(String icon) {
		InputStream in = ControlBar.class.getClassLoader().getResourceAsStream("icons/" + icon);
		return new ImageView(new Image(in));
	}

	public void onPosition(EventHandler<ActionEvent> e) {
		positionButton.setOnAction(e);
	}
	
	public void onPlay(Runnable playAction) {
		this.playAction = playAction;
	}
	
	public void onPause(Runnable pauseAction) {
		this.pauseAction = pauseAction;
	}
}
