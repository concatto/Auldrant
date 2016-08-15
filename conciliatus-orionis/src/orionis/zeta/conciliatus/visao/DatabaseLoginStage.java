package orionis.zeta.conciliatus.visao;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class DatabaseLoginStage extends AbstractLoginStage {
	private Label hostLabel;
	private TextField host;
	
	public DatabaseLoginStage() {
		super();
	}

	@Override
	protected void construct() {
		titleLabel = new Text("Banco de Dados");
		hostLabel = new Label("Servidor");
		host = new TextField();
		
		grid.addRow(row, hostLabel, host);
		row++;
		
		
		connect.setOnAction(e -> controller.connectDatabase(host.getText(), user.getText(), password.getText()));
	}

	@Override
	protected void initDefaults() {
		host.setText(getDefault(DefaultKey.HOST));
		user.setText(getDefault(DefaultKey.DATABASE_USER));
		password.setText(getDefault(DefaultKey.DATABASE_PASSWORD));
	}
}
