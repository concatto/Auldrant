package orionis.delta.experimentum.main;

import java.util.concurrent.CyclicBarrier;

import javafx.animation.Animation.Status;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Threads extends Application {
	private CyclicTimerBarrier barrier;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		int parties = 2;
		int barHeight = 200;
		
		Button button = new Button("Iniciar");
		HBox bars = new HBox(5);
		VBox root = new VBox(10, button, bars);
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Threading!");
		
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(20));
		bars.setPrefHeight(barHeight);
		
		CyclicTimer[] timers = new CyclicTimer[parties];
		barrier = new CyclicTimerBarrier(parties);
		
		for (int i = 0; i < parties; i++) {
			Rectangle rectangle = new Rectangle(20, barHeight);
			rectangle.setFill(i == 0 ? Color.SKYBLUE : i == 1 ? Color.CRIMSON : Color.DARKOLIVEGREEN);
			bars.getChildren().add(rectangle);
			
			timers[i] = new CyclicTimer(rectangle, 30, 3000 + (i * 500), barHeight, barrier);
		}
		
		button.setOnAction(e -> {
			for (CyclicTimer timeline : timers) {
				timeline.start();
			}
		});
		
		primaryStage.show();
		
	}
	
	class CyclicTimer extends AnimationTimer {
		private Timeline timeline;
		private KeyFrame end;
		private KeyFrame begin;
		private Rectangle element;
		private int interval;
		private CyclicTimerBarrier barrier;
		private int cycles = 1;
		
		public CyclicTimer(Rectangle element, int interval, int speed, int maximum, CyclicTimerBarrier barrier) {				
			this.element = element;
			this.interval = interval;
			this.barrier = barrier;
			begin = new KeyFrame(Duration.ZERO, new KeyValue(element.heightProperty(), 0));
			end = new KeyFrame(Duration.millis(speed), new KeyValue(element.heightProperty(), maximum));
			timeline = new Timeline(begin, end);
		}
		
		@Override
		public void handle(long now) {
			
			if (element.getHeight() >= (interval * cycles) && timeline.getStatus() != Status.PAUSED) {
				timeline.pause();
				barrier.increment();
				cycles++;
			} else if (barrier.isEveryoneReady()) {
				timeline.play();
				barrier.decrement();
			}
			
		}
		
		@Override
		public void start() {
			super.start();
			timeline.playFromStart();
		}
	}
	
	class CyclicTimerBarrier {
		private int parties;
		private volatile int count;
		private boolean everyoneReady = false;

		public CyclicTimerBarrier(int parties) {
			this.parties = parties;
		}
		
		public synchronized void increment() {
			count++;
			System.out.println(count);
			if (count == parties) {
				count = 0;
				everyoneReady = true;
			}
		}
		
		public synchronized void decrement() {
			count--;
			if (count == 0) everyoneReady = false;
		}
		
		public boolean isEveryoneReady() {
			return everyoneReady;
		}
		
		public void reset() {
			if (count == 0) everyoneReady = false;
		}
	}
}
