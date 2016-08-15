package epsilon.orionis.aegis.visao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.mail.MessagingException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import epsilon.orionis.aegis.principal.Controlador;

public class CompositorEmail implements Observer {
	private JFrame entradaTexto = new JFrame("Envio de e-mail");
	private JTextArea corpoTexto = new JTextArea(15, 45);
	private JLabel remetente = new JLabel("<html><b>Para:</b> Orionbelt Software</html>");
	private JLabel destinatario = new JLabel();
	private JLabel descricaoAnexo = new JLabel("<html><b>Arquivo anexado:</b></html>");
	private JLabel arquivoAnexado = new JLabel("Nenhum");
	private JPanel painelTudo = new JPanel();
	private JPanel painelTopo = new JPanel(new BorderLayout());
	private JPanel painelAnexar = new JPanel();
	private JPanel painelAnexado = new JPanel();
	private JPanel painelDados = new JPanel();
	private JPanel painelTexto = new JPanel();
	private JPanel painelInferior = new JPanel(new BorderLayout());
	private JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
	private JButton botaoEnviar = new JButton("Enviar");
	private JButton botaoCancelar = new JButton("Cancelar");
	private JButton botaoAnexar = new JButton("Anexar arquivo...");
	private JPopupMenu menu = new JPopupMenu();
	private JMenuItem remover = new JMenuItem("Remover anexo");
	
	private File anexo;
	private final String loginBase = "virusorionis@gmail.com";
	private final String senhaBase = "EpsilonOrionis";
	private Controlador controlador;
		
	public CompositorEmail(Controlador control) {
		controlador = control;
		corpoTexto.setWrapStyleWord(true);
		corpoTexto.setLineWrap(true);
		corpoTexto.setFont(entradaTexto.getFont());
		painelTexto.setBorder(BorderFactory.createTitledBorder("Mensagem"));
		painelTexto.add(corpoTexto);
		painelDados.setLayout(new BoxLayout(painelDados, BoxLayout.Y_AXIS));
		painelDados.setBorder(BorderFactory.createEmptyBorder(1, 4, 6, 0));
		painelDados.add(remetente);
		painelDados.add(destinatario);
		painelAnexar.add(botaoAnexar);
		painelAnexado.add(descricaoAnexo);
		painelAnexado.add(arquivoAnexado);
		painelAnexado.setLayout(new BoxLayout(painelAnexado, BoxLayout.Y_AXIS));
		painelAnexado.setBorder(BorderFactory.createEmptyBorder(3, 4, 0, 0));
		painelTopo.add(painelDados, BorderLayout.WEST);
		painelTopo.add(painelAnexar, BorderLayout.EAST);
		painelInferior.add(painelBotoes, BorderLayout.EAST);
		painelInferior.add(painelAnexado, BorderLayout.WEST);
		painelTudo.add(painelTopo);
		painelTudo.add(painelTexto);
		painelTudo.add(painelInferior);
		painelTudo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		painelTudo.setLayout(new BoxLayout(painelTudo, BoxLayout.Y_AXIS));
		
		botaoAnexar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				anexo = selecionarArquivo();
				if (anexo == null) {
					return;
				}
				controlador.realizarFiltroBytes(anexo.length());
			}
		});
		
		botaoEnviar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controlador.fornecerDados(corpoTexto.getText());
					controlador.fornecerDados(anexo);
					controlador.solicitarEnvio();
				} catch (MessagingException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		botaoCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				entradaTexto.dispose();
			}
		});
		
		remover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				anexo = null;
				arquivoAnexado.setText("Nenhum");
			}
		});
		
		arquivoAnexado.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (anexo != null) {
					menu.show(arquivoAnexado, arquivoAnexado.getX(), arquivoAnexado.getY());
				}
			}
		});
		
		menu.add(remover);
		painelBotoes.add(botaoEnviar);
		painelBotoes.add(botaoCancelar);
		entradaTexto.add(painelTudo);
		entradaTexto.setResizable(false);
		entradaTexto.pack();
		entradaTexto.setLocationRelativeTo(null);
	}
	
	public String[] coletarDados() {
		final JTextField campoLogin = new JTextField();
		final JPasswordField campoSenha = new JPasswordField();
		String login;
		String senha;
		campoLogin.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorRemoved(AncestorEvent event) {/*Em branco*/}
			@Override
			public void ancestorMoved(AncestorEvent event) {/*Em branco*/}
			@Override
			public void ancestorAdded(AncestorEvent event) {
				campoLogin.requestFocusInWindow();
			}
		});
		Object[] campos = {
				"Login:",
				campoLogin,
				"Senha:",
				campoSenha,
				"Você pode deixar os campos em branco para enviar anonimamente."
		};
		int opcao = JOptionPane.showConfirmDialog(entradaTexto.getRootPane(), campos, "Formulário de envio", JOptionPane.OK_CANCEL_OPTION);
		if (opcao == JOptionPane.OK_OPTION) {
			login = campoLogin.getText();
			senha = String.valueOf(campoSenha.getPassword());
			if (login.hashCode() == 0) {
				login = loginBase;
			}
			if (senha.hashCode() == 0) {
				senha = senhaBase;
			}
			String[] dados = {login, senha};

			return dados;
		}
		return null;
	}
	
	public void comporMensagem(String destinatario) {
		this.destinatario.setText(destinatario);
		entradaTexto.setVisible(true);
		corpoTexto.requestFocusInWindow();
	}
	
	public void destruirComponentes() {
		entradaTexto.dispose();
		anexo = null;
		arquivoAnexado.setText("Nenhum");
	}
	
	public String getLoginBase() {
		return loginBase;
	}
	
	private File selecionarArquivo() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Selecione o arquivo para enviar para os desenvolvedores:");
		int opcao = chooser.showOpenDialog(null);
		
		if (opcao == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			arquivoAnexado.setText(anexo.getName() + (String) arg);
		}
	}
}
