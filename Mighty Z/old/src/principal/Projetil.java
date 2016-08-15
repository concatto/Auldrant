package principal;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Projetil {
	private BufferedImage[] tiroA;
	private BufferedImage[] tiroB;
	private BufferedImage[] tiroC;
	Animacao anim = new Animacao();
	
	private static int cargaMaxima = 2;
	private final int largura = 144;
	private final int altura = 30;
	private int x=0;
	private int y=0;
	private int nivel;
	
	private boolean ativo=false;
	private boolean moveDireita=true;
	
	public Projetil(){
		try {
			tiroA = new BufferedImage[1];
			tiroB = new BufferedImage[3];
			tiroC = new BufferedImage[3];

			tiroA[0] = ImageIO.read(new File("resources\\shot.a.gif"));
			
			BufferedImage superTiroB = ImageIO.read(new File("resources\\shot.b.gif"));
			for (int i = 0; i < tiroB.length; i++) {
				tiroB[i] = superTiroB.getSubimage(i*largura, 0, largura, altura);
			}
			
			BufferedImage superTiroC = ImageIO.read(new File("resources\\shot.c.gif"));
			for (int i = 0; i < tiroC.length; i++) {
				tiroC[i] = superTiroC.getSubimage(i*largura, 0, largura, altura);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public int getLargura() {
		return largura-110;
	}

	public int getAltura() {
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
	
	public boolean isMoveDireita() {
		return moveDireita;
	}

	public void setMoveDireita(boolean moveDireita) {
		this.moveDireita = moveDireita;
	}

	public void setTiroA(){
		this.nivel = 1;
		anim.setFrame(tiroA);
		anim.setIntervalo(-1);
	}
	
	public void setTiroB(){
		this.nivel = 2;
		anim.setFrame(tiroB);
		anim.setIntervalo(40);
	}
	
	public void setTiroC(){
		this.nivel = 3;
		anim.setFrame(tiroC);
		anim.setIntervalo(40);
	}

	public int getNivel() {
		return nivel;
	}
	
	public boolean isAtivo() {
		return ativo;
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void desenhar(Graphics g) {
		BufferedImage imagem = anim.getImage();
		if (!moveDireita){
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		    tx.translate(-imagem.getWidth(null), 0);
		    AffineTransformOp op = new AffineTransformOp(tx,
		        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		    imagem = op.filter(imagem, null);
		}
		g.drawImage(imagem, getX(), getY(), null);
	}
	
	public void atualizar(){
		anim.atualizarAnim();
	}

	public static int getCargaMaxima() {
		return cargaMaxima;
	}
}
