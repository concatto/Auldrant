package inimigos;

public class Zyght extends Inimigo {
	public Zyght(int vida, int x, int y) {
		super("resources\\zyght.gif", 2, 39, 45);
		this.vida = vida;
		this.existe = true;
		this.x = x;
		this.y = y;
		setParado();
	}
	
	public void setParado() {
		anim.setFrameAtual(1);
		anim.setIntervalo(-1);
	}
}