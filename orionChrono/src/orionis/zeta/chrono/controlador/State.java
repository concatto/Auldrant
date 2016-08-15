package orionis.zeta.chrono.controlador;

public enum State {
	COUNTDOWN("Contagem Regressiva"), CHRONOMETER("Cronômetro");
	
	private String value;

	private State(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
}
