package orionis.zeta.conciliatus.visao;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Status {
	CONNECTED("conectado", Color.LIMEGREEN), CONNECTING("conectando", Color.CORNFLOWERBLUE), DISCONNECTED("desconectado", Color.CRIMSON);
	
	private String value;
	private Paint color;
	private Status(String value, Paint color) {
		this.value = value;
		this.color = color;
	}
	
	public String getValue() {
		return value;
	}
	
	public Paint getColor() {
		return color;
	}
}
