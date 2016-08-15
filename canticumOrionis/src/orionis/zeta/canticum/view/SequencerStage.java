package orionis.zeta.canticum.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import orionis.zeta.canticum.controller.Controller;

public class SequencerStage extends Stage {
	private Controller controller = Controller.getInstance();
	private FileChooser chooser = new FileChooser();
	private Button open = new Button("Abrir");
	private Button stop = new Button("Parar");
	private HBox buttons = new HBox(10, open, stop);
	private HBox barContainer = new HBox(2);
	private List<NoteRectangle> bars = new ArrayList<>();
	private VBox root = new VBox(20, buttons, barContainer);
	private Scene scene = new Scene(root);
	private SimpleBooleanProperty stoppableStatus = new SimpleBooleanProperty(false);
	
	public SequencerStage() {
		chooser.getExtensionFilters().add(new ExtensionFilter("Arquivos MIDI", "*.mid", "*.midi"));
		
		open.disableProperty().bind(stoppableStatus);
		open.setOnAction(e -> {
			File file = chooser.showOpenDialog(this);
			controller.startSequence(file);
			stoppableStatus.set(true);
		});
		
		stop.disableProperty().bind(stoppableStatus.not());
		stop.setOnAction(e -> {
			controller.stopSequence();
			stoppableStatus.set(false);
		});
		
		InnerShadow shadow = new InnerShadow(20, Color.DARKGOLDENROD);
		shadow.setInput(new InnerShadow(10, Color.BLACK));
		barContainer.setEffect(shadow);
		barContainer.setMaxHeight(Region.USE_PREF_SIZE);
		barContainer.setMinHeight(Region.USE_PREF_SIZE);
		barContainer.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 50% 0%, repeat, goldenrod, #F2C249)");
		barContainer.setAlignment(Pos.BOTTOM_CENTER);
		buttons.setAlignment(Pos.CENTER);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(30, 60, 30, 60));
		setOnCloseRequest(e -> controller.stopSequence());
		setTitle("canticumOrionis");
		setScene(scene);
	}

	public void initChannels(int channels) {
		ObservableList<Node> children = barContainer.getChildren();
		for (int i = 0; i < channels; i++) {
			NoteRectangle rect = new NoteRectangle();
			children.add(rect);
			bars.add(rect);
		}
		barContainer.setPadding(new Insets(5, 10, 1, 10));
		barContainer.setPrefHeight(NoteRectangle.getMaxHeight());
		barContainer.autosize();
		sizeToScene();
		centerOnScreen();
	}
	
	public void sendNote(int note, int track) {
		bars.get(track).playNote(note);
	}

	public void resetBars() {
		barContainer.getChildren().clear();
		barContainer.setPadding(Insets.EMPTY);
		barContainer.setPrefHeight(0);
		barContainer.autosize();
		sizeToScene();
		centerOnScreen();
	}
}
