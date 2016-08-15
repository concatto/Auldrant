package orionis.delta.sandbox.biblioteca;

import java.util.Date;

public class Artigo extends Publicacao {
	private String resumo;

	public Artigo(Date data, String titulo, String[] referencias, Autor[] autores, String resumo) {
		super(data, titulo, referencias, autores);
		
		this.resumo = resumo;
	}
	
	public String getResumo() {
		return resumo;
	}
}
