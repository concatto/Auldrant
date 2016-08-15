package epsilon.orionis.aether.modelo;

public enum EnumInformacoes {
	TEMPO("tempo"),
	ENDERECO("endereco"),
	IP("ip"),
	MASCARA("mascara"),
	MODEM("modem");
	
	String codigo;
	String valor;
	private EnumInformacoes(String codigo) {
		codigo = "param." + codigo;
		this.codigo = codigo;
	}
	
	static {
		TEMPO.valor = "Tempo";
		ENDERECO.valor = "Conexão";
		IP.valor = "Endereço IP";
		MASCARA.valor = "Máscara de sub-rede";
		MODEM.valor = "Gateway padrão";
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getValor() {
		return valor;
	}
	
	public static String converterCodigo(String codigo) {
		for (EnumInformacoes valores : values()) {
			if (valores.getCodigo().equals(codigo)) {
				return valores.getValor();
			}
		}
		return null;
	}
}
