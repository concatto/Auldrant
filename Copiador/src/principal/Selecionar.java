package principal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Selecionar implements ActionListener{
	private BufferedWriter out;
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
	    fc.setDialogTitle("Selecionar destino");
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    fc.setAcceptAllFileFilterUsed(false);
	    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	try {
				out = new BufferedWriter(new FileWriter(Janela.driveID + "Eclipse\\path.txt"));
				Janela.local = fc.getSelectedFile().getPath() + "\\workspace";
				out.write(Janela.local);
				JOptionPane.showMessageDialog(fc, "Destino gravado com sucesso.", "Arquivo gravado!", JOptionPane.INFORMATION_MESSAGE);
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    }
	}
}
