package orionis.zeta.moderatus.web.database;


public enum CategoryKey implements DatabaseKey {
	CODE("IDCategoria"),
	NAME("Valor");
	
	private String value;
	private CategoryKey(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
}
