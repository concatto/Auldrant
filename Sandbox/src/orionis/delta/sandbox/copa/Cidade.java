package orionis.delta.sandbox.copa;

public class Cidade {
	private String nome;
	private String estado;
	private int populacao;
	private float idh;
	
	public Cidade(String nome, String estado, int populacao, float idh) {
		this.nome = nome;
		this.estado = estado;
		this.populacao = populacao;
		this.idh = idh;
	}

	public String getNome() {
		return nome;
	}

	public String getEstado() {
		return estado;
	}

	public int getPopulacao() {
		return populacao;
	}

	public float getIdh() {
		return idh;
	}
}
