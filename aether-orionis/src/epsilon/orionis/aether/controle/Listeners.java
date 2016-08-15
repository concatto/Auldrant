package epsilon.orionis.aether.controle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import epsilon.orionis.aether.modelo.Comando;
import epsilon.orionis.aether.modelo.EnumArquivos;
import epsilon.orionis.aether.modelo.EnumInformacoes;
import epsilon.orionis.aether.modelo.Ferramentas;
import epsilon.orionis.aether.modelo.HistoricoInformacoes;
import epsilon.orionis.aether.modelo.ManipuladorArquivos;
import epsilon.orionis.aether.visao.AssistenteConexao;
import epsilon.orionis.aether.visao.SFrame;
import epsilon.orionis.aether.visao.Restaurar;
import epsilon.orionis.aether.visao.Tela;

public class Listeners {
	private Controlador controlador;
	private Comando comando;
	private ManipuladorArquivos manipulador;
	private Tela tela;
	private Restaurar restaurar;
	private AssistenteConexao telaConexao;
	private HistoricoInformacoes historico;
	
	public Listeners(Controlador controlador, Comando comando, ManipuladorArquivos manipulador, Tela tela, Restaurar restaurar, AssistenteConexao telaConexao, HistoricoInformacoes historico) {
		this.controlador = controlador;
		this.comando = comando;
		this.manipulador = manipulador;
		this.tela = tela;
		this.restaurar = restaurar;
		this.telaConexao = telaConexao;
		this.historico = historico;
	}
	
