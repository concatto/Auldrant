package main;

import javax.swing.SwingUtilities;

public class Principal {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				@SuppressWarnings("unused")
				Janela janela = new Janela();
			}
		});
	}
}
