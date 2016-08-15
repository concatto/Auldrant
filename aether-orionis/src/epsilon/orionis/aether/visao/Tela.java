package epsilon.orionis.aether.visao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import epsilon.orionis.aether.controle.Controlador;
import epsilon.orionis.aether.controle.DirecionadorMensagens;
import epsilon.orionis.aether.controle.Listeners;
import epsilon.orionis.aether.grafico.GraficoPing;
import epsilon.orionis.aether.grafico.GraficoTraceroute;
import epsilon.orionis.aether.grafico.PainelGrafico;
import epsilon.orionis.aether.modelo.EnumProcessos;
import epsilon.orionis.aether.modelo.EnumRespostas;

public class Tela extends JanelaPrincipal implements Observer {
	private JMenuBar barraMenu = new JMenuBar();
	private JMenu arquivo = new JMenu("Arquivo");
	private JMenu editar = new JMenu("Editar");
	private JMenu grafico = new JMenu("Gráfico");
	private JMenu ajuda = new JMenu("Ajuda");
	private JSeparator separador = new JSeparator(SwingConstants.HORIZONTAL);
	private JMenuItem abrir = new JMenuItem("Abrir log");
	private JMenuItem salvar = new JMenuItem("Salvar log");
	private JMenuItem sair = new JMenuItem("Sair");
	private JMenuItem restaurar = new JMenuItem("Restaurar...");
	private ButtonGroup grupoGrafico = new ButtonGroup();
	private JRadioButtonMenuItem opcaoGraficoNenhum = new JRadioButtonMenuItem("Nenhum", true);
	private JRadioButtonMenuItem opcaoGraficoPing = new JRadioButtonMenuItem("Ping");
	private JRadioButtonMenuItem opcaoGraficoTraceroute = new JRadioButtonMenuItem("Traceroute");
	private JMenuItem sobre = new JMenuItem("Sobre");
	private JTabbedPane abas = new JTabbedPane();
	private JPanel principal = new JPanel();
	private JPanel painelAcoes = new JPanel();
	private JPanel painelConfiguracoes = new JPanel();
	private JPanel painelRastreamento = new JPanel();
	private JPanel superAbas = new JPanel();
	private JPanel superBotoes = new JPanel();
	private JPanel superTexto = new JPanel();
	private JButton flush = new JButton("Limpar DNS");
	private JButton renew = new JButton("Renovar conexão");
	private JButton ping = new JButton("Teste de Ping");
	private JButton conexao = new JButton("Configurações da conexão");
	private JButton modem = new JButton("Configurações do modem");
	private JButton traceroute = new JButton("Rastrear rota");
	private JButton parar = new JButton("Parar");
	private JButton limpar = new JButton("Limpar");
	private JTextArea console = new JTextArea("Pronto.", 20, 30);
	private JScrollPane scroll = new JScrollPane(console);
	private JFileChooser chooser;
	private Container content;
	
	private GridBagConstraints layPaineis = new GridBagConstraints();
	
	private Listeners listeners;
	private Controlador controlador;
	private GraficoPing graficoPing = new GraficoPing(200);
	private GraficoTraceroute graficoTraceroute = new GraficoTraceroute(200);
	private PainelGrafico painelPing;
	private PainelGrafico painelTraceroute;
	private List<Component> botoes = new ArrayList<>();
	
	public Tela(Controlador controlador) {
		this.controlador = controlador;
	}
	
	@Override
	public void inicializar() {
		chooser = new JFileChooser();
		listeners = controlador.getListeners();
		content = frame.getContentPane();
		content.addContainerListener(listeners.getListenerContainer());
		configurarComponentes();
		configurarMenus();
		configurarListeners();
		inserirComponentes();
		configurarFrame();
		painelPing = new PainelGrafico(graficoPing);
		painelTraceroute = new PainelGrafico(graficoTraceroute);
		
		for (Component componente : abas.getComponents()) {
			/* Panels que compõem o TabbedPane */
			componente.setBackground(Color.WHITE);
			for (Component componenteInterior : ((Container) componente).getComponents()) {
				/* Buttons que compõem os Panels */
				botoes.add(componenteInterior);
				componenteInterior.setBackground(Color.WHITE);
			}
		}
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obj == null) {
			return;
		}
		
