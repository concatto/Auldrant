package br.univali.seis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {
	private static final double ALTURA = 768;
	private static final double LARGURA = 1024;
	
	private Grafo grafo = new Grafo();
	private boolean vertices;
	private Random rng = new Random();
	
	private void processarLinha(String linha) {
		if (linha.charAt(0) == '*') {
			//Encontro da linha "*Edges"
			if (Character.toLowerCase(linha.charAt(1)) == 'e') {
				vertices = false;
			}
		} else {
			if (vertices) {
				int id = Integer.parseInt(linha.substring(0, linha.indexOf(' ')));
				Vertice v = new Vertice(id);
				v.cor = rng.nextBoolean() ? Color.FIREBRICK : Color.CORNFLOWERBLUE;
				grafo.add(v);
			} else {
				int primeiroEspaco = linha.indexOf(' ');
				int id = Integer.parseInt(linha.substring(0, primeiroEspaco));
				int adj = Integer.parseInt(linha.substring(primeiroEspaco + 1, linha.indexOf(' ', primeiroEspaco + 1)));
				
				grafo.getVertice(id).adjacentes.add(grafo.getVertice(adj));
			}
		}
	}

	private double gerarPosicao(double limite) {
		return (rng.nextDouble() * (limite - 20)) + 10; 
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		String caminho = "C:/Users/Fernando/Documents/Univali/Ciência/Grafos/normalizadas/";
		caminho += "jazz.net";
		
		vertices = true;
		try {
			Files.lines(Paths.get(caminho)).forEach(this::processarLinha);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Group root = new Group();
		
		double raio = 3.5;
		grafo.forEach(vert -> {
			vert.setPosicao(gerarPosicao(LARGURA), gerarPosicao(ALTURA));
			
			vert.adjacentes.stream()
					.forEach(adj -> {
						adj.setPosicao(gerarPosicao(LARGURA), gerarPosicao(ALTURA));
						
						Line linha = new Line(adj.x, adj.y, vert.x, vert.y);
						linha.setStroke(new LinearGradient(adj.x, adj.y, vert.x, vert.y, false, CycleMethod.NO_CYCLE, new Stop(0, adj.cor), new Stop(1, vert.cor)));
						linha.setStrokeWidth(0.2);
						root.getChildren().add(linha);
					});
			
//			Text id = new Text(String.valueOf(vert.id));
//			id.setFont(Font.font("Calibri", 14));
//			id.setFill(Color.WHITE);
//			StackPane container = new StackPane(new Rectangle(20, 20, Color.FIREBRICK), id);
//			container.setTranslateX(vert.x);
//			container.setTranslateY(vert.y);
			
			Circle circ = new Circle(raio, vert.cor);
			circ.setTranslateX(vert.x);
			circ.setTranslateY(vert.y);
			root.getChildren().add(circ);
		});
		
		primaryStage.setScene(new Scene(root, LARGURA, ALTURA));
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
//		launch(args);
	}
}