	public WindowAdapter getListenerFechamento() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				comando.cancelarProcesso();
				tela.getFrame().dispose();
				System.exit(0);
			}
		};
	}
	
	public WindowAdapter getListenerFechamentoSecundario() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				SFrame frame = (SFrame) e.getSource();
				Class<?> classe = frame.getCriador();
				if (classe.equals(restaurar.getClass())) {
					restaurar.reiniciar();
				} else if (classe.equals(telaConexao.getClass())) {
					telaConexao.reiniciar();
				}
				frame.dispose();
			}
		};
	}
	
	public MouseAdapter getListenerCliqueLista() {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					restaurar.selecionarPonto(e.getPoint());
					restaurar.exibirPopup(e.getPoint());
				}
			}
		};
	}
	
	public ContainerAdapter getListenerContainer() {
		return new ContainerAdapter() {			
			@Override
			public void componentAdded(ContainerEvent e) {
				tela.atualizar();
			}
		};
	}
	
	public ListSelectionListener getListSelection() {
		return new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!restaurar.isRestaurarHabilitado()) {
					restaurar.habilitarRestaurar();
				}
				if (restaurar.getSelecionadoLista() == null) {
					restaurar.limparTabela();
				}
				
				if (e.getValueIsAdjusting() && restaurar.getSelecionadoLista() != null) {
					if (historico.getInformacoesCompletas() == null) {
						historico.gravarInformacoes();
					}
					
					restaurar.limparTabela();
					for (String string : historico.getInformacoes(restaurar.getSelecionadoLista())) {
						String[] valores = string.split(":");
						valores[0] = EnumInformacoes.converterCodigo(valores[0]);
						restaurar.adicionarTabela(valores[0], valores[1]);
					}
				}
			}
		};
	}
	
	public ActionListener getParar() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comando.cancelarProcesso();
			}
		};
	}
	
	public ActionListener getLimpar() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tela.limparConsole();
			}
		};
	}
	
	public ActionListener getTraceroute() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comando.executarComando("tracert", "us.battle.net");
			}
		};
	}
	
	public ActionListener getAbrir() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = tela.getChooser();
				int opcao = chooser.showOpenDialog(null);
				if (opcao == JFileChooser.APPROVE_OPTION) {
					File arquivoSelecionado = chooser.getSelectedFile();
					List<String> texto;
					try {
						texto = manipulador.lerDe(arquivoSelecionado);
					} catch (Exception e1) {
						tela.exibirMensagemJanela("Falha na abertura do log.", "Erro", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Date data = new Date(arquivoSelecionado.lastModified());
					tela.publicarMensagem("» Leitura do log " + arquivoSelecionado.getName() + " (" + data.toString() + "):");
					for (String string : texto) {
						string = Normalizer.normalize(string, Normalizer.Form.NFD);
						tela.publicarMensagem(string);
					}
					tela.publicarMensagem("« Fim do log.");
				}
			}
		};
	}
	
	public ActionListener getSalvar() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = tela.getChooser();
				chooser.setSelectedFile(new File("Novo arquivo"));
				int opcao = chooser.showSaveDialog(tela.getFrame());
				if (opcao == JFileChooser.APPROVE_OPTION) {
					String mensagem = tela.getConsole().getText();
					String caminho = chooser.getSelectedFile().toString();
					if (!caminho.endsWith(".orn")) {
						caminho = caminho.concat(".orn");
					}
					File arquivo = new File(caminho);
					try {
						manipulador.escreverPara(mensagem, arquivo, true);
						tela.exibirMensagemJanela("Log salvo com sucesso!", "Sucesso");
					} catch (Exception e1) {
						tela.exibirMensagemJanela("Falha no salvamento do log.", "Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};
	}
	
	public ActionListener getSair() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comando.cancelarProcesso();
				System.exit(0);
			}
		};
	}
	
	public ActionListener getRestaurar() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restaurar.renovarLista();
				restaurar.exibir();
			}
		};
	}

	public ActionListener getExibirNenhum() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tela.exibirNenhum();
				tela.atualizar();
			}
		};
	}
	
	public ActionListener getExibirPing() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tela.exibirPing();
			}
		};
	}
	
	public ActionListener getExibirTraceroute() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tela.exibirTraceroute();
			}
		};
	}

	public ActionListener getModem() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comando.executarComando("ipconfig", "/all");
			}
		};
	}
	
	public ActionListener getSobre() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tela.exibirMensagemJanela("Desenvolvido por: Fernando Concatto", "Sobre");
			}
		};
	}
	
	public ActionListener getFlush() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comando.executarComando("ipconfig", "/flushdns");
			}
		};
	}
	
	public ActionListener getPing() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String internetProtocol = tela.exibirEntrada("Digite o IP ou deixe em branco para testar com o servidor padrão.");
				if (internetProtocol != null) {
					if (internetProtocol.isEmpty()) {
						internetProtocol = "173.194.118.152";
					}
					comando.executarComando("ping", internetProtocol, "-t");
				}
			}
		};
	}
	
	public ActionListener getRenew() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pedidoConfirmacao = "Atenção: este processo desativará sua conexão por alguns segundos.\n"
						+ "Atividades em andamento que requeiram conexão com a Internet podem ser interrompidas.\n"
						+ "Deseja continuar?"
				;
				int opcao = tela.exibirConfirmacao(pedidoConfirmacao);
				if (opcao == JOptionPane.YES_OPTION) {
					comando.executarComando("ipconfig", "/release");
				}
			}
		};
	}
	
	public ActionListener getConfigurarConexao() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				telaConexao.exibir();
			}
		};
	}

	public ActionListener getCancelar() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComponent comp = (JComponent) e.getSource();
				SFrame frame = (SFrame) SwingUtilities.getWindowAncestor(comp);
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		};
	}

	public ActionListener getConfirmarAlteracao() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String mascara = Ferramentas.calcularMascara(telaConexao.getGateway());
				List<String> informacoes = new ArrayList<>(Arrays.asList(telaConexao.getInformacoes()));
				informacoes.add(3, mascara);
				
				/* Registrando as informações atuais em um arquivo, antes de realizar as alterações. */
				comando.executarComando("netsh", "interface", "ip", "show", "addresses", informacoes.get(0));
				
				int resposta = tela.exibirConfirmacao("Estas alterações podem desativar sua conexão com a Internet se as informações\n"
						+ "fornecidas estiverem erradas. Você poderá desfazê-las caso queira.\n"
						+ "Deseja continuar?"
				);
				if (resposta == JOptionPane.YES_OPTION) {
					controlador.alterarConexao(informacoes);
				}
			}
		};
	}

	public ActionListener getRealizarRestauracao() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> valores = restaurar.getValoresTabela();
				valores.add(1, "static");
				controlador.alterarConexao(valores);
			}
		};
	}
	


	public ActionListener getLimparHistorico() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				manipulador.neutralizarArquivo(EnumArquivos.ENDERECOS);
				restaurar.renovarLista();
			}
		};
	}
	
	public ActionListener getRemover() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				historico.deletarRegistro(restaurar.getSelecionadoLista());
				restaurar.renovarLista();
			}
		};
	}
}
