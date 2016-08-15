package orionis.delta.sandbox.copa;

public class Time {
	public static final int QUANTIDADE_JOGADORES = 11;
	private String pais;
	private int quantidadeTitulos;
	private int posicaoFifa;
	private Tecnico tecnico;
	private Jogador[] jogadores = new Jogador[QUANTIDADE_JOGADORES];
	
	public Time(String pais, int quantidadeTitulos, int posicaoFifa, Tecnico tecnico, Jogador[] jogadores) {
		this.pais = pais;
		this.quantidadeTitulos = quantidadeTitulos;
		this.posicaoFifa = posicaoFifa;
		this.tecnico = tecnico;
		this.jogadores = jogadores;
	}
	
	public String getPais() {
		return pais;
	}
	
	public int getQuantidadeTitulos() {
		return quantidadeTitulos;
	}
	
	public int getPosicaoFifa() {
		return posicaoFifa;
	}
	
	public Tecnico getTecnico() {
		return tecnico;
	}
	
	public Jogador[] getJogadores() {
		return jogadores;
	}
}
