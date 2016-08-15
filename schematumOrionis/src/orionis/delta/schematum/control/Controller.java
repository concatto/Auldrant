package orionis.delta.schematum.control;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

public class Controller extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Axis<Number> x = new NumberAxis("X", 0, 300, 30);
		Axis<Number> y = new NumberAxis();
		x.setLabel("X");
		
		Series<Number, Number> first = new Series<>("(x ^ 1.8)", generateData(1.8));
		Series<Number, Number> second = new Series<>("(x ^ 1.6)", generateData(1.6));
		Series<Number, Number> third = new Series<>("(x ^ 1.4)", generateData(1.4));
		
		LineChart<Number, Number> chart = new LineChart<>(x, y);
		chart.getData().addAll(Arrays.asList(first, second, third));
		chart.setCreateSymbols(false);
		chart.setTitle("Teste gráfico");
		chart.getStyleClass().add("small-line");
		
		Scene scene = new Scene(new Group(chart));
		scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private ObservableList<Data<Number, Number>> generateData(double power) {
		List<Data<Number,Number>> data = IntStream.iterate(0, num -> num + 1)
				.limit(300)
				.mapToObj(num -> exponentiate(num, power))
				.collect(Collectors.toList());
		return FXCollections.observableArrayList(data);
	}
	
	private Data<Number, Number> exponentiate(double x, double power) {
		return new Data<Number, Number>(x, Math.pow(x, power));
	}
}
