package unidades;

public class Raid extends Unidade {
	
	public Raid() {
		super();
	}

	public Raid(int x, int y) {
		super(x, y);
	}
	
	public Raid(int x, int y, int largura, int altura) {
		super(x, y, largura, altura);
	}
	
	public Raid(int x, int y, int largura, int altura, double horizontal, double vertical) {
		super(x, y, largura, altura, horizontal, vertical);
	}

	@Override
	public void run() {
		while (true) {			
			try {
				Thread.sleep(getIntervalo());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
