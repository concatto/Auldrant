package epsilon.orionis.aether.controle;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DocumentFilter;

import epsilon.orionis.aether.modelo.Comando;
import epsilon.orionis.aether.modelo.EnumArquivos;
import epsilon.orionis.aether.modelo.EnumInformacoes;
import epsilon.orionis.aether.modelo.EnumProcessos;
import epsilon.orionis.aether.modelo.HistoricoInformacoes;
import epsilon.orionis.aether.modelo.ModeloTabela;
import epsilon.orionis.aether.modelo.NumberFilter;
import epsilon.orionis.aether.modelo.ManipuladorArquivos;
import epsilon.orionis.aether.modelo.ModeloChooser;
import epsilon.orionis.aether.visao.AssistenteConexao;
import epsilon.orionis.aether.visao.Restaurar;
import epsilon.orionis.aether.visao.Tela;

public class Controlador {
	private Tela tela;
	private Restaurar restaurar;
	private AssistenteConexao telaConexao;
	private Comando comando;
	private ManipuladorArquivos manipulador;
	private DirecionadorMensagens principal;
	private DirecionadorMensagens assist;
	private DirecionadorFiltro filtragem;
	private HistoricoInformacoes historico;
	
	public Controlador() {
		principal = new DirecionadorMensagens();
		assist = new DirecionadorMensagens();
		filtragem = new DirecionadorFiltro(principal, assist);
		comando = new Comando(filtragem);
		manipulador = new ManipuladorArquivos();
		historico = new HistoricoInformacoes(manipulador);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				tela = new Tela(Controlador.this);
				restaurar = new Restaurar(Controlador.this);
				telaConexao = new AssistenteConexao(Controlador.this);
				comando.addObserver(tela);
				principal.addObserver(tela);
				assist.addObserver(telaConexao);
				tela.inicializar();
				restaurar.inicializar();
				telaConexao.inicializar();
			}
		});
	}
	
	public void realizarComando(String... comandos) {
		comando.executarComando(comandos);
	}
	
	public ModeloChooser getModeloChooser() {
		return new ModeloChooser();
	}
	
	public ModeloTabela getModeloTabela() {
		return new ModeloTabela();
	}
	
	public Listeners getListeners() {
		return new Listeners(this, comando, manipulador, tela, restaurar, telaConexao, historico);
	}

	public void procurarGateway() {
		String gatewayPadrao = filtragem.getGateway();
		tela.publicarMensagem("Endereço encontrado: " + gatewayPadrao);
		tela.publicarMensagem("Iniciando navegação...");
		int resposta = tela.exibirConfirmacao("Endereço encontrado com sucesso: " + gatewayPadrao
				+ "\nPressione OK para prosseguir para o navegador padrão.");
		if (resposta == JOptionPane.OK_OPTION) {
			try {	
				Desktop.getDesktop().browse(new URI("http://" + gatewayPadrao));
				tela.publicarMensagem("Navegação iniciada com sucesso.");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			tela.publicarMensagem("Navegação cancelada.");
		}
	}
	
	public DocumentFilter getFiltroNumeros() {
		return new NumberFilter();
	}
	
	public void registrarInformacoes(String mensagem) {
		if (mensagem.startsWith(EnumInformacoes.ENDERECO.getCodigo())) {
			String tempo = EnumInformacoes.TEMPO.getCodigo() + ":" + (System.currentTimeMillis() / 1000L);
			manipulador.escreverPara(tempo, EnumArquivos.ENDERECOS, false);
		}
		
		manipulador.escreverPara(mensagem, EnumArquivos.ENDERECOS, false);
	}
	
	public void alterarConexao(List<String> parametros) {
		List<String> informacoes = new ArrayList<>(Arrays.asList("netsh", "interface", "ip", "set", "address"));
		informacoes.addAll(parametros);
		comando.executarComando(informacoes);
	}
	
	public String[] solicitarRegistros() {
		historico.gravarInformacoes();
		return historico.getRegistros().toArray(new String[0]);
	}
	
	public EnumProcessos solicitarProcesso() {
		return comando.getProcesso();
	}
}
