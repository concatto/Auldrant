package br.univali.pushsynth.ui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public abstract class Tecla extends Rectangle {
	public static final Color OUTLINE = Color.valueOf("#4F4F4F");
	private Paint corSolta;
	private Paint corPressionada;

	Tecla(double largura, double altura, Paint corSolta, Paint corPressionada) {
		super(largura, altura);
		this.corSolta = corSolta;
		this.corPressionada = corPressionada;
		
		setFill(corSolta);
	}
	
	public void pressionar() {
		setFill(corPressionada);
	}
	
	public void soltar() {
		setFill(corSolta);
	}
}
