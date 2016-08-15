package epsilon.orionis.aether.modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManipuladorArquivos {
	public ManipuladorArquivos() {
		
	}
	
	public List<String> lerDe(File arquivo) {
		List<String> lista = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new FileReader(arquivo))) {
			String linha = null;
			while ((linha = in.readLine()) != null) {
				lista.add(linha);
			}
			return lista;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> lerDe(EnumArquivos arquivo) {
		return lerDe(arquivo.getArquivo());
	}
	
	public void escreverPara(String mensagem, File arquivo, boolean sobrescrever) {
		if (!arquivo.exists()) {
			try {
				arquivo.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (BufferedWriter out = new BufferedWriter(new FileWriter(arquivo, !sobrescrever))) {
			out.write(mensagem);
			out.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void escreverPara(String mensagem, EnumArquivos arquivo, boolean sobrescrever) {
		escreverPara(mensagem, arquivo.getArquivo(), sobrescrever);
	}
	
	public void escreverPara(String[] mensagens, EnumArquivos arquivo, boolean sobrescrever) {
		for (String mensagem : mensagens) {
			escreverPara(mensagem, arquivo.getArquivo(), sobrescrever);
		}
	}
	
	public void escreverPara(List<String> mensagens, EnumArquivos arquivo, boolean sobrescrever) {
		for (String mensagem : mensagens) {
			escreverPara(mensagem + "\n", arquivo.getArquivo(), sobrescrever);
		}
	}
	
	public void neutralizarArquivo(EnumArquivos arquivo) {
		escreverPara("", arquivo, true);
	}
	
	public boolean deletarArquivo(EnumArquivos arquivo) {
		boolean sucesso = arquivo.getArquivo().delete();
		return sucesso;
	}
	
	public boolean renomearArquivo(EnumArquivos arquivo, EnumArquivos novoArquivo) {
		boolean sucesso = arquivo.getArquivo().renameTo(novoArquivo.getArquivo());
		return sucesso;
	}
}
