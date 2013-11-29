package principal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Overlay extends JPanel{
	private BufferedImage[] charge;
	private int largura = 48;
	private int altura = 50;
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
		
		setDoubleBuffered(true);
		setVisible(true);
		setSize(largura, altura);
		setBackground(new Color(0f,0f,0f,0f));
		
		anim.setFrame(charge);
		anim.setIntervalo(40);
	}
	
	public void paintComponent(Graphics g){
		BufferedImage imagem = anim.getImage();
		g.drawImage(imagem, 0, 0, null);
	}
	
	public void atualizar(){
		anim.atualizarAnim();
	}
}
