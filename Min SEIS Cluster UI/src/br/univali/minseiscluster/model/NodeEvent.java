package br.univali.minseiscluster.model;

public class NodeEvent {
	private int nodeIndex;
	private State newState;
	
	public NodeEvent(int nodeIndex, int newState) {
		this.nodeIndex = nodeIndex;
		this.newState = State.values()[newState];
	}
	
	public int getNodeId() {
		return nodeIndex;
	}
	
	public State getNewState() {
		return newState;
	}
}
