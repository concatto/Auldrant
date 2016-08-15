package epsilon.orionis.aether.modelo;

import java.io.File;

public enum EnumArquivos {
	ENDERECOS("sys/addresses.txt"),
	TEMPORARIO("sys/temp.txt");
	
	private File arquivo;
	private EnumArquivos(String caminho) {
		this.arquivo = new File(caminho);
	}
	
	public File getArquivo() {
		return arquivo;
	}
}
