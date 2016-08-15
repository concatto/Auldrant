package orionis.epsilon.vinculus.controlador;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import orionis.epsilon.vinculus.modelo.Cliente;
import orionis.epsilon.vinculus.modelo.Conexao;
import orionis.epsilon.vinculus.modelo.Mensageiro;
import orionis.epsilon.vinculus.modelo.Mensagem;
import orionis.epsilon.vinculus.modelo.Servidor;
import orionis.epsilon.vinculus.modelo.SocketHandler;
import orionis.epsilon.vinculus.modelo.SocketInformation;
import orionis.epsilon.vinculus.visao.AbstractMainframe;
import orionis.epsilon.vinculus.visao.FrameCliente;
import orionis.epsilon.vinculus.visao.FrameServidor;
import orionis.epsilon.vinculus.visao.Seletor;

public class Controlador {
	private Seletor seletor;
	private FrameCliente frameCliente;
	private FrameServidor frameServidor;
	private SocketHandler handler;
	private Cliente cliente;
	private Servidor servidor;
	private Mensageiro mensageiro;
	
	private AbstractMainframe mainframe;
	private Conexao conexao;
	
	public Controlador() {
		mensageiro = new Mensageiro(this);
		handler = new SocketHandler();
		SwingUtilities.invokeLater(() -> {
			seletor = new Seletor(Controlador.this);
			seletor.construct();
		});
	}
	
	public void transferirFluxo(final String frameDestino) {
		SwingUtilities.invokeLater(() -> {
			seletor.destroy();
			
			if (frameDestino.equals("Cliente")) {
				cliente = new Cliente(mensageiro);
				frameCliente = new FrameCliente(Controlador.this);
				frameCliente.construct();
				
				mainframe = frameCliente;
				conexao = cliente;
			} else {
				servidor = new Servidor(mensageiro);
				frameServidor = new FrameServidor(Controlador.this);
				frameServidor.construct();
				
				mainframe = frameServidor;
				conexao = servidor;
			}
		});
	}

	public void adicionarConexao(String endereco, String porta) {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			private SocketInformation info;
			private Socket socket;
			
			@Override
			protected Void doInBackground() {
				frameCliente.displayProgress("Conectando", "Conectando...", false);
				try {
					socket = cliente.criarSocket(InetAddress.getByName(endereco), Integer.parseInt(porta));
					info = SocketInformation.forSocket(socket);
				} catch (UnknownHostException e) {
					frameCliente.publishError(Mensagem.CONEXAO_INEXISTENTE);
				} catch (IOException e) {
					frameCliente.publishError(Mensagem.CONEXAO_RECUSADA);
				}
				return null;
			}
			
			@Override
			protected void done() {
				frameCliente.destroyProgress();
				frameCliente.resetFields();
				if (socket == null) return;
				
				frameCliente.insertInformation(info);
				handler.inserirSocket(socket);
				cliente.ouvir(socket);
			}
		};
		
