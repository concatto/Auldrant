package principal;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Programa {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unused")
		Janela janela = new Janela();
		JOptionPane.showMessageDialog(null, "abc");
	}
}
