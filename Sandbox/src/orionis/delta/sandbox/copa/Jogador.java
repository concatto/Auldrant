package orionis.delta.sandbox.copa;

import java.util.Date;

public class Jogador extends Pessoa {
	private String posicao;
	private String ladoPreferencia;
	
	public Jogador(String nome, Date nascimento, String escolaridade, float altura, float peso, String posicao, String ladoPreferencia) {
		super(nome, nascimento, escolaridade, altura, peso);
		
		this.posicao = posicao;
		this.ladoPreferencia = ladoPreferencia;
	}
	
	public String getPosicao() {
		return posicao;
	}
	
	public String getLadoPreferencia() {
		return ladoPreferencia;
	}
}
