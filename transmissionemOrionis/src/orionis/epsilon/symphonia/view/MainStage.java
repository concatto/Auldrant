package orionis.epsilon.symphonia.view;

import orionis.epsilon.symphonia.control.Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainStage extends Stage {
	private Button start = new Button("Iniciar");
	private Button stop = new Button("Parar");
	private Button write = new Button("Salvar");
	private VBox root = new VBox(6, start, stop, write);
	private Scene scene = new Scene(root);
	
	public MainStage(Controller controller) {
		setScene(scene);
		setTitle("symphoniaOrionis");
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		
		start.setOnAction(e -> controller.startRecording());
		stop.setOnAction(e -> controller.finishRecording());
		write.setOnAction(e -> controller.writeRecording());
	}
}
