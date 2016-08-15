package epsilon.orionis.aether.modelo;

import java.util.List;

public class ParserComando {
	public static EnumProcessos refinarProcesso(List<String> comandos) {
		String valor = "";
		int limite = comandos.size();
		for (int i = 3; i < limite; i++) {
			String comando = comandos.get(i);
			valor = valor.concat(comando);
			
			if (verificarTermino(valor, comando)) break;
			if (i != limite - 1) {
				valor = valor.concat(", ");
			}
		}
		
		for (EnumProcessos processo : EnumProcessos.values()) {
			if (processo.getValor().equals(valor)) {
				return processo;
			}
		}
		return null;
	}

	private static boolean verificarTermino(String valor, String comando) {
		if (comando.equals("ping") || comando.equals("tracert")) {
			return true;
		} else if (comando.equals("address") && valor.contains("ip, set")) {
			return true;
		} else if (comando.equals("addresses") && valor.contains("ip, show")) {
			return true;
		} else {
			return false;
		}
	}
}
