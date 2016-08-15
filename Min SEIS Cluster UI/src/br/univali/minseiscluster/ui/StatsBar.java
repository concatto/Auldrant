package br.univali.minseiscluster.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class StatsBar extends HBox {
	private Label susceptibleLabel = new Label();
	private Label exposedLabel = new Label();
	private Label infectedLabel = new Label();
	
	public StatsBar() {
		super(5);
		Label[] labels = {susceptibleLabel, exposedLabel, infectedLabel};
		
		for (Label label : labels) {
			label.setText("AAAAAAAA");
			label.setMaxWidth(Double.MAX_VALUE);
			label.setAlignment(Pos.CENTER);
			HBox.setHgrow(label, Priority.ALWAYS);
		}
		
		getChildren().addAll(susceptibleLabel, createSeparator(), exposedLabel, createSeparator(), infectedLabel);
	}

	private Separator createSeparator() {
		Separator s = new Separator(Orientation.VERTICAL);
		s.setPadding(Insets.EMPTY);
		s.setPrefWidth(3);
		return s;
	}
}
