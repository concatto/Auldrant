package br.concatto.main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.Raster;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import br.concatto.tools.Analyzer;
import br.concatto.tools.Hue;
import br.concatto.tools.Note;

public class Watcher {
	private static final double VALUE_THRESHOLD = 0.25;
	private static final double SATURATION_THRESHOLD = 0.30;
	private static final int REGULAR_KEYS = 52;
	private static final int SHARP_X_SHIFT = 8;
	private Robot robot;
	private Raster data;
	private Rectangle bounds;
	private int[] array = new int[4];
	private boolean running = false;
	
	private Queue<Note> notes = new ArrayDeque<>();
	private Set<Note> current = new HashSet<>();
	private Set<Note> previous = new HashSet<>();
	
	public Watcher() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return;
		}
		
		
//		rect = new Rectangle(272, 512, 720, 18);		
	}
	
	public void start(Rectangle bounds) {
		this.bounds = bounds;
		
		running = true;
		new Thread(() -> {
			while (running) {
				data = robot.createScreenCapture(new Rectangle(1280, 1024)).getData(bounds);
				
				Note.defineFirstNote('a', 0);
				
				Analyzer regular = new Analyzer(bounds.y + bounds.height - 1, false);
				Analyzer sharp = new Analyzer(bounds.y, true);
				
				for (int i = bounds.x; i < bounds.width + bounds.x; i++) {
					analyze(i, regular);
					analyze(i, sharp);
				}
				
				previous.clear();
				previous.addAll(current);
				current.clear();
			}
		}).start();
	}
	
	public void stop() {
		running = false;
	}
	
	public Queue<Note> getNotes() {
		return notes;
	}
	
	private void analyze(int x, Analyzer a) {
		Hue hue = findColor(data.getPixel(x, a.getY(), array));
		
		if (hue != null) { 
			a.setHueSum(a.hasFoundHue() ? a.getHueSum() + x : x);
			a.incrementHueCount();
			a.setFoundHue(true);
			a.setCurrentHue(hue);
		} else {
			if (a.getHueSum() > 0) {
				int averageX = a.getHueSum() / a.getHueCount();
				a.reset();
				
				if (a.isSharp() && !verifySharp(averageX, bounds.y, bounds.y + bounds.height)) return;
				save(findColor(data.getPixel(averageX, a.getY(), array)), averageX, a.isSharp());
			}
		}
	}
	
	private boolean verifySharp(int averageX, int startY, int endY) {
		int[] rgb = data.getPixel(averageX, startY, array);
		float[] starting = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);
		for (int i = startY; i < endY; i++) {
			rgb = data.getPixel(averageX, i, array);
			float[] newColors = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);
			
			float[] deltas = new float[3];
			for (int j = 0; j < deltas.length; j++) {
				deltas[j] = starting[j] - newColors[j];
			}
			if (deltas[1] >= 0.2) return true;
		}
		return false;
	}

	private void save(Hue hue, int hueSum, boolean sharp) {
		int x = hueSum - bounds.x;
		if (sharp) x -= SHARP_X_SHIFT;
		int index = (int) Math.floor(x / (bounds.width / (double) REGULAR_KEYS));
		
		Note note = new Note(hue, index, sharp);
		if (!previous.contains(note)) notes.offer(note);
		
		current.add(note);
	}

	private Hue findColor(int[] colors) {
		float[] hsv = Color.RGBtoHSB(colors[0], colors[1], colors[2], null);
		if (hsv[1] > SATURATION_THRESHOLD && hsv[2] > VALUE_THRESHOLD) {
			float h = hsv[0] * 360;
			Hue[] hues = Hue.values();
			int index = (int) Math.floor(((h + 15) % 360) / 30);
			return hues[index];
		}
		
		return null;
	}
}
