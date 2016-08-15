package br.univali.minseiscluster.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GraphParser {
	public static void parse(File file, Consumer<String> vertexConsumer, BiConsumer<String, String> edgeConsumer) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line;
		boolean verts = true;
		while ((line = reader.readLine()) != null) {
			if (line.charAt(0) == '*') {
				//Encontro da linha "*Edges"
				if (Character.toLowerCase(line.charAt(1)) == 'e') {
					verts = false;
				}
			} else {
				if (verts) {
					String id = line.substring(0, line.indexOf(' '));
					
					vertexConsumer.accept(id);
				} else {
					int primeiroEspaco = line.indexOf(' ');
					String id = line.substring(0, primeiroEspaco);
					String adj = line.substring(primeiroEspaco + 1, line.indexOf(' ', primeiroEspaco + 1));
					
					edgeConsumer.accept(id, adj);
				}
			}
		}
		
		reader.close();
	}
}
