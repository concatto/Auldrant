package br.univali.minseiscluster.model;

public class StatefulGraphElement {
	private String id;
	private State state;
	
	public StatefulGraphElement(String id, State state) {
		this.id = id;
		this.state = state;
	}
	
	public String getId() {
		return id;
	}
	
	public State getState() {
		return state;
	}
}
