package orionis.delta.experimentum.main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Trajectory extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		int elements = 7500;
		
		Circle[] circles = new Circle[elements];
		double[] angles = new double[elements];
		
		for (int i = 0; i < elements; i++) {
			Circle circle = new Circle(1.2, Color.WHITE);
			double x = generatePosition(300, 150, 100);
			double y = generatePosition(300, 150, 0);
			circle.setCenterX(x);
			circle.setCenterY(y);
			circles[i] = circle;
			angles[i] = Math.toDegrees(Math.atan2(150 - y, 150 - x));
		}
		
		Group root = new Group(circles);
		Scene scene = new Scene(root, 300, 300, true, SceneAntialiasing.BALANCED);
		scene.setFill(Color.BLACK);
		primaryStage.setScene(scene);
		
		new AnimationTimer() {
			double index = 0;
			
			@Override
			public void handle(long now) {
				for (int i = 0; i < elements; i++) {
					Circle circle = circles[i];
					circle.setTranslateX(index * Math.cos(angles[i] * Math.PI / 180));
					circle.setTranslateY(index * Math.sin(angles[i] * Math.PI / 180));
				}
				index += 1;
			}
		}.start();
		
		primaryStage.setTitle("Angulus");
		primaryStage.show();
	}

	private double generatePosition(int range, int convergence, int interval) {
		double position = Math.random() * range;
		while (position > convergence - interval && position < convergence + interval) {
			position = Math.random() * range;
		}
		return position;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
