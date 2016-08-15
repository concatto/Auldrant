package principal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class Copiar extends Comandos implements ActionListener {
	private JLabel copiando = new JLabel("Copiando arquivos...");
	protected static JProgressBar barra = new JProgressBar();
	protected static JFrame frameBarra = new JFrame("Progresso");
	private JPanel panelBarra = new JPanel();
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int resposta = JOptionPane.showConfirmDialog(null, "Os arquivos de \n" + Janela.origem + "\nserão copiados para\n" + Janela.local + "\nA pasta de destino será deletada antes do processo. Deseja continuar?", "Confirmação", JOptionPane.YES_NO_OPTION);
		if (resposta == JOptionPane.YES_OPTION) {
			count(new File(Janela.origem));
			
			@SuppressWarnings("rawtypes")
			SwingWorker worker = new SwingWorker(){
				@Override
				protected Object doInBackground() throws Exception {
					copyFolder(new File(Janela.origem), new File(Janela.local));
					return null;
				}
			};

			worker.execute();
			
			frameBarra.setResizable(false);
			frameBarra.add(panelBarra);
			panelBarra.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			panelBarra.setLayout(new BoxLayout(panelBarra, BoxLayout.Y_AXIS));
			copiando.setAlignmentX(Component.CENTER_ALIGNMENT);
			panelBarra.add(copiando);
			panelBarra.add(Box.createRigidArea(new Dimension(1, 10)));
			panelBarra.add(barra);
			frameBarra.pack();
			frameBarra.setLocationRelativeTo(null);
			frameBarra.setVisible(true);
		} else {
			return;
		}
	}
}
