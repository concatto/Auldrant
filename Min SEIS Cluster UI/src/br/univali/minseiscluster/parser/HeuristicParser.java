package br.univali.minseiscluster.parser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.univali.minseiscluster.model.Attempt;
import br.univali.minseiscluster.model.PlainEdge;
import br.univali.minseiscluster.model.Heuristic;
import br.univali.minseiscluster.model.NodeEvent;
import br.univali.minseiscluster.model.Replication;
import br.univali.minseiscluster.model.Time;

public class HeuristicParser {
	private static ByteBuffer buffer = ByteBuffer.allocateDirect(4);
	
	public static String extractGraphPath(String line) {
		return line.substring(line.indexOf(':') + 1);
	}

	public static List<List<String>> extractCommunities(String line) {
		return Arrays.stream(line.substring(line.indexOf(':') + 1).split(";"))
			.map(s -> s.split(","))
			.map(s -> Arrays.stream(s)
					.mapToInt(Integer::parseInt)
					.mapToObj(i -> String.valueOf(i + 1))
					.collect(Collectors.toList()))
			.collect(Collectors.toList());
	}
	
	public static Heuristic parse(String path) throws IOException {
		FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
		
		String configPath = readLine(channel);
		int totalTime = read(4, channel);
		int k = read(4, channel);
		int totalAttempts = read(4, channel);
		int totalReplications = read(4, channel);
		int adjacents = read(4, channel);
		int infectedQtd = read(4, channel);
		int[] infectedVerts = new int[infectedQtd];
		
		for (int i = 0; i < infectedVerts.length; i++) {
			infectedVerts[i] = read(4, channel);
		}		
		
		Attempt[] attempts = new Attempt[totalAttempts]; 
		for (int att = 0; att < attempts.length; att++) {
			PlainEdge[] removedEdges = new PlainEdge[k];
			
			for (int edge = 0; edge < k; edge++) {
				int source = read(4, channel);
				int target = read(4, channel);
				
				removedEdges[edge] = new PlainEdge(source, target);
			}
						
			Replication[] replications = new Replication[totalReplications];
			
			for (int rep = 0; rep < replications.length; rep++) {
				Time[] times = new Time[totalTime];
				
				for (int t = 0; t < times.length; t++) {
					int visits = read(4, channel);
					Map<Integer, Integer[]> visitData = new HashMap<>(visits);
					
					for (int i = 0; i < visits; i++) {
						int idVisit = read(4, channel);
						int qtdVisit = read(4, channel);
						
						Integer[] idVisits = new Integer[qtdVisit];
						for (int j = 0; j < idVisits.length; j++) {
							idVisits[j] = read(4, channel);
						}
						
						visitData.put(idVisit, idVisits);
					}
					
					int verts = read(4, channel);
					NodeEvent[] events = new NodeEvent[verts];
					
					for (int i = 0; i < events.length; i++) {
						int index = read(4, channel);
						int state = read(1, channel);
						
						events[i] = new NodeEvent(index, state);
					}
					
					times[t] = new Time(visitData, events);
				}
				
				replications[rep] = new Replication(times);
			}
			
			attempts[att] = new Attempt(replications, removedEdges);
		}
		
		channel.close();
		
		return new Heuristic(attempts, infectedVerts, k, totalReplications, totalTime, adjacents, configPath);
	}
	
	
	private static String readLine(FileChannel channel) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(16);
		StringBuilder builder = new StringBuilder();
		char c;
		
		while (true) {
			channel.read(buf);
			buf.flip();
			while (buf.remaining() > 0) {
				c = (char) buf.get();
				
				if (c == '\0') {
					channel.position(channel.position() - buf.remaining());
					return builder.toString();
				}
				builder.append(c);
			}
			buf.clear();
		}
	}


	private static int read(int bytes, FileChannel channel) throws IOException {
		buffer.limit(bytes);
		channel.read(buffer);
		buffer.flip();
		
		int total = 0;
		for (int i = 0; i < bytes; i++) {
			total = total | ((buffer.get() & 0xFF) << (8 * (bytes - i - 1)));
		}
		
		buffer.clear();
		return total;
	}
}
