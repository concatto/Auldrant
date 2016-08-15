package br.concatto.dimensions;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Temp extends Application {
	static class Camera {
		double center;
		double width;
		double height;
		double depth;
		double perspective;
		
		public Camera(double center, double width, double height, double depth, double perspective) {
			this.center = center;
			this.width = width;
			this.height = height;
			this.depth = depth;
			this.perspective = perspective;
		}
		
		public double getCenter() {
			return center;
		}
		
		public double getWidth() {
			return width;
		}
		
		public double getHeight() {
			return height;
		}
		
		public double getDepth() {
			return depth;
		}
		
		public double getPerspective() {
			return perspective;
		}
		
		public double deep(double point, double depth) {
			double space = this.depth * perspective;
			double perspec = (depth * space) / width;
			double mult = point / (this.depth / 2);
			return point + (perspec - (perspec * mult));
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Camera c = new Camera(250, 500, 500, 500, 0.2);
		Canvas canvas = new Canvas(c.height, c.width);
		GraphicsContext g = canvas.getGraphicsContext2D();
		
		Slider xSlider = new Slider(0, c.width, c.width / 2);
		Slider ySlider = new Slider(0, c.height, c.height / 2);
		Slider depthSlider = new Slider(0, c.depth, c.depth / 2);
		xSlider.setShowTickMarks(true);
		ySlider.setShowTickMarks(true);
		depthSlider.setShowTickMarks(true);
		
		
		ChangeListener<Number> listener = (obs, o, n) -> {
			redraw(g, c, xSlider.getValue(), ySlider.getValue(), depthSlider.getValue());
		};
		
		xSlider.valueProperty().addListener(listener);
		ySlider.valueProperty().addListener(listener);
		depthSlider.valueProperty().addListener(listener);
		
		VBox controls = new VBox(5, new Label("X"), xSlider, new Label("Y"), ySlider, new Label("Profundidade"), depthSlider);
		controls.setPrefWidth(250);
		HBox root = new HBox(5, canvas, controls);
		primaryStage.setTitle("Perspectiva");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	private void redraw(GraphicsContext g, Camera c, double x, double y, double depth) {
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
		g.setFill(Color.RED.deriveColor(1, 1, 1, 1 - (depth / 600)));
		
		double size = c.deep(30, 500 - depth);
		g.fillRect(c.deep(x, depth) - (size / 2f), c.deep(y, depth) - (size / 2f), size, size);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
