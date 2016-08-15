package br.univali.minseiscluster.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import br.univali.minseiscluster.parser.GraphParser;
import br.univali.minseiscluster.parser.HeuristicParser;

public class Heuristic {
	public static final String SUSCEPTIBLE = "susceptible";
	public static final String EXPOSED = "exposed";
	public static final String INFECTED = "infected";
	
	private List<List<String>> communities;
	private Graph graph;
	private StatefulGraph statefulGraph;
	private List<HeuristicSnapshot> snapshots;
	private Attempt[] data;
	private int[] infecteds;
	private int k;
	private int replications;
	private int totalTime;
	private int adjacents;
	private int events;
	private String configPath;
	private String graphPath;
	
	public Heuristic(Attempt[] data, int[] infecteds, int k, int replications, int totalTime, int adjacents, String configPath) {
		this.data = data;
		this.infecteds = infecteds;
		this.k = k;
		this.replications = replications;
		this.totalTime = totalTime;
		this.adjacents = adjacents;
		this.configPath = configPath;
		
		for (Attempt attempt : data) {
			for (Replication replication : attempt) {
				for (Time time : replication) {
					events += time.getEvents().length;
				}
			}
		}

		try {
			readGraph(configPath);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		statefulGraph = StatefulGraph.adapt(graph);
		snapshots = new ArrayList<>();
		saveSnapshots();
	}

	private void saveSnapshots() {
		for (Attempt att : data) {
			for (PlainEdge edge : att.getRemovedEdges()) {
				statefulGraph.setEdgeState(edge, State.REMOVED);
			}
			
			for (Replication repl : att) {
				for (int vert : infecteds) {
					statefulGraph.setNodeState(vert, State.INFECTED);
				}
				
				List<PlainEdge> visitedEdges = new ArrayList<>();
				for (Time time : repl) {
					takeSnapshot();
					
					//Redefinição das arestas
					for (PlainEdge edge : visitedEdges) {
						statefulGraph.setEdgeState(edge, State.SUSCEPTIBLE);
					}
					
					visitedEdges = time.getVisits().entrySet().stream()
							.flatMap(this::mapToEdges)
							.collect(Collectors.toList());
					
					//Troca dos estados das arestas
					for (PlainEdge edge : visitedEdges) {
						statefulGraph.setEdgeState(edge, State.INFECTED);
					}
					
					//Troca dos estados dos vértices
					for (NodeEvent event : time) {
						statefulGraph.setNodeState(event.getNodeId(), event.getNewState());
					}
				}
				
				//Estado final
				takeSnapshot();
				
				statefulGraph.resetNodeStates();
			}
		}
	}

	private Stream<PlainEdge> mapToEdges(Entry<Integer, Integer[]> entry) {
		return Arrays.stream(entry.getValue()).map(visit -> new PlainEdge(entry.getKey(), visit));
	}

	private void takeSnapshot() {
		snapshots.add(HeuristicSnapshot.save(statefulGraph));
	}

	private void readGraph(String configPath) throws IOException, URISyntaxException {
		BufferedReader reader = Files.newBufferedReader(Paths.get("C:/Users/Fernando/Downloads/instancia1.txt"));
		
		graphPath = HeuristicParser.extractGraphPath(reader.readLine());
		communities = HeuristicParser.extractCommunities(reader.readLine());
		
		String name = graphPath.substring(graphPath.lastIndexOf('/') + 1);
		URL style = getClass().getClassLoader().getResource("style.css");
		URL graphUrl = getClass().getClassLoader().getResource(name);
		
		graph = new SingleGraph(name);
		graph.addAttribute("ui.stylesheet", "url('" + style.toString() + "')");
		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.quality");
		
		GraphParser.parse(new File(graphUrl.toURI()), this::addNode, this::addEdge);
	}
	
	private void addNode(String id) {
		graph.addNode(id).addAttribute("ui.label", id);
	}
	
	private void addEdge(String a, String b) {
		graph.addEdge(a + "-" + b, a, b);
	}

	public Attempt[] getData() {
		return data;
	}

	public int getK() {
		return k;
	}

	public int[] getInfecteds() {
		return infecteds;
	}
	
	public int getAdjacents() {
		return adjacents;
	}
	
	public String getConfigPath() {
		return configPath;
	}
	
	public List<List<String>> getCommunities() {
		return communities;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public StatefulGraph getStatefulGraph() {
		return statefulGraph;
	}
	
	public List<HeuristicSnapshot> getSnapshots() {
		return snapshots;
	}
	
	@Override
	public String toString() {
		return String.format("MinSEISCluster{k=%d,adjacents=%d}: %d attempts, %d replications, %d times, %d events.",
				k, adjacents, data.length, replications, totalTime, events);
	}
}
