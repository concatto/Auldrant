package epsilon.orionis.aether.modelo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ferramentas {
	public static int ultimoIndiceRegex(String str, String regex) {
		Matcher m = getMatcher(str, regex);
		
		int ultimoIndice = -1;
		while (m.find()) {
			ultimoIndice = m.end();
		}
		
		return ultimoIndice;
	}
	
	public static int primeiroIndiceRegex(String str, String regex) {
		Matcher m = getMatcher(str, regex);
		
		while (m.find()) {
			return m.start();
		}
		
		return -1;
	}
	
	private static Matcher getMatcher(String str, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);		
		return m;
	}

	public static String calcularMascara(String gateway) {
		String[] valores = gateway.split("\\.");
		int valor;
		try {
			valor = Integer.parseInt(valores[0]);
		} catch (NumberFormatException e) {
			valor = 0;
		}
		int quantidade = valor / 64;
		if (quantidade > 1) quantidade--;
		String texto = "255";
		for (int i = 0; i < 3; i++) {
			if (quantidade > 0) {
				texto = texto.concat(".255");
				quantidade--;
			} else {
				texto = texto.concat(".0");
			}
		}
		return texto;
	}
}