package unidades;

public class Boss extends Unidade {

	public Boss() {
		super();
	}

	public Boss(int x, int y) {
		super(x, y);
	}
	
	public Boss(int x, int y, int largura, int altura) {
		super(x, y, largura, altura);
	}

	public Boss(int x, int y, int largura, int altura, double horizontal, double vertical) {
		super(x, y, largura, altura, horizontal, vertical);
	}

	@Override
	public void run() {
		
	}
}
