package orionis.zeta.conciliatus.visao;

public enum DefaultKey {
	HOST("host"),
	CONTACTS_USER("userContacts"),
	CONTACTS_PASSWORD("passContacts"),
	DATABASE_USER("userDatabase"),
	DATABASE_PASSWORD("passwordDatabase");
	
	private String value;
	private DefaultKey(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
