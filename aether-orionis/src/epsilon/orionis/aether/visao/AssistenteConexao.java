package epsilon.orionis.aether.visao;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import epsilon.orionis.aether.controle.Controlador;

public class AssistenteConexao extends JanelaSecundaria implements Observer {
	private JPanel painel = new JPanel();
	private JPanel painelEscolha = new JPanel();
	private JPanel painelInformacao = new JPanel();
	private JPanel painelEndereco = new JPanel();
	private JPanel painelGateway = new JPanel();
	private JPanel painelBotoes = new JPanel();
	private JTextField tEndereco = new JTextField(13);
	private JTextField tGateway = new JTextField(13);
	private JButton confirmar = new JButton("Confirmar");
	private JButton cancelar = new JButton("Cancelar");
	private JLabel escolha = new JLabel("Escolha a conexão:");
	private JLabel lEndereco = new JLabel("Endereço IP:");
	private JLabel lGateway = new JLabel("Gateway padrão:");
	private JComboBox<String> box = new JComboBox<>();
	private DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
	private AbstractDocument documento;
	
	private JTextField[] campos = {tEndereco, tGateway};
	
	public AssistenteConexao(Controlador controlador) {
		this.controlador = controlador;
	}
	
	@Override
	public void inicializar() {
		frame.setTitle("Assistente de Conexão");
		
		listeners = controlador.getListeners();
		frame.addWindowListener(listeners.getListenerFechamentoSecundario());
		frame.add(painel);
		painel.add(painelEscolha);
		painel.add(painelInformacao);
		painel.add(painelBotoes);
		painelEscolha.add(escolha);
		painelEscolha.add(box);
		painelInformacao.add(lEndereco);
		painelInformacao.add(painelEndereco);
		painelInformacao.add(lGateway);
		painelInformacao.add(painelGateway);
		painelBotoes.add(confirmar);
		painelBotoes.add(cancelar);
		
		painelEndereco.add(tEndereco);
		painelGateway.add(tGateway);
		
		for (JTextField campo : campos) {
			documento = (AbstractDocument) campo.getDocument();
			documento.setDocumentFilter(controlador.getFiltroNumeros());
		}
			
		painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
		painelInformacao.setLayout(new GridLayout(2, 2, 0, 0));
		painelInformacao.setBorder(BorderFactory.createEmptyBorder(7, 9, 5, 3));
		painelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		box.setModel(model);
		
		confirmar.addActionListener(listeners.getConfirmarAlteracao());
		cancelar.addActionListener(listeners.getCancelar());
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			String mensagem = arg.toString();
			if (mensagem.startsWith("Erro")) {
				JOptionPane.showMessageDialog(frame, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
			} else if (mensagem.startsWith("Alteração")) {
				JOptionPane.showMessageDialog(frame, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				frame.dispose();
			} else if (mensagem.startsWith("param")) {
				controlador.registrarInformacoes(mensagem);
			} else {
				model.addElement(mensagem);
			}
		}
	}
	
	public void exibir() {
		frame.setVisible(true);
		requisitarConexoes();
	}
	
	private void requisitarConexoes() {
		model.removeAllElements();
		controlador.realizarComando("netsh", "interface", "show", "interface");
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public String getGateway() {
		return tGateway.getText();
	}
	
	public String[] getInformacoes() {
		String local = "\"" + box.getItemAt(box.getSelectedIndex()) + "\"";
		String[] informacoes = {local, "static", tEndereco.getText(), tGateway.getText()};
		return informacoes;
	}

	@Override
	public void reiniciar() {
		for (JTextField campo : campos) {
			campo.setText("");
		}
	}
}
