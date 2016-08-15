package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Gerenciador {
	public static void reescreverArquivo(String alteracao, int index) {
		BufferedWriter out = null;
		BufferedReader in = null;
		File arquivoOriginal = new File("res/database.txt");
		File arquivoTemporario = new File("res/temp.txt");
		try {
			out = new BufferedWriter(new FileWriter(arquivoTemporario));
			in = new BufferedReader(new FileReader(arquivoOriginal));
			
			String linha;
			int indexValor = (alteracao == "true" || alteracao == "false") ? 1 : 0;
			for (int i = 0; (linha = in.readLine()) != null; i++) {
				if (i == index) {
					String[] valores = linha.split("\\s");
					valores[indexValor] = alteracao;
					linha = valores[0] + " " + valores[1];
				}
				if (linha != null) {
					out.write(linha);
					out.newLine();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				arquivoOriginal.delete();
				arquivoTemporario.renameTo(arquivoOriginal);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (NullPointerException e2) {
				e2.printStackTrace();
			}
		}
	}
}
