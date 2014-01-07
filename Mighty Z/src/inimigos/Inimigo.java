package inimigos;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import principal.Animacao;

public class Inimigo {
	private final int largura;
	private final int altura;
	protected int x = 0;
	protected int y = 0;
	protected int vida;
	protected boolean rejeitarDano = false;
	protected boolean existe;
	protected BufferedImage[] imagem;
	protected Animacao anim = new Animacao();
	
	public Inimigo(String caminho, int frames, int largura, int altura) {
		imagem = new BufferedImage[frames];
		this.largura = largura;
		this.altura = altura;
		
		try {
			BufferedImage superImagem = ImageIO.read(new File(caminho));
			for (int i = 0; i < imagem.length; i++) {
				imagem[i] = superImagem.getSubimage(i*largura, 0, largura, altura);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void receberDano(int dano) {
		this.vida = this.vida - dano;
	}
	
	public void rejeitarDano() {
		this.rejeitarDano = true;
	}
	
	public void cancelarRejeicoes() {
		this.rejeitarDano = false;
	}
	
	public boolean isRejeitado() {
		return rejeitarDano;
	}
	
	public int getVida() {
		return vida;
	}
	
	public int getLargura() {
		return largura;
	}
	
	public int getAltura() {
		return altura;
	}
	
	public BufferedImage getImage() {
//		if (!Jogador.isMoveDireita()){
//			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//		    tx.translate(-imagem[frame].getWidth(null), 0);
//		    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//		    imagem[frame] = op.filter(imagem[frame], null);
//		}
		return imagem[anim.getFrameAtual()];
	}
	
	public boolean existe() {
		return existe;
	}
}
