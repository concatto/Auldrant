package br.concatto.karnaugh;

public class WireframeInformation {
	private int coordinate;
	private int length;

	public WireframeInformation(int coordinate, int length) {
		this.coordinate = coordinate;
		this.length = length;
	}
	
	public int getCoordinate() {
		return coordinate;
	}
	
	public int getLength() {
		return length;
	}
}