		if (obs instanceof DirecionadorMensagens) {
			/* Mensagens de progresso */
			desativarBotoes();
			EnumProcessos processo = controlador.solicitarProcesso();
			String mensagem;
			switch (processo) {
			case PING:
				mensagem = redirecionarPing(obj);
				break;
			case TRACERT:
				mensagem = redirecionarTraceroute(obj);
				break;
			default:
				mensagem = obj.toString();
				break;
			}
			publicarMensagem(mensagem);
		} else {
			/* Mensagens de término vindas do Comando */
			EnumRespostas r = EnumRespostas.valueOf(obj.toString());
			switch (r) {
			case INTERROMPIDO:
				publicarMensagem("Operação cancelada pelo usuário.");
				reativarBotoes();
				break;
			case RELEASED:
				publicarMensagem("Conexão preparada.");
				controlador.realizarComando("ipconfig", "/renew");
				break;
			case GATEWAY:
				publicarMensagem("Iniciando busca por endereços...");
				controlador.procurarGateway();
				reativarBotoes();
				break;
			default:
				reativarBotoes();
				break;
			}
		}
	}
	
	private String redirecionarPing(Object obj) {
		String mensagem = obj.toString();
		try {
			graficoPing.atualizarGrafico(Integer.parseInt(mensagem.substring(0, mensagem.indexOf(" "))));
		} catch (Exception e) {
			if (mensagem.equals("Tempo esgotado.")) {
				graficoPing.atualizarGrafico(0);
			}
		}
		
		return mensagem;
	}

	private String redirecionarTraceroute(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> valores = (List<String>) obj;
		String mensagem;
		if (valores.size() == 1) {
			mensagem = valores.get(0);
		} else {
			graficoTraceroute.atualizarGrafico(valores.get(0), valores.get(1), valores.get(2), valores.get(4));
			if (valores.get(3).equals("0")) {
				mensagem = "Tempo esgotado.";
			} else {
				mensagem = "Média: " + valores.get(3) + " ms. Host: " + valores.get(4);
			}
		}
		return mensagem;
	}

	private void reativarBotoes() {
		parar.setEnabled(false);
		modem.setEnabled(true);
		for (Component comp : botoes) {
			comp.setEnabled(true);
		}
	}
	
	public void desativarBotoes() {
		parar.setEnabled(true);
		modem.setEnabled(false);
		for (Component comp : botoes) {
			comp.setEnabled(false);
		}
	}
	
	private void configurarListeners() {
		flush.addActionListener(listeners.getFlush());
		renew.addActionListener(listeners.getRenew());
		ping.addActionListener(listeners.getPing());
		traceroute.addActionListener(listeners.getTraceroute());
		conexao.addActionListener(listeners.getConfigurarConexao());
		modem.addActionListener(listeners.getModem());
		parar.addActionListener(listeners.getParar());
		limpar.addActionListener(listeners.getLimpar());
	}
	
	private void configurarMenus() {
		frame.setJMenuBar(barraMenu);
		barraMenu.add(arquivo);
		barraMenu.add(editar);
		barraMenu.add(grafico);
		barraMenu.add(ajuda);
		arquivo.add(abrir);
		arquivo.add(salvar);
		arquivo.add(separador);
		arquivo.add(sair);
		editar.add(restaurar);
		
		grupoGrafico.add(opcaoGraficoNenhum);
		grupoGrafico.add(opcaoGraficoPing);
		grupoGrafico.add(opcaoGraficoTraceroute);
		
		grafico.add(opcaoGraficoNenhum);
		grafico.add(opcaoGraficoPing);
		grafico.add(opcaoGraficoTraceroute);
		ajuda.add(sobre);
		
		abrir.addActionListener(listeners.getAbrir());
		salvar.addActionListener(listeners.getSalvar());
		sair.addActionListener(listeners.getSair());
		restaurar.addActionListener(listeners.getRestaurar());
		
		sobre.addActionListener(listeners.getSobre());
		opcaoGraficoNenhum.addActionListener(listeners.getExibirNenhum());
		opcaoGraficoPing.addActionListener(listeners.getExibirPing());
		opcaoGraficoTraceroute.addActionListener(listeners.getExibirTraceroute());
	}
	
	private void inserirComponentes() {	
		frame.add(principal);
		principal.add(superAbas, layPaineis);
		principal.add(superTexto, layPaineis);
		principal.add(superBotoes, layPaineis);
		superAbas.add(abas, layPaineis);
		
		painelAcoes.add(flush);
		painelAcoes.add(renew);
		painelRastreamento.add(ping);
		painelRastreamento.add(traceroute);
		painelConfiguracoes.add(conexao);
		painelConfiguracoes.add(modem);
		
		superTexto.add(scroll);
		superBotoes.add(parar);
		superBotoes.add(limpar);
	}
	
	private void configurarFrame() {
		frame.setTitle("aetherOrionis");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.addWindowListener(listeners.getListenerFechamento());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void configurarComponentes() {
		layPaineis.gridx = 0;
		layPaineis.fill = GridBagConstraints.HORIZONTAL;
		layPaineis.weightx = 1.0;
		principal.setLayout(new GridBagLayout());
		superAbas.setLayout(new GridBagLayout());
		superBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		abas.insertTab("Ações", null, painelAcoes, null, 0);
		abas.insertTab("Rastreamento", null, painelRastreamento, null, 1);
		abas.insertTab("Configurações", null, painelConfiguracoes, null, 2);
		principal.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
		superAbas.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 9));
		scroll.setBorder(BorderFactory.createTitledBorder("Console"));
		console.setBorder(BorderFactory.createEmptyBorder(1, 5, 5, 5));
		
		console.setFont(new Font("Arial", Font.PLAIN, 16));
		console.setEditable(false);
		parar.setEnabled(false);
		opcaoGraficoPing.setSelected(false);
		abas.setFocusable(false);
		
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(controlador.getModeloChooser());
	}
	
	public void publicarMensagem(String mensagem) {
		console.append("\n" + mensagem);
		console.setCaretPosition(console.getDocument().getLength() - mensagem.length());
	}
	
	public void limparConsole() {
		console.setText("Pronto.");
		graficoPing.limparGrafico();
	}
	
	public void atualizar() {
		frame.revalidate();
		frame.repaint();
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public JFileChooser getChooser() {
		return chooser;
	}
	
	public JTextArea getConsole() {
		return console;
	}
	
	public void exibirNenhum() {
		content.remove(painelPing);
		content.remove(painelTraceroute);
	}
	
	public void exibirTraceroute() {
		content.remove(painelPing);
		content.add(painelTraceroute);
	}
	
	public void exibirPing() {
		content.remove(painelTraceroute);
		content.add(painelPing);
	}
	
	public boolean isPingSelecionado() {
		return opcaoGraficoPing.isSelected();
	}
	
	public boolean isTracerouteSelecionado() {
		return opcaoGraficoTraceroute.isSelected();
	}
	
	public int exibirConfirmacao(String mensagem) {
		int resposta = JOptionPane.showConfirmDialog(frame, mensagem, "Confirmação", JOptionPane.OK_CANCEL_OPTION);
		return resposta;
	}
	
	public String exibirEntrada(String mensagem) {
		String resposta = JOptionPane.showInputDialog(frame, mensagem);
		return resposta;
	}
	
	public void exibirMensagemJanela(String mensagem, String titulo, int tipo) {
		JOptionPane.showMessageDialog(frame, mensagem, titulo, tipo);
	}
	
	public void exibirMensagemJanela(String mensagem, String titulo) {
		exibirMensagemJanela(mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void exibirMensagemJanela(String mensagem) {
		exibirMensagemJanela(mensagem, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
	}
}
