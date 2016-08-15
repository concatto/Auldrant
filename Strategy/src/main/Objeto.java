package main;

import java.awt.Color;
import java.awt.Graphics2D;

import utilidades.Diagonal;

public abstract class Objeto implements Runnable {
	private int x;
	private int y;
	private int largura;
	private int altura;
	private int intervalo = 5;
	private int offsetX;
	private int offsetY;
	private int destinoX;
	private int destinoY;
	private Thread thread = new Thread(this);
	private Diagonal diagonal;
	private Color cor = Color.black;
	
	
	//Construtor 1
	public Objeto() {
		this.thread.start();
	}
	
	//Construtor 2
	public Objeto(int x, int y) {
		this.x = x;
		this.y = y;
		this.thread.start();
	}
	
	//Construtor 3
	public Objeto(int x, int y, int largura, int altura) {
		this.x = x;
		this.y = y;
		this.largura = largura;
		this.altura = altura;
		this.thread.start();
	}
	
	//Construtor 4
	public Objeto(int x, int y, int largura, int altura, double horizontal, double vertical) {
		this.x = x;
		this.y = y;
		this.largura = largura;
		this.altura = altura;
		this.diagonal = new Diagonal(horizontal, vertical);
		this.thread.start();
	}
	
	public Diagonal getDiagonal() {
		return diagonal;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getLargura() {
		return largura;
	}

	public int getAltura() {
		return altura;
	}

	public int getIntervalo() {
		return intervalo;
	}
	
	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getDestinoX() {
		return destinoX;
	}

	public int getDestinoY() {
		return destinoY;
	}

	public Color getCor() {
		return cor;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public void setIntervalo(int intervalo) {
		this.intervalo = intervalo;
	}
	
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void setCor(Color cor) {
		this.cor = cor;
	}
	
	public void moverPara(int x, int y) {
		this.destinoX = x;
		this.destinoY = y;
		offsetX = x - this.x;
		offsetY = y - this.y;
	}
	
	public void desenhar(Graphics2D g) {
		g.setColor(cor);
		g.fillOval(x - (largura/2), y - (altura/2), largura, altura);
	}
}
