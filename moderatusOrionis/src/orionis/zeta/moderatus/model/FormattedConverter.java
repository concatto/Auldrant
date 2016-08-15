package orionis.zeta.moderatus.model;

import java.util.Locale;

import javafx.util.converter.NumberStringConverter;

public class FormattedConverter extends NumberStringConverter {
	public FormattedConverter() {
		super(Locale.FRANCE, "###.##");
	}
	
	@Override
	public Number fromString(String value) {
		if (!value.isEmpty()) {
			try {
				return super.fromString(value);
			} catch (RuntimeException e) {
				return 0;
			}
		}
		return 0;
	}
	
	public double toDouble(String value) {
		Number number = super.fromString(value);
		return number != null ? number.doubleValue() : 0;
	}
}
