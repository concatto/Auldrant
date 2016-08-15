package orionis.zeta.conciliatus.visao;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SimpleDialog {
	public static void showMessage(Stage owner, String message) {
		Text text = new Text(message);
		Button close = new Button("Fechar");		
		VBox root = new VBox(15, text, close);
		Scene scene = new Scene(root);
		Stage stage = new AbstractStage();		
		
		close.setOnAction(e -> stage.close());
		root.setPadding(new Insets(15, 20, 15, 20));
		root.setAlignment(Pos.CENTER);
		text.setWrappingWidth(300);
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setTitle("Mensagem");
		stage.setScene(scene);
		stage.show();
	}
}
