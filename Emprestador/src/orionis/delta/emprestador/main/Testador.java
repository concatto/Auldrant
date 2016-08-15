package orionis.delta.emprestador.main;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Testador extends JFrame {
	private volatile boolean running = false;
	
	public Testador() {
		JPanel root = new JPanel();
		JButton start = new JButton("Iniciar");
		JButton finish = new JButton("Finalizar");
		
		root.add(start);
		root.add(finish);
		add(root);
		
		start.addActionListener(this::begin);
		finish.addActionListener(this::finish);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("Emprestador de Monitor");
		setVisible(true);
	}
	
	private void begin(ActionEvent e) {
		for (int i = 0; i < 2; i++) {
			new Thread(() -> {
				try {
					Queue<BufferedImage> images = new LinkedList<BufferedImage>();
					Robot robot = new Robot();
					running = true;
					while (running) {
						images.add(robot.createScreenCapture(new Rectangle(1280, 1024)));
						try {
							Thread.sleep(4);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					
					for (BufferedImage image : images) {
						ImageIO.write(image, "png", new File("C:/Transistor/" + System.currentTimeMillis() + ".png"));
					}
				} catch (AWTException | IOException ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage());
					ex.printStackTrace();
				}
			}).start();
			
			try {
				Thread.sleep(12);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void finish(ActionEvent e) {
		running = false;
	}
	
	public static void main(String[] args) {
		new Testador();
	}
}