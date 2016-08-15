package orionis.epsilon.vinculus.visao;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class SimpleMessage {
	private static void createMessage(Component parent, String title, String message, int type) {
		JOptionPane pane = new JOptionPane(message, type);
		JDialog dialog = pane.createDialog(parent, title);
		dialog.setModal(false);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
	
	public static void createInformation(Component parent, String title, String message) {
		createMessage(parent, title, message, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void createError(Component parent, String title, String message) {
		createMessage(parent, title, message, JOptionPane.ERROR_MESSAGE);
	}
}