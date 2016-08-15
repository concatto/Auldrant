package br.univali.minseiscluster.model;

import java.util.Iterator;

public class Replication implements Iterable<Time> {
	private Time[] times;
	
	public Replication(Time[] times) {
		this.times = times;
	}

	public Time[] getTimes() {
		return times;
	}

	@Override
	public Iterator<Time> iterator() {
		return new ArrayIterator<>(times);
	}
}
