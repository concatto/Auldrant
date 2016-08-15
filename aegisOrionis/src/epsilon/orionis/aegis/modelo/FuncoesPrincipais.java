package epsilon.orionis.aegis.modelo;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.filechooser.FileSystemView;

import epsilon.orionis.aegis.enumeradores.Comunicados;
import epsilon.orionis.aegis.enumeradores.Solicitacoes;

public class FuncoesPrincipais extends Observable implements Runnable {
	private Vector<File> drives = new Vector<>();
	private List<File> listaDrives = Arrays.asList(File.listRoots());
	private DefaultComboBoxModel<File> model = new DefaultComboBoxModel<>();
	private Thread t = new Thread(this, "Observador de Dispositivos");
	private FileSystemView fs = FileSystemView.getFileSystemView();
	
	private String identificadorDrive = "Disco removível";
	private int hashLista = 0;
	
	public FuncoesPrincipais() {
		t.start();
	}
	
	public void vacinar(File caminho) {
		File autorun = new File(caminho + "autorun.inf");
		boolean sucesso = autorun.mkdir();
		setChanged();
		if (sucesso) {
			notifyObservers(Comunicados.SUCESSOVACINA);
		} else {
			if (autorun.isDirectory()) {
				notifyObservers(Comunicados.ESTAVACINADO);
			} else {
				notifyObservers(Solicitacoes.ARQUIVOEXISTE);
			}
		}
	}
	
	public void prepararRemocao(File caminho) {
		File autorun = new File(caminho + "autorun.inf");
		setChanged();
		if (autorun.isDirectory()) {
			notifyObservers(Solicitacoes.SOLICITARCONFIRMACAO);
		} else {
			remover(caminho);
		}		
	}
	
	public void remover(File caminho) {
		File autorun = new File(caminho + "autorun.inf");
		boolean sucesso = autorun.delete();
		setChanged();
		if (sucesso) {
			notifyObservers(Comunicados.SUCESSOREMOCAO);
		} else if (!autorun.exists()) {
			notifyObservers(Comunicados.NAOEXISTE);
		} else {
			notifyObservers(Comunicados.FALHAREMOCAO);
		}
	}
	
	@Override
	public void run() {
		buscarDispositivos();
		int hash = 0;
		while(true) {
			buscarDispositivos();
			if (drives.hashCode() != hash) {
				hash = drives.hashCode();
				model = null;
				setChanged();
				if (drives.isEmpty()) {
					model = new DefaultComboBoxModel<>(new File[]{new File("Nenhum dispositivo.")});
					notifyObservers(Solicitacoes.DESATIVARFUNCOES);
				} else {
					model = new DefaultComboBoxModel<>(drives);
					notifyObservers(Solicitacoes.ATIVARFUNCOES);
				}
				setChanged();
				notifyObservers(model);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private void buscarDispositivos() {
		listaDrives = Arrays.asList(File.listRoots());
		if (listaDrives.hashCode() != hashLista) {
			hashLista = listaDrives.hashCode();
			for (File f : listaDrives) {
				try {
					if (fs.getSystemTypeDescription(f).equals(identificadorDrive)) {
						if (!drives.contains(f)) {
							drives.add(f);
						}
					}
				} catch (NullPointerException e) {
					break;
				}
			}
			for (ListIterator<File> iterator = drives.listIterator(); iterator.hasNext();) {
				if (!listaDrives.contains(iterator.next())) {
					iterator.remove();
				}
			}
		}
	}
}