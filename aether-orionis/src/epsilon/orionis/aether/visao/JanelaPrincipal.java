package epsilon.orionis.aether.visao;

import epsilon.orionis.aether.controle.Controlador;
import epsilon.orionis.aether.controle.Listeners;

public abstract class JanelaPrincipal {
	protected SFrame frame = new SFrame(this);
	protected Controlador controlador;
	protected Listeners listeners;
	
	protected abstract void inicializar();
}
