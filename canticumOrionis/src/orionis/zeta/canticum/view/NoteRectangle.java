package orionis.zeta.canticum.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class NoteRectangle extends Rectangle {
	private static final int WIDTH = 10;
	private static final int HEIGHT = 5;
	private static final int MAX_HEIGHT = 300;
	private static final double COEFFICIENT = MAX_HEIGHT / Byte.MAX_VALUE;
	private ColorAdjust color = new ColorAdjust();
	private DropShadow shadow = new DropShadow(2, Color.DARKGOLDENROD);
	private Timeline flash;
	private Timeline drop;
	
	public NoteRectangle() {
		super(WIDTH, HEIGHT);
		
		color.setInput(shadow);
		setEffect(color);
		setFill(Color.ROYALBLUE);
		KeyFrame flashStart = new KeyFrame(Duration.ZERO, new KeyValue(color.brightnessProperty(), 0.5));
		KeyFrame flashFinish = new KeyFrame(Duration.millis(300), new KeyValue(color.brightnessProperty(), 0));
		flash = new Timeline(flashStart, flashFinish);
		
		KeyFrame dropFinish = new KeyFrame(Duration.seconds(2), new KeyValue(heightProperty(), HEIGHT));
		drop = new Timeline(dropFinish);
		drop.setDelay(Duration.seconds(2));
		
		flash.setOnFinished(e -> drop.playFromStart());
	}
	
	public void playNote(double note) {
		Platform.runLater(() -> {
			setHeight(note * COEFFICIENT);
			drop.stop();
			flash.playFromStart();
		});
	}
	
	public static int getMaxHeight() {
		return MAX_HEIGHT;
	}
}
