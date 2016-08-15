package principal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Comandos{
	private int arquivoAtual = 0;
	private int tamanho = 0;
	private int progresso = 0;
	
	public void copyFolder(File src, File dest) {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			try {
				InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];

				if (arquivoAtual == tamanho-1) {
					JOptionPane.showMessageDialog(null, "Arquivos copiados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
					Copiar.frameBarra.dispose();
				}
				
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				
				if (progresso != arquivoAtual * 100 / tamanho){
					progresso = arquivoAtual * 100 / tamanho;
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							Copiar.barra.setValue(progresso);
						}
					});
				}
				
				arquivoAtual++;
				
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void count(File src) {
		if (src.isDirectory()) {
			File files[] = src.listFiles();
			for (File file : files) {
				count(file);
			}
		} else {
			tamanho++;
		}
	}
	
	public void emptyFolder(File dest) throws IOException {
		if (dest.isDirectory()) {
			if (dest.exists()) {
				String files[] = dest.list();
				for (String file : files) {
					File destFile = new File(dest, file);
					emptyFolder(destFile);
				}
			}
		} else {
			dest.delete();
		}
	}
}
