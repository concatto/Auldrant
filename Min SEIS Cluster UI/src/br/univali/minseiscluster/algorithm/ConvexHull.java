package br.univali.minseiscluster.algorithm;

import java.util.Arrays;
import java.util.List;

import org.graphstream.ui.geom.Point3;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ConvexHull {
	private double top;
	private double left;
	private double bottom;
	private double right;
	private double width;
	private double height;
	private GeometryFactory factory;
	private Geometry geometry;
	
	private ConvexHull() {
		factory = new GeometryFactory();
	}
	
	public static ConvexHull compute(List<Point3> points) {
		ConvexHull hull = new ConvexHull();
		GeometryFactory factory = hull.factory;
		Coordinate[] coords = points.stream().map(point -> new Coordinate(point.x, point.y)).toArray(Coordinate[]::new);
		
		Geometry mp = factory.createMultiPoint(coords);
		
		hull.geometry = mp.convexHull().buffer(20);
		hull.calculateAttributes();
		return hull;
	}
	
	private void calculateAttributes() {
		bottom = (right = Double.MIN_VALUE);
		top = (left = Double.MAX_VALUE);
		
		for (Coordinate p : geometry.getCoordinates()) {
			if (p.x < left) left = p.x;
			if (p.x > right) right = p.x;
			if (p.y < top) top = p.y;
			if (p.y > bottom) bottom = p.y;
		}

		width = right - left;
		height = bottom - top;
	}
	
	public double getTop() {
		return top;
	}
	
	public double getLeft() {
		return left;
	}
	
	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	public List<Coordinate> getCoordinates() {
		return Arrays.asList(geometry.getCoordinates());
	}
}
