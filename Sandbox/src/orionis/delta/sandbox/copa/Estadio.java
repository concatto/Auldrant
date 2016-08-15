package orionis.delta.sandbox.copa;

import java.util.Date;

public class Estadio {
	private Cidade cidade;
	private String nome;
	private Date fundacao;
	private int capacidade;
	
	public Estadio(Cidade cidade, String nome, Date fundacao, int capacidade) {
		this.cidade = cidade;
		this.nome = nome;
		this.fundacao = fundacao;
		this.capacidade = capacidade;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public String getNome() {
		return nome;
	}

	public Date getFundacao() {
		return fundacao;
	}

	public int getCapacidade() {
		return capacidade;
	}
}
