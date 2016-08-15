package orionis.zeta.chrono.controlador;

import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class ChronoMenuBar extends MenuBar {
	private Controller controller = Controller.getInstance();
	private State destino = controller.getState().equals(State.COUNTDOWN) ? State.CHRONOMETER : State.COUNTDOWN;
	private Menu arquivo = new Menu("Arquivo");
	private MenuItem alternar = new MenuItem("Alternar para " + destino.value());
	
	public ChronoMenuBar() {
		alternar.setOnAction(e -> controller.switchState(destino));
		arquivo.getItems().add(alternar);
		
		setPadding(Insets.EMPTY);
		getMenus().add(arquivo);
	}
}
