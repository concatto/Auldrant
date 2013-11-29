package principal;

import javax.swing.SwingUtilities;

public class Programa {
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
