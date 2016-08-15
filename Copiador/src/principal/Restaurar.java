package principal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JOptionPane;

public class Restaurar extends Comandos implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		int resposta = JOptionPane.showConfirmDialog(null, "Os arquivos de \n"
				+ Janela.local + "\nserão copiados para\n" + Janela.origem
				+ "\nDeseja continuar?", "Confirmação",
				JOptionPane.YES_NO_OPTION);
		if (resposta == JOptionPane.YES_OPTION) {
			copyFolder(new File(Janela.local), new File(Janela.origem));
		} else {
			return;
		}
	}

}
