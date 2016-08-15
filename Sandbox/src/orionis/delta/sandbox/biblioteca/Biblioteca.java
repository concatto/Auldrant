package orionis.delta.sandbox.biblioteca;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Biblioteca {
	private Publicacao[] publicacoes = new Publicacao[65535];
	private int indice = 0;
	
	public Biblioteca() {
		while(true) {
			String in = JOptionPane.showInputDialog("Digite 1 para cadastrar um livro, 2 para cadastrar um artigo ou 3 para sair.");
			
			if (in.equals("1")) {
				cadastrarLivro();
			} else if (in.equals("2")) {
				cadastrarArtigo();
			} else if (in.equals("3")) {
				break;
			}
		}
		
		for (Publicacao p : publicacoes) {
			if (p == null) break;
			System.out.println(Arrays.toString(p.getReferencias()));
		}
	}
	
	private void cadastrarLivro() {
		JTextField edicao = new JTextField();
		JTextField editora = new JTextField();
		JTextField isbn = new JTextField();
		
		Object[] campos = {"Edição do livro", edicao, "Nome da editora", editora, "ISBN", isbn};
		Object[] shared = getInformacoesCompartilhadas();
		Object[] completo = Arrays.copyOf(shared, campos.length + shared.length);
		System.arraycopy(campos, 0, completo, shared.length, campos.length);
		
		JOptionPane.showConfirmDialog(null, completo);
		
		String[] resultado = new String[completo.length / 2];
		for (int i = 0; i < resultado.length; i++) {
			resultado[i] = ((JTextField) completo[1 + (i * 2)]).getText();
		}
		
		try {
			publicacoes[indice] = new Livro(
				new SimpleDateFormat("dd/MM/yyyy").parse(resultado[0]),
				resultado[1],
				resultado[2].split(","),
				Autor.registrarAutores(resultado[3]),
				Integer.parseInt(resultado[4]),
				resultado[5],
				resultado[6]
			);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		indice++;
	}
	
	private void cadastrarArtigo() {
		JTextField resumo = new JTextField();
		
		Object[] campos = {"Resumo do artigo", resumo};
		Object[] shared = getInformacoesCompartilhadas();
		Object[] completo = Arrays.copyOf(shared, campos.length + shared.length);
		System.arraycopy(campos, 0, completo, shared.length, campos.length);
		
		JOptionPane.showConfirmDialog(null, completo);
		
		String[] resultado = new String[completo.length / 2];
		for (int i = 0; i < resultado.length; i++) {
			resultado[i] = ((JTextField) completo[1 + (i * 2)]).getText();
		}
		
		try {
			publicacoes[indice] = new Artigo(
				new SimpleDateFormat("dd/MM/yyyy").parse(resultado[0]),
				resultado[1],
				resultado[2].split(","),
				Autor.registrarAutores(resultado[3]),
				resultado[4]
			);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		indice++;
	}
	
	private Object[] getInformacoesCompartilhadas() {
		JTextField data = new JTextField();
		JTextField titulo = new JTextField();
		JTextField referencias = new JTextField();
		JTextField autores = new JTextField();
		
		Object[] campos = {
			"Data de publicação", data,
			"Título da publicação", titulo,
			"Referências, separadas por vírgulas", referencias,
			"Autores, com o seguinte formato: autor, titulação; autor, titulação; autor...", autores
		};
		return campos;
	}
}
