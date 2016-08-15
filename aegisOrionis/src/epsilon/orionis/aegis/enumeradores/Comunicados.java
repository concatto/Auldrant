package epsilon.orionis.aegis.enumeradores;

public enum Comunicados {
	SUCESSOVACINA("Dispositivo vacinado com sucesso."),
	ESTAVACINADO("Falha na vacinação do dispositivo.\nO dispositivo já está vacinado."),
	SUCESSOREMOCAO("Arquivo autorun.inf removido com sucesso."),
	NAOEXISTE("O arquivo não existe. Seu dispositivo está pronto para ser vacinado."),
	FALHAREMOCAO("O arquivo não pôde ser removido.");
	;
	
	String mensagem;
	private Comunicados(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public String getMensagem() {
		return mensagem;
	}
}
