package orionis.zeta.chrono.controlador;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.text.Text;

public class Cronometro implements Runnable {
	private ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> future;
	private LocalTime time = LocalTime.of(0, 0, 0);
	private Text target;

	public Cronometro(Text target) {
		this.target = target;
	}
	
	@Override
	public void run() {
		time = time.plusSeconds(1);
		Platform.runLater(() -> target.setText(time.toString()));
	}
	
	public void start() {
		future = timer.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
	}
	
	public void restart() {
		time = LocalTime.of(0, 0, 0);
		target.setText("00:00:00");
	}
	
	public void stop() {
		future.cancel(true);
	}
	
	public void terminate() {
		timer.shutdown();
	}
}
