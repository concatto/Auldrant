package orionis.zeta.chrono.controlador;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class MenuRoot extends VBox {
	public MenuRoot() {
		super(new ChronoMenuBar());
		setAlignment(Pos.TOP_CENTER);
	}
}
