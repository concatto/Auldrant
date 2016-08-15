package orionis.delta.sandbox.animation;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class Animator extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Rectangle path = new Rectangle(200, 200);
		Circle circle = new Circle(20, Color.DODGERBLUE);
		PathTransition transition = new PathTransition(Duration.seconds(10), path, circle);
		ProgressIndicator progress = new ProgressIndicator();
		progress.setPrefSize(300, 300);
		
		Pagination view = new Pagination();
		view.setPageCount(3);
		view.setPageFactory(param -> new Button(String.valueOf(param)));
		VBox root = new VBox(new ToolBar(new Button("Primeiro"), new Button("Segundo"), new Separator(), new Button("Terceiro")), view, circle);
		Scene scene = new Scene(root, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		root.setAlignment(Pos.CENTER);
		transition.play();
		
		transition.currentTimeProperty().addListener((a, b, c) -> {
			progress.setProgress(c.toSeconds() / transition.getTotalDuration().toSeconds());
		});
	}
}
