package orionis.zeta.moderatus.view;

import orionis.zeta.moderatus.model.NumberChangeListener;
import javafx.scene.control.TextField;

public class NumberField extends TextField {
	public NumberField() {
		this(0);
	}
	
	public NumberField(int decimals) {
		this(null, decimals);
	}
	
	public NumberField(String initialText) {
		this(initialText, 0);
	}
	
	public NumberField(String initialText, int decimals) {
		super(initialText);
		textProperty().addListener(new NumberChangeListener(textProperty(), decimals));
	}
}
