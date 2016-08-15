package orionis.zeta.conciliatus.visao;

import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ContactsLoginStage extends AbstractLoginStage {	
	public ContactsLoginStage(Stage owner) {
		super();
		
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
	}

	@Override
	protected void construct() {
		titleLabel = new Text("Contatos");
		
		connect.setOnAction(e -> controller.connectContacts(user.getText(), password.getText()));
	}

	@Override
	protected void initDefaults() {
		user.setText(getDefault(DefaultKey.CONTACTS_USER));
		password.setText(getDefault(DefaultKey.CONTACTS_PASSWORD));
	}
}
