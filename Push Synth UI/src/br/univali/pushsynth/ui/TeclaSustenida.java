package br.univali.pushsynth.ui;

import javafx.scene.paint.Color;

public class TeclaSustenida extends Tecla {
	public TeclaSustenida() {
		this(false);
	}
	
	public TeclaSustenida(boolean vazia) {
		super(14, 80, vazia ? Color.TRANSPARENT : Color.valueOf("#555555"), vazia ? Color.TRANSPARENT : Color.valueOf("#0F0F0F"));
		
		if (!vazia) setStroke(Tecla.OUTLINE);
	}
}
