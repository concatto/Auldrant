package br.concatto.treeofmemory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

public class Controller extends Application {
	private static final int MAX_SIZE = 60;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private int time = 0;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
		Series<Number, Number> series = new Series<>();

		chart.getData().add(series);
		chart.setAnimated(false);
		chart.setPrefSize(400, 300);
		chart.setCreateSymbols(false);
		xAxis.setTickLabelsVisible(false);
		primaryStage.setScene(new Scene(new Group(chart), 400, 300));
		primaryStage.setAlwaysOnTop(true);
		primaryStage.show();
		
		listen(value -> Platform.runLater(() -> {
			series.getData().add(new Data<Number, Number>(time++, value));
			series.setName(String.valueOf(value));
			if (series.getData().size() >= MAX_SIZE) {
				series.getData().remove(0);
			}
		}));
		
		primaryStage.setOnCloseRequest(e -> executor.shutdownNow());
	}
	
	private void listen(Consumer<Integer> consumer) {
		MemoryReader reader = new MemoryReader();
		reader.initialize("Tree Of Savior");
		
		Deque<Integer> data = new ArrayDeque<>(MAX_SIZE);
		
		executor.submit(() -> {
			int experience = reader.readCurrentExperience();
			int newExperience;
			while (true) {
				newExperience = reader.readCurrentExperience();
				if (data.size() >= MAX_SIZE) {
					data.pollFirst();
				}
				data.offerLast(newExperience - experience);
				experience = newExperience;
				consumer.accept(data.stream().reduce(0, Integer::sum));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
