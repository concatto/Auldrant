package br.univali.seis;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Grafo extends ArrayList<Vertice> {
	public Grafo() {
		super();
	}
	
	public Vertice getVertice(int id) {
		//Minigambiarra
		return get(id - 1);
	}
}
