package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import unidades.Boss;
import unidades.Raid;
import unidades.ResponsavelEngenheiro;

@SuppressWarnings("serial")
public class Tela extends JPanel implements Runnable {
	private Thread thread = new Thread(this);
	private BufferedImage display;
	private Graphics2D g;
	
	private Boss boss;
	private Raid raid;
	private ResponsavelEngenheiro engenheiro;
	
	private boolean ativo = true;
	
	public Tela(Dimension tamanho) {
		display = new BufferedImage(tamanho.width, tamanho.height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) display.getGraphics();
		
		setVisible(true);
		setFocusable(true);
		setBackground(Color.black);
		setSize(Janela.tamanho);
		
		inicializar();
		
		thread.start();
	}
	
	private void inicializar() {
		raid = new Raid(Janela.tamanho.width/3, (int) (Janela.tamanho.height/1.2), 25, 25);
		boss = new Boss(Janela.tamanho.width/2, Janela.tamanho.height/2, 25, 25);
		engenheiro = new ResponsavelEngenheiro(Janela.tamanho.width/3, (int) (Janela.tamanho.height/1.2), 7, 7);
	}

	@Override
	public void run() {	
		engenheiro.moverPara(0, 0);
		while (ativo) {
			g.setColor(Color.white);
			g.fillRect(0, 0, Janela.tamanho.width, Janela.tamanho.height);
			
			boss.desenhar(g);
			raid.desenhar(g);
			engenheiro.desenhar(g);
			
			repaint();
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(display, 0, 0, null);
	}
}
