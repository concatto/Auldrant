package br.univali.minseiscluster.model;

public class PlainEdge {
	private int source;
	private int target;
	
	public PlainEdge(int source, int target) {
		this.source = source + 1;
		this.target = target + 1;
	}
	
	public int getSource() {
		return source;
	}
	
	public int getTarget() {
		return target;
	}
	
	public String regularID() {
		return String.format("%d-%d", source, target);
	}
	
	public String reversedID() {
		return String.format("%d-%d", target, source);
	}
	
	@Override
	public String toString() {
		return regularID();
	}
}
