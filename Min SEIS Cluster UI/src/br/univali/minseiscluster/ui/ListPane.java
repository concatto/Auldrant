package br.univali.minseiscluster.ui;

import br.univali.minseiscluster.model.HeuristicSnapshot;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class ListPane extends VBox {
	private Button saveButton = new Button("Salvar");
	private ListView<HeuristicSnapshot> list = new ListView<>();
	
	public ListPane() {
		super(5);
		setMinWidth(150);
		saveButton.setMaxWidth(Double.MAX_VALUE);
	}
	
	public void setSnapshots(ObservableList<HeuristicSnapshot> snapshots) {
		list.getItems().setAll(snapshots);
	}
	
	public void onSave(EventHandler<ActionEvent> action) {
		saveButton.setOnAction(action);
	}
}
