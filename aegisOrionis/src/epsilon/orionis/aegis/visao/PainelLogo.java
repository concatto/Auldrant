package epsilon.orionis.aegis.visao;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PainelLogo extends JPanel {
	private BufferedImage logo;
	
	public PainelLogo() {
		try {
			logo = ImageIO.read(new File("res/logo.fw.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setPreferredSize(new Dimension(logo.getWidth(), logo.getHeight() + 10));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(logo, 15, 5, null);
	}
}
