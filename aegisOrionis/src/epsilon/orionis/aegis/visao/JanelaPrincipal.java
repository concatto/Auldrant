package epsilon.orionis.aegis.visao;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import epsilon.orionis.aegis.enumeradores.Comunicados;
import epsilon.orionis.aegis.enumeradores.Solicitacoes;
import epsilon.orionis.aegis.principal.Controlador;

@SuppressWarnings("serial")
public class JanelaPrincipal extends JFrame implements Observer {
	private JTextArea areaTexto = new JTextArea();
	private JComboBox<File> boxDrives = new JComboBox<>();
	private JLabel labelEmail = new JLabel("Descobriu um vírus? Envie para atualizarmos a vacina");
	private JLabel labelDispositivo = new JLabel("<html><b>Dispositivos: </b></html>");
	private JPanel painelLogo = new PainelLogo();
	private JPanel painelTexto = new JPanel();
	private JPanel painelBotoes = new JPanel();
	private JPanel painelEmail = new JPanel();
	private JButton botaoVacinar = new JButton("Vacinar");
	private JButton botaoRemover = new JButton("Remover");
	private JButton botaoEmail = new JButton("Enviar E-mail");
	
	private Component[] componentesFuncoes;
	private Controlador controlador;
	
	public JanelaPrincipal(Controlador controlador) {
		this.controlador = controlador;
		List<Image> icones = new ArrayList<Image>();
		icones.add(new ImageIcon("res/logo 128.png").getImage());
		icones.add(new ImageIcon("res/logo 64.png").getImage());
		icones.add(new ImageIcon("res/logo 32.png").getImage());
		icones.add(new ImageIcon("res/logo 16.png").getImage());
		
		setIconImages(icones);
		setTitle("aegisOrionis v0.5");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(painelLogo);
		add(painelTexto);
		add(painelBotoes);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(painelEmail);
		
		configurarElementos(); 
		
		painelTexto.add(areaTexto);
		painelBotoes.add(labelDispositivo);
		painelBotoes.add(boxDrives);
		painelBotoes.add(botaoVacinar);
		painelBotoes.add(botaoRemover);
		painelEmail.add(labelEmail);
		painelEmail.add(botaoEmail);
		
		configurarListeners();
		
		componentesFuncoes = painelBotoes.getComponents();
		componentesFuncoes = Arrays.copyOfRange(componentesFuncoes, 1, componentesFuncoes.length);
		
		setResizable(false);
		pack();
		setVisible(true);
		requestFocusInWindow();
		setLocationRelativeTo(null);
	}
	
	private void configurarListeners() {
		botaoVacinar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controlador.realizarVacina(boxDrives.getItemAt(boxDrives.getSelectedIndex()));
			}
		});
		
		botaoRemover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controlador.realizarRemocao(boxDrives.getItemAt(boxDrives.getSelectedIndex()));
			}
		});
		
		botaoEmail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controlador.realizarEmail();
			}
		});
	}
	
	private void configurarElementos() {
		areaTexto.setText("aegisOrionis remove e previne infecções em dispositivos removíveis.\n"
			+ "Escolha um dispositivo e pressione o botão Remover para obliterar\n"
			+ "totalmente ameaças no dispositivo selecionado. Em seguida, utilize\n"
			+ "o botão Vacinar para proteger o dispositivo contra futuras infecções.");
		
		areaTexto.setEditable(false);
		areaTexto.setFocusable(false);
		areaTexto.setBackground(getBackground());
		areaTexto.setFont(getFont());
		boxDrives.setBorder(BorderFactory.createEmptyBorder(1, 0, 2, 0));
		boxDrives.setPrototypeDisplayValue(new File("Nenhum dispositivo."));
		painelBotoes.setBorder(BorderFactory.createEmptyBorder(6, 0, 2, 0));
	}
	

	
	private void alterarEstadoFuncoes(final boolean estado) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (Component comps : componentesFuncoes) {
					comps.setEnabled(estado);
				}
			}
		});
	}
	
	@Override
	public void update(Observable o, Object msg) {
		if (msg instanceof Solicitacoes) {
			File caminho = boxDrives.getItemAt(boxDrives.getSelectedIndex());
			int opcao;
			switch (Solicitacoes.valueOf(msg.toString())) {
			case ARQUIVOEXISTE:
				opcao = JOptionPane.showConfirmDialog(null, "O arquivo autorun.inf ainda existe. Deseja removê-lo agora?");
				if (opcao == JOptionPane.YES_OPTION) {
					controlador.forcarRemocao(caminho);
					controlador.realizarVacina(caminho);
				} else {
					JOptionPane.showMessageDialog(rootPane, "Um arquivo com nome de autorun.inf já existe.\nPressione o botão Remover para excluí-lo.");
				}
				break;
			case SOLICITARCONFIRMACAO:
				opcao = JOptionPane.showConfirmDialog(rootPane, "Já existe uma pasta com o nome de autorun.inf, o que\n"
						+ "pode significar que o dispositivo já está vacinado.\n"
						+ "Deseja prosseguir mesmo assim?");
				if (opcao == JOptionPane.YES_OPTION) {
					controlador.forcarRemocao(caminho);
				}
				break;
			case ATIVARFUNCOES:
				alterarEstadoFuncoes(true);
				break;
			case DESATIVARFUNCOES:
				alterarEstadoFuncoes(false);
				break;
			default:
				break;
			}
		} else if (msg instanceof Comunicados) {
			Comunicados c = Comunicados.valueOf(msg.toString());
			String mensagem = c.getMensagem();
			JOptionPane.showMessageDialog(rootPane, mensagem);
		} else if (msg instanceof DefaultComboBoxModel) {
			@SuppressWarnings("unchecked")
			final DefaultComboBoxModel<File> model = (DefaultComboBoxModel<File>) msg;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					boxDrives.setModel(model);
				}
			});
		}
	}
}