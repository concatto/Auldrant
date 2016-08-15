package epsilon.orionis.aegis.enumeradores;

public enum Comunicados {
	SUCESSOVACINA("Dispositivo vacinado com sucesso."),
	ESTAVACINADO("Falha na vacina��o do dispositivo.\nO dispositivo j� est� vacinado."),
	SUCESSOREMOCAO("Arquivo autorun.inf removido com sucesso."),
	NAOEXISTE("O arquivo n�o existe. Seu dispositivo est� pronto para ser vacinado."),
	FALHAREMOCAO("O arquivo n�o p�de ser removido.");
	;
	
	String mensagem;
	private Comunicados(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public String getMensagem() {
		return mensagem;
	}
}
