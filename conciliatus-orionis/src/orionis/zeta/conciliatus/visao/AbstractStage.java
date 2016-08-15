package orionis.zeta.conciliatus.visao;

import orionis.zeta.conciliatus.controlador.Controller;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class AbstractStage extends Stage {
	public static final String NAME = "conciliatusOrionis";
	protected static final Controller controller = Controller.getInstance();
	
	public AbstractStage() {
		super();
		applyProperties(this);
	}
	
	public static void applyProperties(Stage target) {
		target.getIcons().add(new Image(AbstractStage.class.getResourceAsStream("/conciliatus.png")));
		target.setTitle(NAME);
		target.setOnShown(e -> centralize(target));
		target.setOnCloseRequest(e -> controller.cleanUp());
	}
	
	public static void centralize(Stage target) {
		target.setY((Screen.getPrimary().getBounds().getHeight() - target.getHeight()) / 2);
	}
}
