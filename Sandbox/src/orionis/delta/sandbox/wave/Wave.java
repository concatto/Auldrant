package orionis.delta.sandbox.wave;

import java.util.Arrays;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Wave extends Application {
	VBox root = new VBox();
	Canvas canvas = new Canvas(1000, 800);
	GraphicsContext context = canvas.getGraphicsContext2D();
	int lines = 25;
	double yOff = 200;
	double[] curves = new double[lines];
	Color[] colors = new Color[lines];
	double amplitude = 6;
	double wavelength = 100.0;
	
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		for (int i = 0; i < colors.length; i++) {
			colors[i] = Color.hsb((i / ((double) colors.length)) * 360.0, 1, 0.8);
		}
		Arrays.fill(curves, yOff);
		
		Thread t = new Thread(() -> {
			for (double i = 0; i < 1000; i += 0.5) {
				final double index = i;
				Platform.runLater(() -> update(index));
				try {
					Thread.sleep(2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		canvas.setEffect(new MotionBlur(180, 20));
		root.getChildren().add(canvas);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
		t.start();
	}

	private void update(double i) {
		
		double sin = Math.sin(i / wavelength) * amplitude;
		for (int j = 0; j < curves.length; j++) {
			double x = i - (j * 4);
			if (x >= 0 && x < 1000) {
				double x2 = x + (j*2);
				context.setStroke(colors[j]);
				double y = yOff + (sin * (j - (curves.length / 2)));
				context.strokeLine(x, curves[j], x + 0.5, y);
				curves[j] = y;
			}
		}
	}
}