package br.univali.minseiscluster.model;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;

public class StatefulGraph {
	private Map<String, State> nodes;
	private Map<String, State> edges;
	
	public StatefulGraph(Map<String, State> nodes, Map<String, State> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public Map<String, State> getNodes() {
		return nodes;
	}
	
	public Map<String, State> getEdges() {
		return edges;
	}
	
	public static StatefulGraph adapt(Graph g) {
		return new StatefulGraph(adaptCollection(g.getNodeSet()), adaptCollection(g.getEdgeSet()));
	}
	
	private static Map<String, State> adaptCollection(Collection<? extends Element> collection) {
		return collection.stream()
				.collect(Collectors.toMap(e -> e.getId(), e -> State.SUSCEPTIBLE));
	}

	public void setEdgeState(PlainEdge edge, State newState) {
		String[] ids = {edge.regularID(), edge.reversedID()};
		
		for (String id : ids) {
			if (edges.containsKey(id)) {
				edges.put(id, newState);
			}
		}
	}
	
	public void setNodeState(String nodeId, State newState) {
		if (nodes.containsKey(nodeId)) {
			nodes.put(nodeId, newState);
		}
	}
	
	public void setNodeState(int nodeId, State newState) {
		setNodeState(String.valueOf(nodeId), newState);
	}

	public void resetNodeStates() {
		for (String key : nodes.keySet()) {
			nodes.put(key, State.SUSCEPTIBLE);
		}
	}
}
