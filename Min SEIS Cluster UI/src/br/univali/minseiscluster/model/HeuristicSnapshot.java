package br.univali.minseiscluster.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.graphstream.graph.Graph;

public class HeuristicSnapshot {
	private int index = -1;
	private List<StatefulGraphElement> nodes;
	private List<StatefulGraphElement> edges;
	
	public static HeuristicSnapshot save(StatefulGraph graph) {
		HeuristicSnapshot snap = new HeuristicSnapshot();
		
		snap.nodes = saveElements(graph.getNodes());
		snap.edges = saveElements(graph.getEdges());
		return snap;
	}
	
	private static List<StatefulGraphElement> saveElements(Map<String, State> elements) {
		return elements.entrySet().stream().map(entry -> {
			return new StatefulGraphElement(entry.getKey(), entry.getValue());
		}).collect(Collectors.toList());
	}

	public void restoreTo(Graph graph) {
		for (StatefulGraphElement node : nodes) {
			graph.getNode(node.getId()).addAttribute("ui.class", node.getState().getName());
		}
		
		for (StatefulGraphElement edge : edges) {
			graph.getEdge(edge.getId()).addAttribute("ui.class", edge.getState().getName());
		}
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public String toString() {
		return "Tempo " + (index == -1 ? "?" : String.valueOf(index));
	}
}
