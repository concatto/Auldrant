package principal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Projetil extends JPanel{
	private BufferedImage[] tiroA;
	private BufferedImage[] tiroB;
	private BufferedImage[] tiroC;
	Animacao anim = new Animacao();
	
	private final int largura = 144;
	private final int altura = 30;
	
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
		
		setDoubleBuffered(true);
		setVisible(true);
		setSize(largura, altura);
		setBackground(new Color(0f,0f,0f,0f));
	}
	
	public boolean isMoveDireita() {
		return moveDireita;
	}

	public void setMoveDireita(boolean moveDireita) {
		this.moveDireita = moveDireita;
	}

	public void setTiroA(){
		anim.setFrame(tiroA);
		anim.setIntervalo(-1);
	}
	
	public void setTiroB(){
		anim.setFrame(tiroB);
		anim.setIntervalo(40);
	}
	
	public void setTiroC(){
		anim.setFrame(tiroC);
		anim.setIntervalo(40);
	}

	public boolean isAtivo() {
		return ativo;
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void paintComponent(Graphics g) {
		BufferedImage imagem = anim.getImage();
		if (!moveDireita){
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		    tx.translate(-imagem.getWidth(null), 0);
		    AffineTransformOp op = new AffineTransformOp(tx,
		        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		    imagem = op.filter(imagem, null);
		}
		g.drawImage(imagem, 0, 0, null);
	}
	
	public void atualizar(){
		anim.atualizarAnim();
	}
}
