package orionis.zeta.moderatus.web.database;

public enum AttributeHeader {
	CHARACTERISTIC("Caracteristicas", "Codigo", "Produto", "Caracteristica"),
	CONTENT("Conteudo", "Codigo", "Produto", "Valor");
	
	private String table;
	private String code;
	private String product;
	private String value;
	
	private AttributeHeader(String table, String code, String product, String value) {
		this.table = table;
		this.code = code;
		this.product = product;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getProduct() {
		return product;
	}
	
	public String getTable() {
		return table;
	}
}
