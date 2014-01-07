package principal;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Janela extends JFrame{
	public Janela(){
		setIconImage((new ImageIcon("resources\\icon.gif")).getImage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Mighty Z");
		setSize(640, 480);
		setLocationRelativeTo(null);
		setVisible(true);
		setContentPane(new Cenario());
	}
}
