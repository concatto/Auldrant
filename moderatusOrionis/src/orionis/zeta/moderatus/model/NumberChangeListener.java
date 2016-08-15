package orionis.zeta.moderatus.model;

import java.util.regex.Pattern;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class NumberChangeListener implements ChangeListener<String> {
	private Pattern pattern;
	private StringProperty target;

	public NumberChangeListener(StringProperty target, int decimals) {
		this.target = target;
		if (decimals > 0) {
			/*
			 * Regex replaces invalid characters.
			 * First part matches characters that are not digits or commas.
			 * Second part matches characters preceded by a comma and <decimals> digits.
			 */
			StringBuilder regex = new StringBuilder("([^\\d\\,]|(?<=\\,");
			for (int i = 0; i < decimals; i++) {
				regex.append("\\d");
			}
			regex.append(").)");
			pattern = Pattern.compile(regex.toString());
		} else {
			/* Matches characters that are not digits. */
			pattern = Pattern.compile("[^\\d]");
		}
	}
	
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		newValue = newValue.replace(".", ",");
		target.set(pattern.matcher(newValue).replaceAll(""));
	}
}
