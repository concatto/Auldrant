package orionis.delta.experimentum.main;

import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Stars extends Application {
	private static final int STAR_COUNT = 2000;
	private final Circle[] nodes = new Circle[STAR_COUNT];
	private final double[] angles = new double[STAR_COUNT];
	private final long[] start = new long[STAR_COUNT];

	@Override
	public void start(final Stage primaryStage) {
		for (int i = 0; i < STAR_COUNT; i++) {
			nodes[i] = new Circle(1.5, Color.WHITE);
			angles[i] = (Math.PI * 2) * (i / ((double) STAR_COUNT));
			start[i] = 0;
		}
		Label valueLabel = new Label("0");
		valueLabel.setFont(Font.font(26));
		Slider slider = new Slider(0, 30, 0);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setShowTickLabels(true);
		slider.valueProperty().addListener((obs, old, newValue) -> {
			int value = newValue.intValue();
			if (value == old.intValue()) return;
			for (int i = 0; i < STAR_COUNT; i++) {
				start[i] = i * value;
			}
			valueLabel.setText(String.valueOf(value));
		});
		StackPane group = new StackPane(nodes);
		group.setPrefHeight(600);
		group.setPrefWidth(800);
		group.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		VBox left = new VBox(20, slider, valueLabel);
		left.setAlignment(Pos.CENTER);
		HBox root = new HBox(20, left, group);
		root.setPadding(new Insets(0, 0, 0, 20));
		final Scene scene = new Scene(root);
		primaryStage.setScene(scene);

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				double width = group.getWidth() / 2;
				double height = group.getHeight() / 2;
				double radius = ((width + height) / 2);
				for (int i = 0; i < STAR_COUNT; i++) {
					long duration = 1000;
					long time = TimeUnit.NANOSECONDS.convert(duration, TimeUnit.MILLISECONDS);
					Node node = nodes[i];
					double angle = angles[i];
					long t = (now - TimeUnit.NANOSECONDS.convert(start[i], TimeUnit.MILLISECONDS)) % time;
					double d = t * radius / ((double) time);
					
					node.setTranslateX(Math.cos(angle) * d + (width / 5));
					node.setTranslateY(Math.sin(angle) * d + (height / 10));
				}
			}
		}.start();
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}