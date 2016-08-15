package br.concatto.karnaugh;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Wireframe {
	
	private static final Comparator<? super Point> X_COMPARATOR = (first, second) -> first.x > second.x ? 1 : (first.x < second.x ? -1 : 0);
	private static final Comparator<? super Point> Y_COMPARATOR = (first, second) -> first.y > second.y ? 1 : (first.y < second.y ? -1 : 0);
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Wireframe(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public static Wireframe analyze(ArrayList<Point> points, double toleranceX, double toleranceY, int width, int height) {
		WireframeInformation horizontal = extractDimensions(points, width, toleranceY, true);
		WireframeInformation vertical = extractDimensions(points, height, toleranceX, false);
		return new Wireframe(horizontal.getCoordinate(), vertical.getCoordinate(), horizontal.getLength(), vertical.getLength());
	}
	
	private static WireframeInformation extractDimensions(ArrayList<Point> points, int relativeDimension, double tolerance, boolean isWidth) {	
		Collections.sort(points, isWidth ? Y_COMPARATOR : X_COMPARATOR);
		ArrayList<Integer[]> candidates = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			int iteratingValue = isWidth ? point.y : point.x;
			int amount = 0;
			while (i < points.size() && isEqual(points.get(i++), iteratingValue, isWidth)) {
				amount++;
			}
			Integer[] details = {iteratingValue, amount};
			if (amount > relativeDimension / tolerance) candidates.add(details);
		}
		
		candidates.sort((first, second) -> first[0] > second[0] ? 1 : (first[0] < second[0] ? -1 : 0));
		
		List<Point> filteredPoints = points.stream()
				.filter(t -> (isWidth ? t.y : t.x) == candidates.get(0)[0])
				.sorted(isWidth ? X_COMPARATOR : Y_COMPARATOR)
				.collect(Collectors.toList());
		
		Point firstPoint = filteredPoints.get(0);
		Point lastPoint = filteredPoints.get(filteredPoints.size() - 1);
		
		if (isWidth) {
			return new WireframeInformation(firstPoint.x, lastPoint.x - firstPoint.x);
		} else {
			return new WireframeInformation(firstPoint.y, lastPoint.y - firstPoint.y);
		}
	}
	
	private static boolean isEqual(Point point, int comparator, boolean isWidth) {
		int value = isWidth ? point.y : point.x;
		return value == comparator;
	}
}
