package orionis.epsilon.vinculus.modelo;

public enum Mensagem {
	ARQUIVO,
	FIM,
	ACEITAR,
	REJEITAR,
	PORTA_INVALIDA("Porta inválida."),
	TRANSFERENCIA_INTERROMPIDA("O Servidor fechou a Conexão. Transferência cancelada."),
	CONEXAO_RECUSADA("O Servidor não está aceitando Conexões."),
	CONEXAO_INEXISTENTE("O Servidor não existe."),
	CONTEUDO_CLIPBOARD_INVALIDO("O conteúdo da Área de Transferência não é uma imagem."),
	NENHUM_ARQUIVO_SELECIONADO("Nenhum arquivo foi selecionado. Utilize o botão Selecionar."),
	ERRO_IO("Ocorreu um erro na escrita ou leitura de dados.");
	
	private String mensagem;
	
	private Mensagem() {
		this.mensagem = null;
	}
	
	private Mensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public String getMensagem() {
		return mensagem;
	}
}
