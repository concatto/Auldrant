package orionis.epsilon.vinculus.visao;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import orionis.epsilon.vinculus.controlador.Controlador;
import orionis.epsilon.vinculus.controlador.ResourceLoader;
import orionis.epsilon.vinculus.modelo.Mensagem;
import orionis.epsilon.vinculus.modelo.SocketInformation;

public abstract class AbstractMainframe extends AbstractFrame {
	@SuppressWarnings("serial")
	public final class DisconnectButton extends JButton {
		private DisconnectButton(String text) {
			super(text);
		}

		public void morph(boolean socketClosed) {
			String text = socketClosed ? REMOVE : CLOSE;
			setText(text);
		}
	}
	protected static final String CLOSE = "Fechar conexão";
	protected static final String REMOVE = "Remover conexão";
	protected static final int SMALL = 1;
	protected static final int LARGE = 2;
	protected static final int MEDIUM = 3;
	
	protected DefaultListModel<SocketInformation> modelo = new DefaultListModel<>();
	protected DisconnectButton desconectar = new DisconnectButton(CLOSE);
	protected GridBagConstraints constraints = new GridBagConstraints();
	protected GridBagLayout bag = new GridBagLayout();
	protected JLabel labelPorta = new JLabel("Porta");
	protected JPanel painelConector = new JPanel(new GridBagLayout());
	protected JPanel painelAcoes = new JPanel();
	protected JTextField porta = new JTextField(8);
	protected ConnectionList lista = new ConnectionList();
	
	private ConnectionRenderer renderer = new ConnectionRenderer();
	private JMenu arquivo = new JMenu("Arquivo");
	private JMenuBar barra = new JMenuBar();
	private JMenuItem sairTray = new JMenuItem("Sair");
	private JMenuItem sair = new JMenuItem("Sair");
	private JMenuItem exibir = new JMenuItem("Exibir");
	private JPanel superPrincipal = new JPanel(new GridBagLayout());
	private JPanel superLista = new JPanel(new BorderLayout());
	private JPanel painelBotao = new JPanel();
	private JPopupMenu popup = new JPopupMenu();
	private JScrollPane paneLista = new JScrollPane(lista);
	private JSeparator separador = new JSeparator(JSeparator.HORIZONTAL);
	private ProgressFrame progressBar = new ProgressFrame();
	private SystemTray tray = SystemTray.getSystemTray();
	private TrayIcon icone = new TrayIcon(new ImageIcon(ResourceLoader.loadResource("/16.png")).getImage(), frame.getTitle());
	
