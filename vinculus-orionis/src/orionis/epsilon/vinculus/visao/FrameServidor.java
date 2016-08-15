package orionis.epsilon.vinculus.visao;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import orionis.epsilon.vinculus.controlador.Controlador;

public class FrameServidor extends AbstractMainframe {
	private JButton iniciar = new JButton("Iniciar servidor");
	private JButton fechar = new JButton("Fechar servidor");
	private JButton copiar = new JButton("Copiar");
	@SuppressWarnings("serial")
	private JFileChooser chooser = new JFileChooser() {
		@Override
		public void approveSelection() {
			File arquivo = getSelectedFile();
			if (arquivo != null && arquivo.exists()) {
				int resposta = JOptionPane.showConfirmDialog(this, "O arquivo " + arquivo.getName() + " já existe. Deseja sobrescrever?", "Sobrescrever arquivo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (resposta != JOptionPane.YES_OPTION) return;
			}
			
			super.approveSelection();
		};
	};
	private JLabel labelIndicadora = new JLabel("Endereço IP:");
	private JLabel labelEndereco = new JLabel("Servidor fechado.");
	private JPanel painelTexto = new JPanel(bag);
	private JPanel painelEndereco = new JPanel(bag);
	private JTextArea log = new JTextArea();
	private JScrollPane paneTexto = new JScrollPane(log);
	
	public FrameServidor(Controlador controlador) {
		super(controlador);
	}

	@Override
	public void construct() {		
		frame.setTitle("Servidor - vinculusOrionis");
		log.setRows(9);
		log.setLineWrap(true);
		log.setEditable(false);
		log.setWrapStyleWord(true);
		log.setFont(lista.getFont());
		log.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 5));
		log.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		((DefaultCaret) log.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		fechar.setEnabled(false);
		copiar.setEnabled(false);
		paneTexto.getVerticalScrollBar().setUnitIncrement(5);
		labelEndereco.setHorizontalAlignment(SwingConstants.LEFT);
		painelTexto.setBorder(createCenteredBorder("Registro de Eventos", LARGE));
		painelEndereco.setBorder(createCenteredBorder("Endereço Externo", SMALL));
		
		iniciar.addActionListener(e -> controlador.abrirServidor(porta.getText()));
		fechar.addActionListener(e -> controlador.fecharServidor());
		copiar.addActionListener(e -> {
			StringSelection content = new StringSelection(labelEndereco.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
			publicarMensagem("Endereço copiado para a Área de Transferência.");
		});
		populateMain();
		insertActions();
		insertConnection();
		display();
		
		porta.requestFocusInWindow();
	}

	@Override
	protected void insertActions() {
		painelAcoes.add(painelTexto);
		painelAcoes.add(Box.createRigidArea(new Dimension(0, 3)));
		painelAcoes.add(painelEndereco);
		
		constraints.insets = new Insets(0, 4, 0, 4);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weighty = 1;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		painelTexto.add(paneTexto, constraints);
		
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0;
		painelEndereco.add(labelIndicadora, constraints);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 1;
		painelEndereco.add(labelEndereco, constraints);
		
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0;
		constraints.gridx = 2;
		painelEndereco.add(copiar, constraints);
	}

	@Override
	protected void insertConnection() {
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridheight = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		painelConector.add(labelPorta, constraints);
		
		constraints.ipady = 1;
		constraints.gridy = 1;
		painelConector.add(porta, constraints);
		
		constraints.ipady = 0;
		constraints.gridx = 1;
		painelConector.add(iniciar, constraints);
		
		constraints.gridx = 2;
		painelConector.add(fechar, constraints);
	}

	public void setEndereco(String endereco) {
		labelEndereco.setText(endereco);
	}
	
	public void alterarAbrir(boolean status) {
		porta.setEnabled(status);
		iniciar.setEnabled(status);
		if (status) {
			resetFields();
		}
	}
	
	@Override
	public void resetFields() {
		porta.setText(null);
		porta.requestFocusInWindow();
	}
	
	public void alterarFechar(boolean status) {
		fechar.setEnabled(status);
		copiar.setEnabled(status);
	}
	
	public void publicarMensagem(String message) {
		if (log.getText().isEmpty()) {
			log.append(message);
		} else {
			log.append("\n" + message);
		}
		JScrollBar barraVertical = paneTexto.getVerticalScrollBar();
		BoundedRangeModel modeloBarra = barraVertical.getModel();
		if ((modeloBarra.getValue() + modeloBarra.getExtent()) >= (modeloBarra.getMaximum() - 5)) {
			log.setCaretPosition(log.getDocument().getLength());
		}
	}

	public File salvarArquivo(String nome, String remetente) {
		int opcao = JOptionPane.showConfirmDialog(frame, "O Cliente " + remetente + " deseja enviar\no arquivo " + nome + ".\nAceitar?", "Transferência de Arquivo", JOptionPane.YES_NO_OPTION);
		if (opcao != JOptionPane.OK_OPTION) return null;
		
		chooser.setSelectedFile(new File(nome));
		int opcaoArquivo = chooser.showSaveDialog(frame);
		if (opcaoArquivo != JFileChooser.APPROVE_OPTION) return null;
		
		SwingUtilities.invokeLater(() -> {
			displayProgress("Recebendo", "Recebendo arquivo...", true);
		});
		return chooser.getSelectedFile();
	}

	@Override
	protected void finishTransfer() {
		SimpleMessage.createInformation(frame, "Sucesso", "Arquivo recebido com sucesso.");
		updateList();
	}

	@Override
	public void changeActions(boolean isClosed) {
		finishChanges(isClosed);
	}
}