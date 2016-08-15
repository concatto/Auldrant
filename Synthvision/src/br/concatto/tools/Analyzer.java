package br.concatto.tools;


public class Analyzer {
	private boolean foundHue = false;
	private int hueSum = 0;
	private int hueCount = 0;
	private Hue currentHue = null;
	private int y;
	private boolean sharp;
	
	public Analyzer(int y, boolean sharp) {
		this.y = y;
		this.sharp = sharp;
	}
	
	public void reset() {
		hueSum = 0;
		hueCount = 0;
		foundHue = false;
	}

	public void setFoundHue(boolean foundHue) {
		this.foundHue = foundHue;
	}
	
	public boolean hasFoundHue() {
		return foundHue;
	}

	public void setHueSum(int hueSum) {
		this.hueSum = hueSum;
	}
	
	public int getHueSum() {
		return hueSum;
	}

	public void incrementHueCount() {
		hueCount++;
	}
	
	public int getHueCount() {
		return hueCount;
	}

	public void setCurrentHue(Hue currentHue) {
		this.currentHue = currentHue;
	}
	
	public Hue getCurrentHue() {
		return currentHue;
	}

	public int getY() {
		return y;
	}

	public boolean isSharp() {
		return sharp;
	}
}