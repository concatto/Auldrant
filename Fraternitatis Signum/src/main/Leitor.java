package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

public class Leitor {
	private InputStream in;
	private BufferedReader reader;
	
	private List<String> membros = new ArrayList<String>();
	private List<Boolean> status = new ArrayList<Boolean>();
	
	public Leitor() {
		
	}
	
	public void atualizar(final JComboBox<Object> box) {
		try {
			final int indexSelecionado;
			int indexTeste = box.getSelectedIndex();
			if (indexTeste == -1) {
				indexSelecionado = 0;
			} else {
				indexSelecionado = indexTeste;
			}
			
			in = new FileInputStream("res/database.txt");
			reader = new BufferedReader(new InputStreamReader(in));
			String linha;
			
			membros.clear();
			status.clear();
			
			while ((linha = reader.readLine()) != null) {
				linha = Normalizer.normalize(linha, Normalizer.Form.NFD);
				String[] valores = linha.split("\\s");
				membros.add(valores[0]);
				status.add(valores[1].startsWith("true"));
			}
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					box.setModel(new DefaultComboBoxModel<>(membros.toArray()));
					box.setSelectedIndex(indexSelecionado);
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getMembros() {
		return membros;
	}

	public List<Boolean> getStatus() {
		return status;
	}
}
