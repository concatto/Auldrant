package br.univali.minseiscluster.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Controller extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {		
		MSCScene scene = new MSCScene();
		primaryStage.setOnCloseRequest(e -> scene.finish());
		
		primaryStage.setScene(scene);
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		System.setProperty("swing.jlf.contentPaneTransparent", "true");
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		launch(args);
	}
}
