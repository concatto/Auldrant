package epsilon.orionis.aether.modelo;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumberFilter extends DocumentFilter {	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if (text.matches("(.*)[^\\d\\.](.*)")) {
			/* Caractere inválido! */
		} else {
			super.replace(fb, offset, length, text, attrs);
		}
	}
}
