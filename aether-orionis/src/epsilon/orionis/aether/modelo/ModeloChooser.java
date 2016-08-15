package epsilon.orionis.aether.modelo;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ModeloChooser extends FileFilter {
	@Override
	public String getDescription() {
		String descricao = "Arquivo ORION (*.orn)";
		return descricao;
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
	        return true;
	    }
		
		String extension = null;
		String arquivo = f.toString();
		int i;
		try {
			i = arquivo.lastIndexOf(".");
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
		if (i != -1) {
			extension = arquivo.substring(i);
		}
		if (extension != null) {
	        if (extension.equals(".orn")) {
	        	return true;
	        } else {
	        	return false;
	        }
	    }
		return false;
	}
}
