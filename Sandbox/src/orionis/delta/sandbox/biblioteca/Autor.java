package orionis.delta.sandbox.biblioteca;

public class Autor {
	private String nome;
	private String titulacao;
	
	public Autor(String nome, String titulacao) {
		this.nome = nome;
		this.titulacao = titulacao;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getTitulacao() {
		return titulacao;
	}
	
	public static Autor[] registrarAutores(String autores) {
		String[] valores = autores.split(";");
		Autor[] autoresProntos = new Autor[valores.length];
		
		for (int i = 0; i < valores.length; i++) {
			String[] info = valores[i].split(",");
			autoresProntos[i] = new Autor(info[0].trim(), info[1].trim());
		}
		
		return autoresProntos;
	}
}