		worker.execute();
	}
	
	public void realizarTransferencia(SocketInformation destino, BufferedImage imagem) {
		String nome = "img " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss dd-MM-yyyy")) + ".png";
		File arquivo;
		
		if (imagem == null) {
			try {
				imagem = getImagemClipboard();
			} catch (UnsupportedFlavorException e) {
				mainframe.publishError(Mensagem.CONTEUDO_CLIPBOARD_INVALIDO);
				return;
			}
		}
		
		try {
			arquivo = new File(System.getProperty("java.io.tmpdir"), nome);
			arquivo.deleteOnExit();
			ImageIO.write(imagem, "png", arquivo);
		} catch (IOException e) {
			mainframe.publishError(Mensagem.ERRO_IO);
			return;
		}
		
		realizarTransferencia(destino, arquivo);
	}
	
	public void realizarTransferencia(SocketInformation destino, File arquivo) {
		if (arquivo == null) {
			frameCliente.publishError(Mensagem.NENHUM_ARQUIVO_SELECIONADO);
		}
		cliente.transferirArquivo(handler.getSocket(destino), arquivo);
		frameCliente.displayProgress("Enviando", "Enviando arquivo...", true);
	}
	
	public void abrirServidor(String porta) {
		try {
			frameServidor.alterarAbrir(false);
			frameServidor.publicarMensagem("Iniciando Servidor na porta " + frameServidor.getPorta() + "...");
			servidor.criarServidor(Integer.parseInt(porta));
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				String endereco;
				@Override
				protected Void doInBackground() {
					frameServidor.setEndereco("Obtendo endereço...");
					endereco = servidor.getEnderecoExterno();
					return null;
				}
				@Override
				protected void done() {
					frameServidor.setEndereco(endereco);
					frameServidor.publicarMensagem("Servidor criado com sucesso.");
					frameServidor.publicarMensagem("Endereço do Servidor: " + endereco);
					frameServidor.alterarFechar(true);
					criarObservadorConexao();
				}
			};
			worker.execute();
		} catch (IllegalArgumentException e) {
			frameServidor.publishError(Mensagem.PORTA_INVALIDA);
			frameServidor.alterarAbrir(true);
			frameServidor.publicarMensagem("Erro ao abrir o Servidor. Utilize outra porta.");
		} catch (IOException e) {
			mainframe.publishError(Mensagem.ERRO_IO);
			e.printStackTrace();
		}
	}
	
	public void fecharServidor() {
		servidor.fecharServerSocket();
		handler.limpar();
		frameServidor.getListModel().removeAllElements();
		frameServidor.publicarMensagem("Servidor fechado.");
		frameServidor.alterarFechar(false);
		frameServidor.alterarAbrir(true);
	}
	
	private void criarObservadorConexao() {
		Thread observador = new Thread(() -> {
			while (true) {
				Socket socket = servidor.aguardarConexao();
				if (socket == null) break;
				final SocketInformation info = SocketInformation.forSocket(socket);
				handler.inserirSocket(socket);
				SwingUtilities.invokeLater(() -> {
					frameServidor.insertInformation(info);
					frameServidor.publicarMensagem("Novo Cliente conectado: " + info.getAddress() + ".");
				});
				servidor.ouvir(socket);
			}
		});
		
		observador.start();
	}
	
	/* Tratará das mensagens ouvidas pelo Socket. */
	public void tratarMensagem(Socket origem, Object mensagemCrua) {
		if (mensagemCrua instanceof Integer) {
			mainframe.updateProgress((int) mensagemCrua);
		} else if (mensagemCrua instanceof String || mensagemCrua instanceof Mensagem) {
			Mensagem mensagem = Mensagem.valueOf(mensagemCrua.toString());
			
			switch (mensagem) {
			case ARQUIVO:
				servidor.aguardarArquivo(origem);
				break;
			case FIM:
				try {
					SocketInformation info = mainframe.getInfoBySocket(origem);
					handler.getSocket(info).close();
					info.setClosed(true);
					mainframe.changeActions(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case ACEITAR:
				cliente.prosseguir();
				break;
			case TRANSFERENCIA_INTERROMPIDA:
				mainframe.destroyProgress();
				mainframe.publishError(mensagem);
				break;
			default:
				break;
			}
		}
	}
	
	public BufferedImage getImagemClipboard() throws UnsupportedFlavorException {
		try {
			return (BufferedImage) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.imageFlavor);
		} catch (HeadlessException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public File obterCaminho(String nome, Socket remetente) {
		String enderecoRemetente = remetente.getInetAddress().getHostAddress();
		
		frameServidor.publicarMensagem(enderecoRemetente + " envia: " + nome);
		File destino = frameServidor.salvarArquivo(nome, enderecoRemetente);
		
		String status = destino == null ? "Arquivo recusado. Conexão fechada." : "Arquivo aceito. Transferência iniciada.";
		frameServidor.publicarMensagem(status);
		
		return destino;
	}
	
	public void fecharConexao(Socket socket) {
		try {
			/* Tentará escrever uma mensagem de Fim para o Socket, que será tratada pelo método tratarMensagem acima */
			conexao.escrever(socket, Mensagem.FIM);
			socket.close();
		} catch (IOException e) {
			/* Já está fechado */
		}
	}
	
	public void fecharSocket(SocketInformation info) {
		Socket socket = handler.getSocket(info);
		fecharConexao(socket);
	}

	public void removerSocket(SocketInformation info) {
		handler.removerSocket(info);
	}
}
