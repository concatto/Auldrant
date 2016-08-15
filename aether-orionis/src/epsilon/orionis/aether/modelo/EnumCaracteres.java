package epsilon.orionis.aether.modelo;

public enum EnumCaracteres {
	ATIL(198, 227),
	AAGUDO(160, 225),
	IAGUDO(161, 237),
	UAGUDO(163, 250),
	OAGUDO(162, 243),
	ACIRCUNFLEXO(402, 226),
	OCIRCUNFLEXO(8220, 244),
	CEDILHA(8225, 231);
	
	int errado;
	int correto;
	private EnumCaracteres(int errado, int correto) {
		this.errado = errado;
		this.correto = correto;
	}
	
	public char getErrado() {
		return (char) errado;
	}
	
	public char getCorreto() {
		return (char) correto;
	}
}
