package br.univali.minseiscluster.model;

public enum State {
	SUSCEPTIBLE("susceptible"),
	EXPOSED("exposed"),
	INFECTED("infected"),
	REMOVED("removed");
	
	private String name;
	
	State(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static String stateName(int state) {
		State[] states = values();
		return states[state].name;
	}
	
	public static State forName(String stateName) {
		for (State s : values()) {
			if (s.name == stateName) return s;
		}
		
		return null;
	}
}
