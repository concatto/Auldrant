package epsilon.orionis.aether.visao;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import epsilon.orionis.aether.controle.Controlador;
import epsilon.orionis.aether.modelo.ModeloTabela;

public class Restaurar extends JanelaSecundaria {
	private static final int QTD_LINHAS_TABELA = 4;
	private JPanel painel = new JPanel();
	private JPanel painelEsquerda = new JPanel();
	private JPanel painelDireita = new JPanel();
	private JPanel painelLista = new JPanel();
	private JPanel painelTabela = new JPanel();
	private JPanel botoesLista = new JPanel();
	private JPanel botoesTabela = new JPanel();
	private JLabel pontos = new JLabel("Pontos de restauração", SwingConstants.LEFT);
	private JLabel detalhes = new JLabel("Detalhes", SwingConstants.LEFT);
	private JPopupMenu menu = new JPopupMenu();
	private JMenuItem remover = new JMenuItem("Remover ponto de restauração");
	private JButton limpar = new JButton("Limpar histórico");
	private JButton restaurar = new JButton("Restaurar configurações");
	private JButton cancelar = new JButton("Cancelar");
	private JSeparator separador = new JSeparator(SwingConstants.VERTICAL);
	private JScrollPane scrollLista = new JScrollPane();
	private JScrollPane scrollTabela = new JScrollPane();
	private JList<String> lista = new JList<>();
	private DefaultListModel<String> modeloLista = new DefaultListModel<>();
	private ModeloTabela modeloTabela;
	@SuppressWarnings("serial")
	private JTable tabela = new JTable() {		
		@Override
		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
			Component comp = super.prepareRenderer(renderer, row, column);
			JComponent jComp = (JComponent) comp;
			jComp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return jComp;
		};
	};
	
	public Restaurar(Controlador controlador) {
		this.controlador = controlador;
	}

	@Override
	public void inicializar() {
		frame.setTitle("Restaurar");
		listeners = controlador.getListeners();
		modeloTabela = controlador.getModeloTabela();
		
		painelLista.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
		painelTabela.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
		painelLista.setLayout(new BorderLayout(5, 5));
		painelTabela.setLayout(new BorderLayout(5, 5));
		painelEsquerda.setLayout(new BoxLayout(painelEsquerda, BoxLayout.Y_AXIS));
		painelDireita.setLayout(new BoxLayout(painelDireita, BoxLayout.Y_AXIS));
		painel.setLayout(new FlowLayout());
		limpar.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		frame.addWindowListener(listeners.getListenerFechamentoSecundario());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(painel);
		painel.add(painelEsquerda);
		painel.add(painelDireita);
		
		botoesLista.add(limpar);
		botoesTabela.add(restaurar);
		botoesTabela.add(cancelar);
		
		painelLista.add(scrollLista, BorderLayout.WEST);
		painelLista.add(separador, BorderLayout.EAST);
		painelLista.add(pontos, BorderLayout.NORTH);
		painelTabela.add(detalhes, BorderLayout.NORTH);
		painelTabela.add(scrollTabela, BorderLayout.WEST);
		
		painelEsquerda.add(painelLista);
		painelEsquerda.add(botoesLista);
		painelDireita.add(painelTabela);
		painelDireita.add(botoesTabela);
		
		menu.add(remover);
		
		scrollLista.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollLista.setViewportView(lista);
		scrollTabela.setViewportView(tabela);
		
		restaurar.addActionListener(listeners.getRealizarRestauracao());
		cancelar.addActionListener(listeners.getCancelar());
		remover.addActionListener(listeners.getRemover());
		limpar.addActionListener(listeners.getLimparHistorico());
		
		lista.addMouseListener(listeners.getListenerCliqueLista());
		lista.addListSelectionListener(listeners.getListSelection());
		lista.setModel(modeloLista);
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setVisibleRowCount(6);
		lista.setPrototypeCellValue("00/00/0000 - 00:00:00");

		modeloTabela.addColumn("Parâmetro");
		modeloTabela.addColumn("Conteúdo");
		tabela.setModel(modeloTabela);
		tabela.setGridColor(frame.getBackground());
		
	}
	
	private void calcularTabela() {
		frame.pack();
		JTableHeader header = tabela.getTableHeader();
		/* A altura desejada para a tabela é a altura concreta da lista */
		int alturaLista = scrollLista.getViewport().getHeight();
		
		int alturaLinha = (int) Math.ceil((alturaLista - header.getHeight()) / (double) QTD_LINHAS_TABELA);
		/* Altura das linhas: altura da lista menos o tamanho do header, dividida pela quantidade de linhas, arredondada para cima. */
		tabela.setRowHeight(alturaLinha);
		
		Dimension dimensao = header.getPreferredSize();
		dimensao.height = alturaLista - (alturaLinha * QTD_LINHAS_TABELA);
		/* Altura do header: altura da lista menos o corpo da tabela, arredondada para baixo. */
		header.setPreferredSize(dimensao);
		
		tabela.setPreferredScrollableViewportSize(new Dimension(260, tabela.getRowHeight() * QTD_LINHAS_TABELA));
	}
	
	@Override
	public void exibir() {
		calcularTabela();
		
		restaurar.setEnabled(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void adicionarLista(String elemento) {
		modeloLista.addElement(elemento);
	}
	
	public void substituirLista(String[] elementos) {

	}
	
	public void adicionarTabela(String chave, String valor) {
		Object[] dados = {chave, valor};
		modeloTabela.addRow(dados);
	}
	
	public void limparTabela() {
		modeloTabela.clearTable();
	}
	
	public String getSelecionadoLista() {
		return lista.getSelectedValue();
	}
	
	public List<String> getValoresTabela() {
		List<String> valores = new ArrayList<>();
		for (int i = 0; i < QTD_LINHAS_TABELA; i++) {
			valores.add(tabela.getValueAt(i, 1).toString());
		}
		return valores;
	}
	
	public boolean isRestaurarHabilitado() {
		return restaurar.isEnabled();
	}
	
	public void habilitarRestaurar() {
		restaurar.setEnabled(true);
	}

	public void selecionarPonto(Point ponto) {
		lista.setSelectedIndex(lista.locationToIndex(ponto));
		for (ListSelectionListener listener : lista.getListSelectionListeners()) {
			/* Necessário para forçar isAdjusting a ser true. */
			listener.valueChanged(new ListSelectionEvent(lista, 0, 0, true));
		}
	}
	
	public void exibirPopup(Point ponto) {
		menu.show(lista, ponto.x, ponto.y);
	}
	
	public void renovarLista() {
		String[] elementos = controlador.solicitarRegistros();
		modeloLista.clear();
		for (String elemento : elementos) {
			modeloLista.addElement(elemento);
		}
	}
	
	@Override
	public void reiniciar() {
		limparTabela();
	}
}
