package orionis.delta.sandbox.copa;

import java.util.Date;

public class Registro {
	private Time[] times = new Time[32];
	private Estadio[] estadios = new Estadio[100];
	private int indiceTime = 0;
	private int indiceEstadio = 0;
	
	public void registrarTime() {
		String pais = Campeonato.lerTexto("Digite o país a que o time pertence");
		int quantidadeTitulos = Campeonato.lerInt("Digite a quantidade de títulos que o time possui");
		int posicaoFifa = Campeonato.lerInt("Digite a posição do time no ranking da FIFA");
		Tecnico tecnico = registrarTecnico();
		Jogador[] jogadores = new Jogador[Time.QUANTIDADE_JOGADORES];
		
		for (int i = 0; i < jogadores.length; i++) {
			jogadores[i] = registrarJogador(i + 1);
		}
		
		times[indiceTime] = new Time(pais, quantidadeTitulos, posicaoFifa, tecnico, jogadores);
		indiceTime++;
	}
	
	private Jogador registrarJogador(int indice) {
		String nome = Campeonato.lerTexto("Digite o nome do " + indice + "º jogador");
		Date nascimento = Campeonato.lerData("Digite a data de nascimento do " + indice + "º jogador (dd/mm/aaaa)");
		String escolaridade = Campeonato.lerTexto("Digite a escolaridade do " + indice + "º jogador");
		float altura = Campeonato.lerFloat("Digite a altura do " + indice + "º jogador (em metros)");
		float peso = Campeonato.lerFloat("Digite o peso do " + indice + "º jogador (em quilos)");
		String posicao = Campeonato.lerTexto("Digite a posição do " + indice + "º jogador");
		String ladoPreferencia = Campeonato.lerTexto("Digite o lado preferencial do " + indice + "º jogador");
		
		Jogador jogador = new Jogador(nome, nascimento, escolaridade, altura, peso, posicao, ladoPreferencia);
		return jogador;
	}

	private Tecnico registrarTecnico() {
		String nome = Campeonato.lerTexto("Digite o nome do técnico");
		Date nascimento = Campeonato.lerData("Digite a data de nascimento do técnico (dd/mm/aaaa)");
		String escolaridade = Campeonato.lerTexto("Digite a escolaridade do técnico");
		float altura = Campeonato.lerFloat("Digite a altura do técnico (em metros)");
		float peso = Campeonato.lerFloat("Digite o peso do técnico (em quilos)");
		String nacionalidade = Campeonato.lerTexto("Digite a nacionalidade do técnico");
		
		Tecnico tecnico = new Tecnico(nome, nascimento, escolaridade, altura, peso, nacionalidade);
		return tecnico;
	}
	
	private Cidade registrarCidade() {
		String nome = Campeonato.lerTexto("Digite o nome da cidade-sede");
		String estado = Campeonato.lerTexto("Digite o Estado em que a cidade se encontra");
		int populacao = Campeonato.lerInt("Digite a população da cidade");
		float idh = Campeonato.lerFloat("Digite o IDH (Índice de Desenvolvimento Humano) da cidade");
		
		Cidade cidade = new Cidade(nome, estado, populacao, idh);
		return cidade;
	}
	
	public void registrarEstadio() {
		Cidade cidade = registrarCidade();
		
		String nome = Campeonato.lerTexto("Digite o nome do estádio");
		Date fundacao = Campeonato.lerData("Digite a data de fundação do estádio (dd/mm/aaaa)");
		int capacidade = Campeonato.lerInt("Digite a capadidade do estádio");
		
		estadios[indiceEstadio] = new Estadio(cidade, nome, fundacao, capacidade);
		indiceEstadio++;
	}

	public Time[] getTimes() {
		return times;
	}

	public Estadio[] getEstadios() {
		return estadios;
	}

	public int getIndiceTime() {
		return indiceTime;
	}

	public int getIndiceEstadio() {
		return indiceEstadio;
	}
}
