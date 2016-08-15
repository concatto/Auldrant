package epsilon.orionis.aether.modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import javax.swing.SwingWorker;

import epsilon.orionis.aether.controle.DirecionadorFiltro;

public class Comando extends Observable {
	private List<String> filtros;
	private List<String> comandos;
	private boolean ativo = false;
	
	private SwingWorker<Void, String> worker;
	private Process processo = null;
	
	private DirecionadorFiltro filtragem;

	public Comando(DirecionadorFiltro filtragem) {
		this.filtragem = filtragem;
		comandos = new ArrayList<>();
		filtros = new ArrayList<>();
		filtros.add("cmd.exe");
		filtros.add("/U");
		filtros.add("/C");
	}
	
	public void executarComando(List<String> comandosRecebidos) {
		String[] comandos = comandosRecebidos.toArray(new String[0]);
		executarComando(comandos);
	}
	
	public void executarComando(String... comandosRecebidos) {
		comandos.addAll(filtros);
		comandos.addAll(Arrays.asList(comandosRecebidos));
		ProcessBuilder builder = new ProcessBuilder(comandos);
		builder.redirectErrorStream(true);

		ativo = true;
		try {
			processo = builder.start();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()));
			
			worker = new SwingWorker<Void, String>() {
				@Override
				protected Void doInBackground() throws Exception {
					String saida = null;
					while ((saida = reader.readLine()) != null) {
						publish(saida);
					}
					return null;
				}
				@Override
				protected void process(List<String> publicado) {
					if (ativo) {
						filtragem.encaminharMensagem(publicado, getProcesso());
					}
				}
				@Override
				protected void done() {
					EnumProcessos processo = getProcesso();
					comandos.clear();
					setChanged();
					if (ativo) {
						switch (processo) {
						case RELEASE:
							notifyObservers(EnumRespostas.RELEASED);
							break;
						case GATEWAY:
							notifyObservers(EnumRespostas.GATEWAY);
							break;
						case RENEW:
							notifyObservers(EnumRespostas.RENEWED);
							break;
						default:
							notifyObservers(EnumRespostas.FIM);
							break;
						}
					}
				}
			};
			worker.execute();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void cancelarProcesso() {
		if (processo != null && worker != null) {
			setChanged();
			notifyObservers(EnumRespostas.INTERROMPIDO);
			try {
				ProcessBuilder killerProcesso = new ProcessBuilder("taskkill", "/F", "/IM", comandos.get(3) + ".exe");
				@SuppressWarnings("unused")
				Process p = killerProcesso.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException | IndexOutOfBoundsException e1) {
				/* Em branco - o killer não executa quando a List comandos está vazia ou não contém o terceiro índice */
			}
			ativo = false;
			worker.cancel(true);
			processo.destroy();
			processo = null;
		}
	}
	
	public EnumProcessos getProcesso() {
		return ParserComando.refinarProcesso(comandos);
	}
}