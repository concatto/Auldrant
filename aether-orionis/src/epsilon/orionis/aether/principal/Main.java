package epsilon.orionis.aether.principal;

import javax.swing.UIManager;

import epsilon.orionis.aether.controle.Controlador;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Controlador();
	}
}
