package orionis.zeta.chrono.controlador;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ChronometerScene extends AbstractScene {
	private Text text;
	private Button stop;
	private Button reset;
	private Button start;
	private Cronometro chrono;
	private HBox buttons;
	
	public ChronometerScene() {
		super();
	}

	@Override
	public void terminate() {
		chrono.terminate();
	}

	@Override
	protected void setButtonState(boolean isRunning) {
		reset.setDisable(isRunning);
		start.setDisable(isRunning);
		stop.setDisable(!isRunning);
	}

	@Override
	protected void construct() {
		stop.setDisable(true);
		
		start.setOnAction(e -> {
			setButtonState(true);
			chrono.start();
		});
		
		stop.setOnAction(e -> {
			setButtonState(false);
			chrono.stop();
		});
		
		reset.setOnAction(e -> chrono.restart());		
		mainPane.setAlignment(Pos.CENTER);
		mainPane.setPadding(new Insets(20, 40, 30, 40));
		mainPane.getChildren().addAll(text, buttons);
	}

	@Override
	protected void initialize() {
		text = new Text("00:00:00");
		stop = new Button("Parar");
		reset = new Button("Reiniciar");
		start = new Button("Iniciar");
		chrono = new Cronometro(text);
		buttons = new HBox(10, start, stop, reset);
	}	
}
