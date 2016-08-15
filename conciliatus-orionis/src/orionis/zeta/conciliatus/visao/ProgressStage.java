package orionis.zeta.conciliatus.visao;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressStage extends Stage {
	private Text text = new Text();
	private VBox root = new VBox(10, text, new LoadingBar());
	private Scene cena = new Scene(root);
	
	private class LoadingBar extends ProgressBar {
		public LoadingBar() {
			String color = Status.CONNECTING.getColor().toString();
			color = color.substring(2, color.length() - 2); /* Extração do código hexadecimal */
			setStyle("-fx-accent: #" + color);
		}
	}
	
	public ProgressStage(Stage owner, String titleText, String loadingText) {
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(15, 50, 20, 50));
		text.setText(loadingText);
		
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setOnShowing(e -> root.getChildren().set(1, new LoadingBar()));
		setOnShown(e -> AbstractStage.centralize(this));
		setTitle(titleText);
		setScene(cena);
	}
}
