package br.univali.seis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

public class Vertice {
	public List<Vertice> adjacentes;
	public int id;
	public double x = -1;
	public double y = -1;
	public Color cor;
	
	public Vertice(int id, Vertice... adj) {
		this.id = id;
		adjacentes = new ArrayList<>(Arrays.asList(adj));
	}
	
	public void setPosicao(double x, double y) {
		if (!hasPosicao()) {
			this.x = x;
			this.y = y;
		}
	}
	
	public boolean hasPosicao() {
		return x != -1 && y != -1;
	}
	
	@Override
	public String toString() {
		String adj = adjacentes.stream()
				.map(vert -> String.valueOf(vert.id))
				.collect(Collectors.joining(", ", "{", "}"));
		
		return String.format("%d: %s", id, adj);
	}
}
