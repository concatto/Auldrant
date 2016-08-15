package utilidades;

public class Diagonal {
	private double horizontal;
	private double vertical;
	private double diagonal;
	private double total;
	private int movido;
	private int mover;
	
	public Diagonal(double horizontal, double vertical) {
		horizontal = Math.abs(horizontal);
		vertical = Math.abs(vertical);
		this.horizontal = horizontal;
		this.vertical = vertical;
		if (this.horizontal == this.vertical) {
			diagonal = horizontal/vertical;
		} else {
			diagonal = vertical/horizontal;
		}
		total = diagonal;
		movido = 0;
		mover = 0;
	}
	
	public void atualizarDiagonal(double horizontal, double vertical) {
		this.horizontal = horizontal;
		this.vertical = vertical;
	}
	
	public double getHorizontal() {
		return horizontal;
	}

	public double getVertical() {
		return vertical;
	}

	public double getDiagonal() {
		return diagonal;
	}

	public double getTotal() {
		return total;
	}

	public int getMovido() {
		return movido;
	}
	
	public int getMovimento() {
		return mover;
	}
	
	public void realizarMovimento() {
		diagonal = horizontal/vertical;
		
		mover = (int) (Math.floor(total) - movido);
		
		total += diagonal;
		movido += mover;
	}
}
