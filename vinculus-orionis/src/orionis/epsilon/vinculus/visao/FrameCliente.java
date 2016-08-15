package orionis.epsilon.vinculus.visao;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import orionis.epsilon.vinculus.controlador.Controlador;

public class FrameCliente extends AbstractMainframe {
	private static final String NENHUM_ARQUIVO = "Nenhum arquivo.";
	private static final String NENHUMA_PREVIA = "Nenhuma prévia.";
	private static final String PREVIA_INVALIDA = "Prévia inválida.";
	
	private ScrollableImage preview = new ScrollableImage(NENHUMA_PREVIA);
	private JFileChooser chooser = new JFileChooser();
	private JLabel labelEndereco = new JLabel("Endereço");
	private JLabel labelArquivo = new JLabel(NENHUM_ARQUIVO);
	private JButton adicionar = new JButton("Adicionar");
	private JButton selecionar = new JButton("Selecionar");
	private JButton prever = new JButton("Prever");
	private JButton transferirArquivo = new JButton("Transferir arquivo");
	private JButton transferirTela = new JButton("Transferir tela");
	private JPanel painelArquivo = new JPanel(bag);
	private JPanel painelTela = new JPanel(bag);
	private JPanel painelPreview = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 1));
	private JScrollPane panePreview = new JScrollPane(preview, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	private JTextField endereco = new JTextField(18);
	
	private File arquivoAtual;
	
	public FrameCliente(Controlador controlador) {
		super(controlador);
	}
	
	@Override
	public void construct() {		
		frame.setTitle("Cliente - vinculusOrionis");
		labelArquivo.setPreferredSize(new Dimension(160, 10));
		transferirArquivo.setEnabled(false);
		transferirTela.setEnabled(false);
		painelArquivo.setBorder(createCenteredBorder("Transferência de Arquivos", SMALL));
		painelTela.setBorder(createCenteredBorder("Transferência de Tela", MEDIUM));
		panePreview.setPreferredSize(new Dimension(195, 135));
		
		selecionar.addActionListener(e -> selecionarArquivo());
		prever.addActionListener(e -> {
			try {
				BufferedImage imagem = controlador.getImagemClipboard();
				preview.setImage(imagem);
			} catch (UnsupportedFlavorException e1) {
				preview.setText(PREVIA_INVALIDA);
			}
		});
		adicionar.addActionListener(e -> controlador.adicionarConexao(endereco.getText(), porta.getText()));
		transferirArquivo.addActionListener(e -> controlador.realizarTransferencia(lista.getSelectedValue(), arquivoAtual));
		transferirTela.addActionListener(e -> controlador.realizarTransferencia(lista.getSelectedValue(), preview.getSource()));
		
		populateMain();
		insertActions();
		insertConnection();
		display();
		
		endereco.requestFocusInWindow();
	}
	
	@Override
	protected void insertActions() {
		painelAcoes.add(painelArquivo);
		painelAcoes.add(Box.createRigidArea(new Dimension(0, 7)));
		painelAcoes.add(painelTela);
		
		constraints.insets = new Insets(0, 4, 0, 4);
		constraints.gridx = 0;
		painelArquivo.add(selecionar, constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		painelTela.add(painelPreview, constraints);
		
		constraints.gridx = 1;
		painelArquivo.add(labelArquivo, constraints);
		
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0;
		painelTela.add(prever);
		
		constraints.gridx = 2;
		painelArquivo.add(transferirArquivo, constraints);
		
		constraints.gridx = 2;
		painelTela.add(transferirTela, constraints);
		
		painelPreview.add(panePreview);
	}
	
	@Override
	protected void insertConnection() {
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		painelConector.add(labelEndereco, constraints);
		
		constraints.gridx = 1;
		painelConector.add(labelPorta, constraints);
		
		constraints.ipady = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		painelConector.add(endereco, constraints);
		
		constraints.gridx = 1;
		painelConector.add(porta, constraints);
		
		constraints.ipady = 0;
		constraints.ipadx = 20;
		constraints.gridx = 2;
		constraints.gridy = 1;
		painelConector.add(adicionar, constraints);
		constraints.ipadx = 0;
	}
	
	@Override
	protected void finishTransfer() {
		SimpleMessage.createInformation(frame, "Sucesso", "Transferência concluída com sucesso!");
		arquivoAtual = null;
		preview.setText(NENHUMA_PREVIA);
		labelArquivo.setText(NENHUM_ARQUIVO);
		updateList();
	}
	
	@Override
	public void resetFields() {
		porta.setText(null);
		endereco.setText(null);
		endereco.requestFocusInWindow();
	}
	
	@Override
	public void changeActions(boolean isClosed) {
		transferirArquivo.setEnabled(!isClosed);
		transferirTela.setEnabled(!isClosed);
		finishChanges(isClosed);
	}
	
	private void selecionarArquivo() {
		int opcao = chooser.showOpenDialog(frame);
		if (opcao == JFileChooser.APPROVE_OPTION) {
			arquivoAtual = chooser.getSelectedFile();
			labelArquivo.setText(arquivoAtual.getName());
		}
	}
	
	public void limparEntrada() {
		endereco.setText(null);
		porta.setText(null);
	}
	
	public String getEndereco() {
		return endereco.getText();
	}
	
	public File getArquivo() {
		return arquivoAtual;
	}
}