	public AbstractMainframe(Controlador controlador) {
		super(controlador);
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));
		desconectar.setEnabled(false);
		frame.setResizable(false);
		
		/* False para iconified, True para o resto */
		frame.addWindowStateListener(e -> frame.setVisible(e.getNewState() != JFrame.ICONIFIED));
				
		icone.addActionListener(e -> exibir.doClick());
		sairTray.addActionListener(e -> sair.doClick());
		
		exibir.addActionListener(e -> {
			frame.setVisible(true);
			frame.setExtendedState(JFrame.NORMAL);
		});
		
		icone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.setLocation(e.getPoint());
					popup.setInvoker(popup);
					popup.setVisible(true);
				}
			}
		});
		
		sair.addActionListener(e -> {
			destroy();
			terminate();
		});
		
		desconectar.addActionListener(e -> SwingUtilities.invokeLater(this::handleDisconnect));
		
		lista.addListSelectionListener(this::handleListEvent);
		
		try {
			tray.add(icone);
			popup.add(exibir);
			popup.addSeparator();
			popup.add(sairTray);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}
	
	private void handleDisconnect() {
		SocketInformation info = lista.getSelectedValue();
		if (info == null) return;
		if (desconectar.getText().equals(REMOVE)) {
			controlador.removerSocket(info);
			try {
				modelo.removeElement(info);
			} catch (NullPointerException e1) {
				/* Erro no Swing? Não fazer nada */
			}
			desconectar.setEnabled(false);
		} else {
			controlador.fecharSocket(info);
		}
	}
	
	private void handleListEvent(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			SocketInformation info = lista.getSelectedValue();
			boolean fechado = info == null ? true : info.isClosed();
			changeActions(fechado);
		}
	}
	
	protected void populateMain() {
		painelAcoes.setLayout(new BoxLayout(painelAcoes, BoxLayout.PAGE_AXIS));
		
		constraints.insets = new Insets(10, 4, 4, 4);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;
		superPrincipal.add(separador, constraints);
		
		constraints.insets = new Insets(2, 4, 2, 4);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		superPrincipal.add(painelConector, constraints);
		
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.gridy = 2;
		superPrincipal.add(painelAcoes, constraints);
		
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridheight = 3;
		constraints.gridx = 1;
		constraints.gridy = 0;
		superPrincipal.add(superLista, constraints);
		
		icone.setToolTip(frame.getTitle());
		arquivo.add(sair);
		populateChildren();
	}

	private void populateChildren() {
		paneLista.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		paneLista.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		superLista.setBorder(BorderFactory.createTitledBorder("Conexões"));
		painelBotao.setBorder(BorderFactory.createEmptyBorder(5, 3, 2, 3));
		desconectar.setPreferredSize(new Dimension(130, 23));
		
		lista.setModel(modelo);
		lista.setCellRenderer(renderer);
		
		frame.setJMenuBar(barra);
		panel.add(superPrincipal);
		barra.add(arquivo);
		
		superLista.add(paneLista, BorderLayout.CENTER);
		superLista.add(painelBotao, BorderLayout.SOUTH);
		
		painelBotao.add(desconectar);
	}
	
	protected Border createCenteredBorder(String title, int size) {
		Border border = null;
		switch (size) {
		case SMALL:
			border = BorderFactory.createEmptyBorder(1, 2, 6, 2);
			break;
		case LARGE:
			border = BorderFactory.createEmptyBorder(8, 8, 14, 8);
			break;
		case MEDIUM:
			border = BorderFactory.createEmptyBorder(4, 3, 7, 3);
			break;
		}
		Border titled = BorderFactory.createTitledBorder(null, title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		return BorderFactory.createCompoundBorder(titled, border);
	}
	
	protected void updateList() {
		SwingUtilities.invokeLater(lista::updateUI);
	}
	
	public void displayProgress(String title, String text, boolean determinate) {
		progressBar.prepare(title, text, determinate);
		progressBar.display();
	}
	
	public void updateProgress(int valor) {
		SwingUtilities.invokeLater(() -> {
			progressBar.updateProgress(valor);
			if (valor == progressBar.getMaximumValue()) {
				progressBar.close();
				finishTransfer();
			}
		});
	}
	
	public void destroyProgress() {
		progressBar.setVisible(false);
		progressBar.dispose();
	}
	
	public void insertInformation(SocketInformation informacao) {
		if (informacao == null) {
			SimpleMessage.createError(frame, "Erro", "Endereço não encontrado.");
		} else {
			modelo.addElement(informacao);
			updateList();
		}
	}
	
	public DefaultListModel<SocketInformation> getListModel() {
		return modelo;
	}
	
	public String getPorta() {
		return porta.getText();
	}
	
	public SocketInformation getInfoBySocket(Socket socket) {
		for (int i = 0; i < modelo.size(); i++) {
			SocketInformation info = modelo.get(i);
			if (socket.getInetAddress().equals(info.getLiteralAddress()) && socket.getPort() == info.getLiteralPort()) {
				return info;
			}
		}
		return null;
	}
	
	protected void finishChanges(boolean isClosed) {
		desconectar.setEnabled(true);
		desconectar.morph(isClosed);
		updateList();
	}
	
	public void publishError(Mensagem error) {
		JOptionPane.showMessageDialog(frame, error.getMensagem(), "Erro", JOptionPane.ERROR_MESSAGE);
	}
	
	public abstract void resetFields();
	public abstract void changeActions(boolean isClosed);
	protected abstract void finishTransfer();
	protected abstract void insertActions();
	protected abstract void insertConnection();
}
