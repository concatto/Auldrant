package epsilon.orionis.aether.modelo;

public enum EnumProcessos {
	PING("ping"),
	TRACERT("tracert"),
	RENEW("ipconfig, /renew"),
	RELEASE("ipconfig, /release"),
	FLUSHDNS("ipconfig, /flushdns"),
	GATEWAY("ipconfig, /all"),
	NETSHOW("netsh, interface, show, interface"),
	NETSET("netsh, interface, ip, set, address"),
	NETINFO("netsh, interface, ip, show, addresses");
	
	private String valor;
	private EnumProcessos(String valor) {
		this.valor = valor;
	}
	
	public String getValor() {
		return valor;
	}
}
