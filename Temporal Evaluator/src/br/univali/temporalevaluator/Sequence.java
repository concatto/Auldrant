package br.univali.temporalevaluator;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Sequence extends ArrayList<Event> {
	public Sequence(int identifier, long time) {
		super();
		add(new Event(identifier, time));
	}
	
	public Sequence and(int identifier, long time) {
		add(new Event(identifier, time));
		return this;
	}
}
