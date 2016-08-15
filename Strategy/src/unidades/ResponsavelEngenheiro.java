package unidades;

import utilidades.Diagonal;

public class ResponsavelEngenheiro extends Unidade {

	public ResponsavelEngenheiro() {
		super();
	}

	public ResponsavelEngenheiro(int x, int y) {
		super(x, y);
	}
	
	public ResponsavelEngenheiro(int x, int y, int largura, int altura) {
		super(x, y, largura, altura);
	}

	public ResponsavelEngenheiro(int x, int y, int largura, int altura, double horizontal, double vertical) {
		super(x, y, largura, altura, horizontal, vertical);
	}

	@Override
	public void run() {
		Diagonal d = new Diagonal(getOffsetX(), getOffsetY());
		while (true) {
			d.realizarMovimento();
			
			System.out.println(d.getDiagonal());
			
			if (d.getVertical() < 1.0) {
				setX(getX() - d.getMovimento());
				setY(getY() - 1);
			} else {
				setX(getX() - 1);
				setY(getY() - d.getMovimento());
			}
			
			try {
				Thread.sleep(getIntervalo());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
