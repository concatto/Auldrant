package br.univali.temporalevaluator;

public class Event {
	private int identifier;
	private long time;
	
	public Event(int identifier, long time) {
		this.identifier = identifier;
		this.time = time;
	}

	public int getIdentifier() {
		return identifier;
	}

	public long getTime() {
		return time;
	}
}
