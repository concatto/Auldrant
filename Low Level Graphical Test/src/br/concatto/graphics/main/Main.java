package br.concatto.graphics.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private final int width = 640;
	private final int height = 480;
	
	public Main() {
		super("Graphics");
		JPanel drawPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintCross(g);
				
				for (int i = -width; i < width; i++) {
					double x = i;
					double inc = Math.abs(x) / height;
					for (int y = 0; y < height; y++) {
						if (x < 0) x += inc;
						else x -= inc;
						g.drawRect((int) (x + width / 2), y - height / 2, 1, 1);
					}
				}
			}
		};
		
		drawPanel.setPreferredSize(new Dimension(width, height));
		add(drawPanel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	private void paintCross(Graphics g) {
		Color previousColor = g.getColor();
		g.setColor(Color.RED);
		
		for (int x = 0; x < width; x++) {
			g.drawRect(x, height / 2, 1, 1);
		}
		
		for (int y = 0; y < height; y++) {
			g.drawRect(width / 2, y, 1, 1);
		}
		
		g.setColor(previousColor);	
	}
}
