package unidades;

import main.Objeto;

public abstract class Unidade extends Objeto {

	public Unidade() {
		super();
	}
	
	public Unidade(int x, int y) {
		super(x, y);
	}
	
	public Unidade(int x, int y, int largura, int altura) {
		super(x, y, largura, altura);
	}

	public Unidade(int x, int y, int largura, int altura, double horizontal, double vertical) {
		super(x, y, largura, altura, horizontal, vertical);
	}
	
}