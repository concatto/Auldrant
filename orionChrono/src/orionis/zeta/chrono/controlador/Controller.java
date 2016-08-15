package orionis.zeta.chrono.controlador;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.stage.Stage;

public class Controller extends Application {
	private static final Preferences PREFS = Preferences.userNodeForPackage(Controller.class);
	private static final String KEY = "state";	
	private static Controller instance;
	
	public Controller() {
		super();
		
		synchronized (Controller.class) {
			/* Singleton */
			if (instance != null) throw new UnsupportedOperationException("Uma instância desta classe já existe.");
			instance = this;
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		AbstractScene cena = getState().equals(State.COUNTDOWN) ? new CountdownScene() : new ChronometerScene();
		primaryStage.setOnCloseRequest(e -> cena.terminate());
		primaryStage.setTitle("orionChrono");
		primaryStage.setScene(cena);
		primaryStage.show();
	}
	
	public void setState(State state) {
		PREFS.put(KEY, state.toString());
	}
	
	public State getState() {
		return State.valueOf(PREFS.get(KEY, State.CHRONOMETER.toString()));
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Controller getInstance() {
		return instance;
	}

	public void switchState(State newState) {
		setState(newState);
	}
}
