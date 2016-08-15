package orionis.epsilon.vinculus.visao;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import orionis.epsilon.vinculus.controlador.Controlador;
import orionis.epsilon.vinculus.controlador.ResourceLoader;

public abstract class AbstractFrame {
	protected JFrame frame;
	protected JPanel panel;
	protected Controlador controlador;
	
	public AbstractFrame(Controlador controlador) {
		this.controlador = controlador;
		frame = new JFrame("vinculusOrionis");
		panel = new JPanel();
		
		panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(panel);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		List<Image> icones = new ArrayList<>();
		icones.add(new ImageIcon(ResourceLoader.loadResource("/16.png")).getImage());
		icones.add(new ImageIcon(ResourceLoader.loadResource("/32.png")).getImage());
		
		frame.setIconImages(icones);
	}
	
	public void display() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		panel.requestFocusInWindow();
	}
	
	public void destroy() {
		frame.setVisible(false);
		frame.dispose();
	}
	
	public void terminate() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	public abstract void construct();
}
