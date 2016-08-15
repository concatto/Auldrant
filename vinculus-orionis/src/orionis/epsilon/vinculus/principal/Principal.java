package orionis.epsilon.vinculus.principal;

import javax.swing.UIManager;

import orionis.epsilon.vinculus.controlador.Controlador;

public class Principal {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Controlador();
	}
}