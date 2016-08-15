package orionis.epsilon.vinculus.visao;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import orionis.epsilon.vinculus.controlador.Controlador;

public class Seletor extends AbstractFrame {
	public static final String TIP_CLIENTE = "Indica que você enviará dados a Servidores";
	public static final String TIP_SERVIDOR = "Indica que você receberá dados de Clientes";
	private JPanel painelBotoes = new JPanel();
	private JPanel painelSelecione = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 7));
	private JPanel painelTooltip = new JPanel();
	private JLabel selecione = new JLabel("Selecione sua função");
	private JLabel tooltip = new JLabel(TIP_SERVIDOR);
	private JButton botaoCliente = new JButton("Cliente");
	private JButton botaoServidor = new JButton("Servidor");
	
	public Seletor(Controlador controlador) {
		super(controlador);
		
		try {
			/* Breve exibição da SplashScreen */
			Thread.sleep(700);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void construct() {
		frame.setResizable(false);
		tooltip.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		painelTooltip.setLayout(new BoxLayout(painelTooltip, BoxLayout.LINE_AXIS));
		
		panel.add(painelSelecione);
		panel.add(painelBotoes);
		panel.add(painelTooltip);
		
		painelSelecione.add(selecione);
		painelTooltip.add(tooltip);
		painelTooltip.add(Box.createRigidArea(new Dimension(0, 13)));
		
		painelBotoes.add(botaoCliente);
		painelBotoes.add(botaoServidor);
		
		for (Component comp : painelBotoes.getComponents()) {
			JButton botao = (JButton) comp;
			botao.addActionListener(e -> controlador.transferirFluxo(botao.getText()));
			botao.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					String tip = botao.equals(botaoCliente) ? TIP_CLIENTE : TIP_SERVIDOR;
					setTooltip(tip);
				};
				
				public void mouseExited(MouseEvent e) {
					resetTooltip();
				};
			});
		}
		
		display();
		resetTooltip();
	}
	
	private void setTooltip(String texto) {
		tooltip.setText(texto);
		tooltip.setVisible(true);
	}
	
	private void resetTooltip() {
		tooltip.setText("");
		tooltip.setVisible(false);
	}
}