package orionis.zeta.conciliatus.visao;

import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public abstract class AbstractLoginStage extends AbstractStage {
	protected int row = 0;
	protected Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	protected GridPane grid = new GridPane();
	protected Text titleLabel;
	protected Label userLabel = new Label("Usuário");
	protected Label passwordLabel = new Label("Senha");
	protected TextField user = new TextField();
	protected PasswordField password = new PasswordField();
	protected Button connect = new Button("Conectar");
	private VBox root = new VBox(20);
	private Scene cena = new Scene(root);
	private ProgressStage progress = new ProgressStage(this, "Conectando", "Conectando...");
	
	public AbstractLoginStage() {
		super();
		construct();
		
		grid.setHgap(10);
		grid.setVgap(7);
		grid.setAlignment(Pos.CENTER);
		
		grid.addRow(row, userLabel, user);
		row++;
		grid.addRow(row, passwordLabel, password);
		
		titleLabel.setFont(Font.font("Calibri", 30));
		root.setPadding(new Insets(20, 30, 30, 30));
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(titleLabel, grid, connect);
		
		grid.getChildren().stream().filter(t -> t instanceof TextField).forEach(t -> t.addEventHandler(ActionEvent.ACTION, e -> connect.fire()));
		
		connect.addEventHandler(ActionEvent.ACTION, e -> progress.show());
		
		initDefaults();
		setScene(cena);
	}

	public void finishProgress() {
		progress.close();
	}
	
	public void setDefault(DefaultKey key, String value) {
		prefs.put(key.getValue(), value);
	}
	
	public String getDefault(DefaultKey key) {
		return prefs.get(key.getValue(), "");
	}
	
	protected abstract void construct();
	protected abstract void initDefaults();

	public void focusFirst() {
		grid.getChildren().get(1).requestFocus();
	}
}