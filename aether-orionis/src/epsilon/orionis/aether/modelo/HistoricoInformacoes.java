package epsilon.orionis.aether.modelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoricoInformacoes {
	private List<String> informacoes;
	private SimpleDateFormat formatador;
	
	private ManipuladorArquivos manipulador;
	
	public HistoricoInformacoes(ManipuladorArquivos manipulador) {
		this.manipulador = manipulador;
		formatador = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
	}

	public void gravarInformacoes() {
		this.informacoes = manipulador.lerDe(EnumArquivos.ENDERECOS);
	}

	public List<String> getRegistros() {
		List<String> dados = new ArrayList<>();
		for (String string : informacoes) {
			if (string.startsWith(EnumInformacoes.TEMPO.getCodigo())) {
				String tempo = string.substring(string.indexOf(":") + 1);
				Date data = new Date(Long.parseLong(tempo) * 1000);
				dados.add(formatador.format(data));
			}
		}
		return dados;
	}
	
	public List<String> getInformacoesCompletas() {
		return informacoes;
	}
	
	public void deletarRegistro(String data) {
		long timestamp = converterData(data);
		int indice = procurarIndice(timestamp);
		
		for (int i = 0; i < informacoes.size(); i++) {
			if (i < indice || i > indice + 5) {
				String mensagem = informacoes.get(i);
				manipulador.escreverPara(mensagem, EnumArquivos.TEMPORARIO, false);
			}
		}
		
		manipulador.deletarArquivo(EnumArquivos.ENDERECOS);
		manipulador.renomearArquivo(EnumArquivos.TEMPORARIO, EnumArquivos.ENDERECOS);
	}
	
	public String[] getInformacoes(String data) {
		long timestamp = converterData(data);
		
		String[] dados = new String[4];
		int indice = procurarIndice(timestamp) + 1;
		
		for (int i = indice, j = 0; i < indice + dados.length; i++, j++) {
			dados[j] = informacoes.get(i);
		}
		return dados;
	}

	private long converterData(String data) {
		long timestamp = 0;
		try {
			timestamp = formatador.parse(data).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
	private int procurarIndice(long timestamp) {
		for (int i = 0; i < informacoes.size(); i++) {
			if (informacoes.get(i).contains(String.valueOf(timestamp))) {
				return i;
			}
		}
		return -1;
	}
}
