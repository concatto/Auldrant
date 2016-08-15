package orionis.zeta.conciliatus.visao;

public enum User {
	DEFAULT("indefinido"), WAITING("aguardando"), NONE("nenhum");
	
	private String value;

	private User(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static User register(String value) {
		DEFAULT.value = value;
		return DEFAULT;
	}
}
