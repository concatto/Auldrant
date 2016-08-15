package orionis.delta.sandbox.javafx;

import javafx.application.Application;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class ThreeDee extends Application {
	private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	
	public static void main(String[] args) {
		launch(args);
	}

	public Shape3D make3DObject(double x, double y) {
		Sphere c = new Sphere(200);
		PhongMaterial material = new PhongMaterial();
		
		material.setDiffuseMap(new Image("http://www.redbaron85.com/RES-ARTICOLI/Tutorial-mappare-renderizzare-texture-pianeti-Jupiter-Texture.jpg"));
		c.setEffect(new ColorAdjust(0, 0, 0.5, 0));
//		material.setDiffuseColor(Color.DEEPSKYBLUE);
//		material.setSpecularColor(Color.WHITE);
		
		c.setMaterial(material);
		c.setLayoutX(y);
		c.setLayoutY(y);
		c.getTransforms().add(rotateX);
		c.getTransforms().add(rotateY);
		return c;
	}

	@Override
	public void start(Stage primaryStage) {
		AmbientLight ambient = new AmbientLight();
		Shape3D shape = make3DObject(300, 300);
		PointLight point = new PointLight();
		point.setLayoutX(100);
		Group root = new Group();
		
		ambient.setColor(Color.LIGHTBLUE);
		point.setColor(Color.DARKBLUE);
		root.getChildren().addAll(shape, point, ambient);
		
		SubScene sub = new SubScene(root, 600, 600, true, SceneAntialiasing.BALANCED);
		Scene scene = new Scene(new Group(sub), 600, 600);
		
		PerspectiveCamera cam = new PerspectiveCamera(false);
		scene.setOnMouseMoved(e -> {
			rotateX.setAngle(e.getY() / 2);
			rotateY.setAngle(e.getX() / 2);
		});
		sub.setCamera(cam);
		primaryStage.setTitle("cosmosOrionis");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
