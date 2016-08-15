package epsilon.orionis.aether.controle;

import java.util.Observable;

public class DirecionadorMensagens extends Observable {
	public DirecionadorMensagens() {
		
	}

	public void enviarMensagem(Object mensagem) {
		setChanged();
		notifyObservers(mensagem);
	}
}
