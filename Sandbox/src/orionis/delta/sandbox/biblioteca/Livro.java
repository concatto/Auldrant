package orionis.delta.sandbox.biblioteca;

import java.util.Date;

public class Livro extends Publicacao {
	private int edicao;
	private String editora;
	private String isbn;
	
	public Livro(Date data, String titulo, String[] referencias, Autor[] autores, int edicao, String editora, String isbn) {
		super(data, titulo, referencias, autores);
		
		this.edicao = edicao;
		this.editora = editora;
		this.isbn = isbn;
	}
	
	public int getEdicao() {
		return edicao;
	}
	
	public String getEditora() {
		return editora;
	}

	public String getIsbn() {
		return isbn;
	}
}
