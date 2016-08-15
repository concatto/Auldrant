package main;

import java.awt.Dimension;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Janela extends JFrame {
	public static Dimension tamanhoComBorda = new Dimension(600, 450);
	public static Dimension tamanho;
	
	public Janela() {
		setTitle("Estratégia");
		setSize(tamanhoComBorda);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		tamanho = new Dimension(getContentPane().getWidth(), getContentPane().getHeight());
		setContentPane(new Tela(tamanho));
	}
}
