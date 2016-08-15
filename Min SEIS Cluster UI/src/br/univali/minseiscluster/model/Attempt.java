package br.univali.minseiscluster.model;

import java.util.Iterator;

public class Attempt implements Iterable<Replication> {
	private PlainEdge[] removedEdges;
	private Replication[] replications;

	public Attempt(Replication[] replications, PlainEdge[] removedEdges) {
		this.replications = replications;
		this.removedEdges = removedEdges;
	}

	public Replication[] getReplications() {
		return replications;
	}
	
	public PlainEdge[] getRemovedEdges() {
		return removedEdges;
	}

	@Override
	public Iterator<Replication> iterator() {
		return new ArrayIterator<>(replications);
	}
}
