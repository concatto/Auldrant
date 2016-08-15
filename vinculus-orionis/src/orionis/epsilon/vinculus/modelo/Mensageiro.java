package orionis.epsilon.vinculus.modelo;

import java.io.File;
import java.net.Socket;

import orionis.epsilon.vinculus.controlador.Controlador;

public class Mensageiro {
	private Controlador controlador;
	
	public Mensageiro(Controlador controlador) {
		this.controlador = controlador;
	}

	public void comunicar(Socket origem, Object mensagem) {
		controlador.tratarMensagem(origem, mensagem);
	}
	
	public File obterDestino(String nome, Socket cliente) {
		return controlador.obterCaminho(nome, cliente);
	}
}
