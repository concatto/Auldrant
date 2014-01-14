package principal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class Comando {
	private List<String> colecaoSaida;
	private String saida;
	private String[] filtros;
	private String[] comandosFiltrados;
	private boolean completo = true;
	
	private SwingWorker<Void, String> worker;
	private Process processo = null;

	public Comando() {
		filtros = new String[3];
		filtros[0] = "cmd.exe";
		filtros[1] = "/U";
		filtros[2] = "/C";
	}
	
	public void executarComando(String... comandos) {
		comandosFiltrados = new String[filtros.length + comandos.length];
		
		for (int i = 0; i < filtros.length; i++) {
			comandosFiltrados[i] = filtros[i];
		}
		
		for (int i = 0; i < comandos.length; i++) {
			comandosFiltrados[i + filtros.length] = comandos[i];
		}
		
		ProcessBuilder builder = new ProcessBuilder(comandosFiltrados);
		builder.redirectErrorStream(true);

		try {
			processo = builder.start();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()));
			
			worker = new SwingWorker<Void, String>() {
				@Override
				protected Void doInBackground() throws Exception {
					completo = false;
					while ((saida = reader.readLine()) != null) {
						saida = saida.replace(procurarCaractereNulo(saida), "");
						publish(saida);
					}
					return null;
				}
				@Override
				protected void process(List<String> publicado) {
					colecaoSaida = publicado;
				}
				protected void done() {
					saida = null;
					completo = true;
				}
			};
			worker.execute();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void cancelarProcesso(boolean deveConferir) {
		if (deveConferir && processo == null) {
			JOptionPane.showMessageDialog(null, "Não há nenhum processo ativo.");
		} else if (processo != null && worker != null){
			worker.cancel(true);
			processo.destroy();
			processo = null;
			try {
				ProcessBuilder killer = new ProcessBuilder("taskkill", "/F", "/IM", comandosFiltrados[3] + ".exe");
				@SuppressWarnings("unused")
				Process p = killer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String procurarCaractereNulo(String linha) {
		String caractereNulo = "";
		
		for (int i = 0; i < linha.length(); i++) {
			if (Character.getName(linha.charAt(i)).equals("NULL")) {
				caractereNulo = String.valueOf(linha.charAt(i));
				break;
			}
		}
		
		return caractereNulo;
	}
	
	public List<String> getSaida() {
		return colecaoSaida;
	}
	
	public boolean isCompleto() {
		return completo;
	}
}