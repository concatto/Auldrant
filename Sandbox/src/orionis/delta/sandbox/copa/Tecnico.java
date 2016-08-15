package orionis.delta.sandbox.copa;

import java.util.Date;

public class Tecnico extends Pessoa {
	private String nacionalidade;

	public Tecnico(String nome, Date nascimento, String escolaridade, float altura, float peso, String nacionalidade) {
		super(nome, nascimento, escolaridade, altura, peso);
		
		this.nacionalidade = nacionalidade;
	}
	
	public String getNacionalidade() {
		return nacionalidade;
	}
}
