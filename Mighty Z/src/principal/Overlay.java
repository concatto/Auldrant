package principal;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Overlay {
	private BufferedImage[] charge;
	private int largura = 48;
	private int altura = 50;
	private int x = 0;
	private int y = 0;
	Animacao anim = new Animacao();
	
	public Overlay(){
		try {
			charge = new BufferedImage[11];
			
			BufferedImage superCharge = ImageIO.read(new File("resources\\overlay.new.gif"));
			for (int i = 0; i < charge.length; i++) {
				charge[i] = superCharge.getSubimage(i*largura, 0, largura, altura);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		anim.setFrame(charge);
		anim.setIntervalo(40);
	}
	
	public int getWidth() {
		return largura;
	}

	public int getHeight() {
		return altura;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setLocation(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void desenhar(Graphics g){
		BufferedImage imagem = anim.getImage();
		g.drawImage(imagem, getX(), getY(), null);
	}
	
	public void atualizar(){
		anim.atualizarAnim();
	}
}
