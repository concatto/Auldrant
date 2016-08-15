package epsilon.orionis.aether.visao;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class SFrame extends JFrame {
	private Class<?> criador;
	
	public SFrame(Object criador) {
		super();
		setCriador(criador.getClass());
		List<Image> icones = new ArrayList<Image>();
		icones.add(new ImageIcon("res/logo 128.png").getImage());
		icones.add(new ImageIcon("res/logo 64.png").getImage());
		icones.add(new ImageIcon("res/logo 32.png").getImage());
		icones.add(new ImageIcon("res/logo 16.png").getImage());
		
		setIconImages(icones);
	}
	
	public Class<?> getCriador() {
		return criador;
	}
	
	public void setCriador(Class<?> criador) {
		this.criador = criador;
	}
}
