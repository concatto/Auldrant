package orionis.delta.alacritas.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller extends Application {
	private boolean up;
	private boolean down;
	private boolean right;
	private boolean left;
	private SimpleDoubleProperty x = new SimpleDoubleProperty(50);
	private SimpleDoubleProperty y = new SimpleDoubleProperty(50);
	private boolean pressed = false;
	private ColorAdjust effect;
	private Timeline time;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Circle circle = new Circle(20);
		AnchorPane root = new AnchorPane(circle);
		Scene scene = new Scene(root, 500, 500);
		
		circle.centerXProperty().bind(x);
		circle.centerYProperty().bind(y);
		
		scene.setOnKeyPressed(this::changePressed);
		scene.setOnKeyReleased(this::changeReleased);
		
		circle.setFill(Color.MEDIUMSEAGREEN);
		root.setPadding(new Insets(30));
		
		effect = new ColorAdjust();
		circle.setEffect(effect);
		
		KeyFrame off = new KeyFrame(Duration.ZERO, new KeyValue(effect.brightnessProperty(), 0));
		KeyFrame on = new KeyFrame(Duration.millis(400), new KeyValue(effect.brightnessProperty(), 0.8, Interpolator.EASE_OUT));
	
		time = new Timeline(on, off);
		time.setCycleCount(Timeline.INDEFINITE);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("alacritasOrionis");
		primaryStage.show();
		
		Thread loop = new Thread(() -> {
			while (true) {
				if (pressed) Platform.runLater(() -> time.setRate(time.getRate() * 1.001));
				if (up) Platform.runLater(() -> y.set(y.get() - 1));
				if (down) Platform.runLater(() -> y.set(y.get() + 1));
				if (left) Platform.runLater(() -> x.set(x.get() - 1));
				if (right) Platform.runLater(() -> x.set(x.get() + 1));
				
				try {
					Thread.sleep(5);
				} catch (Exception e) {
					break;
				}
			}
		});
		loop.start();
		
		primaryStage.setOnCloseRequest(t -> loop.interrupt());
	}
	
	private void changePressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.X) && time.getCurrentTime().equals(Duration.ZERO)) {
			pressed = true;
			blink();
		}
		if (e.getCode().equals(KeyCode.UP)) up = true;
		if (e.getCode().equals(KeyCode.DOWN)) down = true;
		if (e.getCode().equals(KeyCode.LEFT)) left = true;
		if (e.getCode().equals(KeyCode.RIGHT)) right = true;
	}
	
	private void changeReleased(KeyEvent e) {
		if (e.getCode().equals(KeyCode.X)) {
			pressed = false;
			time.setRate(1);
			stopBlink();
		}
		if (e.getCode().equals(KeyCode.UP)) up = false;
		if (e.getCode().equals(KeyCode.DOWN)) down = false;
		if (e.getCode().equals(KeyCode.LEFT)) left = false;
		if (e.getCode().equals(KeyCode.RIGHT)) right = false;
	}
	
	private void blink() {
		time.playFromStart();
	}
	
	private void stopBlink() {
		time.stop();
		effect.setBrightness(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
