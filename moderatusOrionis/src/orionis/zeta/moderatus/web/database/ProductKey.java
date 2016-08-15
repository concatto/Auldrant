package orionis.zeta.moderatus.web.database;


public enum ProductKey implements DatabaseKey {	
	CODE("Codigo"),
	NAME("Nome"),
	DESCRIPTION("Descricao"),
	PRICE("Preco"),
	MAIN_IMAGE("ImagemPrincipal"),
	CATEGORY("Categoria"),
	STOCK("Estoque"),
	AMOUNT_SOLD("QuantidadeVendida"),
	WEIGHT("Peso"),
	DISCOUNT("Desconto"),
	EXTRA_IMAGE_1("ImagemExtra1"),
	EXTRA_IMAGE_2("ImagemExtra2"),
	EXTRA_IMAGE_3("ImagemExtra3"),
	EXTRA_IMAGE_4("ImagemExtra4");

	private String value;
	private ProductKey(String value) {
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return value;
	}
}
