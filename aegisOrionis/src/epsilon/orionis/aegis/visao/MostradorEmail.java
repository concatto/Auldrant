package epsilon.orionis.aegis.visao;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import epsilon.orionis.aegis.enumeradores.ResultadoAutenticacao;
import epsilon.orionis.aegis.enumeradores.Solicitacoes;
import epsilon.orionis.aegis.principal.Controlador;

public class MostradorEmail implements Observer {
	private JDialog dialog = new JDialog();
	private JProgressBar barra = new JProgressBar();
	private JPanel painel = new JPanel();

	private Controlador controlador;
	
	public MostradorEmail(Controlador controlador) {
		this.controlador = controlador;
		dialog.setAlwaysOnTop(true);
		dialog.setTitle("Enviando...");
		barra.setIndeterminate(true);
		painel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		painel.add(barra);
		dialog.add(painel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
	}

	@Override
	public void update(Observable arg0, Object msg) {
		if (msg instanceof ResultadoAutenticacao) {
			dialog.dispose();
			switch (ResultadoAutenticacao.valueOf(msg.toString())) {
			case SUCESSO:
				JOptionPane.showMessageDialog(null, "E-mail enviado com sucesso. Obrigado pela contribuição!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				break;
			case FALHA:
				JOptionPane.showMessageDialog(null, "Falha no envio do e-mail. Verifique seus dados e tente novamente.",  "Falha", JOptionPane.ERROR_MESSAGE);
				break;
			}
			controlador.concluirEmail();
		} else if (msg instanceof Solicitacoes) {
			dialog.setVisible(true);
		}
	}

}
