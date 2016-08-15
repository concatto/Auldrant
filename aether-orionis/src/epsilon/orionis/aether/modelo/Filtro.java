package epsilon.orionis.aether.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filtro {
	private Map<String, String> gateway = new HashMap<>();
	private Map<String, String> endereco = new HashMap<>();
	private String saida = null;
	
	public Filtro() {
		
	}
	
	/* Início dos filtros */
	
	public String filtrarPing(String recebido) {
		String tempo = "tempo";
		int indexInicial = recebido.indexOf(tempo) + 1;
		int indexFinal = recebido.lastIndexOf("ms");
		if (recebido.startsWith("Disparando")) {
			saida = recebido.substring(0, recebido.indexOf("com"));
			saida = saida.replaceAll("[^0-9\\.]", "");
			saida = "Ping: " + saida;
		} else if (recebido.startsWith("A solicitação ping")) {
			saida = "O host não foi encontrado.";
		} else if (recebido.startsWith("Esgotado")) {
			saida = "Tempo esgotado.";
		} else if (indexInicial > 0) {
			saida = recebido.substring(indexInicial + tempo.length(), indexFinal) + " ms";
		}
		return saida;
	}
	
	public String filtrarFlush(String recebido) {
		if (recebido.endsWith("sucedida.")) {
			saida = "Cache do DNS limpo com sucesso!";
		}
		return saida;
	}
	
	public String filtrarRelease(String recebido) {
		if (recebido.startsWith("Configuração de IP")) {
			saida = "Preparando conexão para renovação...";
		}
		return saida;
	}
	
	public String filtrarRenew(String recebido, int tamanho, int indice) {
		if (recebido.startsWith("Configuração de IP")) {
			saida = "Renovando conexão...";
		} else if (tamanho > 4 && indice == tamanho) {
			saida = "Conexão renovada com sucesso.";
		}
		return saida;
	}
	
	public String filtrarGateway(String recebido) {
		if (recebido.startsWith("Endereço IPv4")) {
			preencherLista(endereco, recebido);
		} else if (recebido.startsWith("Gateway Padrão")) {
			preencherLista(gateway, recebido);
		} else if (recebido.startsWith("Configuração de IP")) {
			saida = "Coletando endereços...";
		}
		return saida;
	}
	
	public String filtrarShow(String recebido) {
		if (recebido.startsWith("Habilitado")) {
			saida = recebido.substring(Ferramentas.ultimoIndiceRegex(recebido, "(cado\\s)")).trim();
		}
		return saida;
	}
	
	public String filtrarSet(String recebido, int tamanho) {
		if (recebido.hashCode() == 0 && tamanho != 1) {
			return null;
		}
		if (recebido.startsWith("Par")) {
			recebido = recebido.substring(recebido.indexOf(" ") + 1);
			String parametro = null;
			if (recebido.startsWith("address")) {
				parametro = "Endereço IP";
			} else if (recebido.startsWith("gateway")) {
				parametro = "Gateway padrão";
			}
			saida = "Erro: A sintaxe do parâmetro \"" + parametro + "\" não está correta. Tente novamente.";
		} else if (tamanho != 1) {
			saida = "Erro: " + recebido;
		} else {
			saida = "Alteração realizada com sucesso!\n"
					+ "Se você não conseguir se conectar com a Internet,\n"
					+ "utilize a opção Restaurar no menu Editar."
			;
		}
		return saida;
	}
	
	public String filtrarInfo(String recebido) {
		int indiceInicial = 0;
		int indiceFinal = recebido.length();
		if (recebido.startsWith("Configuração")) {
			indiceInicial = Ferramentas.primeiroIndiceRegex(recebido, "\"");
			indiceFinal = Ferramentas.ultimoIndiceRegex(recebido, "\"");
			saida = EnumInformacoes.ENDERECO.getCodigo();
		} else if (recebido.startsWith("Endereço")) {
			indiceInicial = Ferramentas.primeiroIndiceRegex(recebido, "\\d");
			saida = EnumInformacoes.IP.getCodigo();
		} else if (recebido.startsWith("Prefixo")) {
			indiceInicial = Ferramentas.primeiroIndiceRegex(recebido, "(255)");
			indiceFinal = Ferramentas.ultimoIndiceRegex(recebido, "\\d");
			saida = EnumInformacoes.MASCARA.getCodigo();
		} else if (recebido.startsWith("Gateway")) {
			indiceInicial = Ferramentas.primeiroIndiceRegex(recebido, "\\d");
			saida = EnumInformacoes.MODEM.getCodigo();
		}
		if (saida != null) {
			saida = saida + ":" + recebido.substring(indiceInicial, indiceFinal);
		}
		return saida;
	}
	
	public List<String> filtrarTracert(String recebido) {
		List<String> valores = new ArrayList<>();
		int divisor = 27;
		int colunas = 3;
		
		if (Ferramentas.primeiroIndiceRegex(recebido, "^\\d") == 0) {
			recebido = recebido.substring(recebido.indexOf(" ") + 1);			
			String numeros = recebido.substring(0, divisor);
			
			/* Valores da latência */
			for (int i = 0; i < colunas; i++) {
				int intervalo = divisor / colunas;
				int primeiroIndice = i * intervalo;
				String numero = numeros.substring(primeiroIndice, primeiroIndice + intervalo).trim();
				if (numero.equals("*")) {
					numero = "0";
				} else {
					numero = numero.replaceAll("[^0-9]", "");
				}
				valores.add(numero);
			}
			
			/* Média dos valores */
			int media = 0;
			int quant = 0;
			for (int i = 0; i < colunas; i++) {
				int valor = Integer.parseInt(valores.get(i));
				if (valor > 0) quant++;
				media += valor;
			}
			if (quant == 0) {
				media = 0;
			} else {
				media /= quant;
			}
			valores.add(String.valueOf(media));
			
			/* Nome do host */
			valores.add(recebido.substring(divisor).trim());			
		} else if (recebido.startsWith("Rastreando a rota")) {
			String endereco = recebido.substring(recebido.indexOf("[") + 1, recebido.indexOf("]")).trim();
			String nome = recebido.substring(recebido.indexOf("para") + 4, recebido.indexOf("[")).trim();
			valores.add("Endereço do host: " + endereco
					+ "\nNome do host: " + nome
					+ "\nRastreando rota..."
			);
		} else if (recebido.startsWith("Rastreamento")) {
			valores.add("Rastreamento completo!");
		} else {
			return null;
		}
		return valores;
	}
	
	/* Fim dos filtros */
	
	public void limparSaida() {
		saida = null;
	}
	
	public String buscarGateway() {
		String gatewayEncontrado = null;
		for (String iEndereco : endereco.keySet()) {
			for (String iGateway : gateway.keySet()) {
				if (iGateway.equals(iEndereco)) {
					gatewayEncontrado = gateway.get(iGateway);
				}
			}
		}
		endereco.clear();
		gateway.clear();
		return gatewayEncontrado;
	}
	
	public String consertarCaracteres(String mensagem) {
		String caractereNulo = "";
		for (int i = 0; i < mensagem.length(); i++) {
			if (Character.getName(mensagem.charAt(i)).equals("NULL")) {
				//Descobrimento do caractere nulo
				caractereNulo = String.valueOf(mensagem.charAt(i));
				break;
			}
		}
		
		for (EnumCaracteres caractere : EnumCaracteres.values()) {
			mensagem = mensagem.replace(caractere.getErrado(), caractere.getCorreto());
			mensagem = mensagem.replace(caractereNulo, "");
		}
		return mensagem;
	}
	
	private void preencherLista(Map<String, String> lista, String recebido) {
		String preAdicao;
		int primeiroDigito = recebido.indexOf("1");
		if (primeiroDigito > 0) {
			int ultimoDigito = Ferramentas.ultimoIndiceRegex(recebido, "\\d");
			preAdicao = recebido.substring(primeiroDigito, ultimoDigito);
			if (preAdicao.contains(".")) {
				lista.put(preAdicao.substring(0, preAdicao.lastIndexOf(".")), preAdicao);
				//Key: primeiros três valores. Value: todos os valores.
			}
		}
	}	
}
