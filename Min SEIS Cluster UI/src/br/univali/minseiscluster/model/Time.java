package br.univali.minseiscluster.model;

import java.util.Iterator;
import java.util.Map;

public class Time implements Iterable<NodeEvent> {
	private NodeEvent[] events;
	private Map<Integer, Integer[]> visits;
	
	public Time(Map<Integer, Integer[]> visits, NodeEvent[] events) {
		this.visits = visits;
		this.events = events;
	}

	public NodeEvent[] getEvents() {
		return events;
	}

	public Map<Integer, Integer[]> getVisits() {
		return visits;
	}
	
	@Override
	public Iterator<NodeEvent> iterator() {
		return new ArrayIterator<>(events);
	}
}
