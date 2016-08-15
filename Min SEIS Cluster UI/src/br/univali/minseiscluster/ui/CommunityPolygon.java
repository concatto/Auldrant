package br.univali.minseiscluster.ui;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicNode;

import br.univali.minseiscluster.algorithm.ConvexHull;
import javafx.collections.ObservableList;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.Polygon;

public class CommunityPolygon extends Polygon {
	private static Function<GraphicNode, Point3> transformer;
	private List<GraphicNode> nodes;

	public CommunityPolygon(List<GraphicNode> nodes) {
		this.nodes = nodes;
		
		setBlendMode(BlendMode.MULTIPLY);
		setMouseTransparent(true);
	}
	
	public void recalculate() {
		List<Point3> points = nodes.stream().map(transformer).collect(Collectors.toList());
		ConvexHull hull = ConvexHull.compute(points);
		
		ObservableList<Double> list = getPoints();
		list.clear();
		
		hull.getCoordinates().forEach(coord -> list.addAll(coord.x, coord.y));
		
		setTranslateX(hull.getLeft());
		setTranslateY(hull.getTop());
	}
	
	public static void prepareTransformer(Function<GraphicNode, Point3> transformer) {
		CommunityPolygon.transformer = transformer;
	}
	
	public void hide() {
		setVisible(false);
	}
	
	public void recalculateAndShow() {
		recalculate();
		setVisible(true);
	}
}
