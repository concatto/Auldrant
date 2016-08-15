package orionis.zeta.chrono.controlador;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public abstract class AbstractScene extends Scene {
	protected VBox mainPane = new VBox(10);
	private VBox root;
	
	public AbstractScene() {
		super(new MenuRoot());
		
		initialize();
		construct();
		root = (VBox) getRoot();
		root.getChildren().add(mainPane);
	}
	
	public abstract void terminate();
	protected abstract void initialize();
	protected abstract void construct();
	protected abstract void setButtonState(boolean isRunning);
}
