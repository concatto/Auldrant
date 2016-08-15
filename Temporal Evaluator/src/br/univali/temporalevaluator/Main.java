package br.univali.temporalevaluator;

import java.util.ArrayList;

public class Main {
	public Main() {
		ArrayList<Sequence> sequences = new ArrayList<>();
		
		sequences.add(new Sequence(1, 100).and(1, 500).and(2, 600));
		
		Event e = new Event(1, 90);
		System.out.printf("%.3f", 100 * compare(e, sequences.get(0).get(0)));
	}
	
	double compare(Event actual, Event expected) {
		double delta = expected.getTime() - actual.getTime();
		delta /= expected.getTime();
		
		return 1.0 / (Math.pow(2, delta * 15));
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
