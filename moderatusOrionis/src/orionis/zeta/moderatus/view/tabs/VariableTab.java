package orionis.zeta.moderatus.view.tabs;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class VariableTab extends AbstractTab {
	@SuppressWarnings("serial")
	private List<StringProperty> fieldProperties = new ArrayList<StringProperty>() {
		@Override
		public boolean add(StringProperty e) {
			boolean success = super.add(e);
			if (success) insertField(e);
			return success;
		}
		
		@Override
		public void clear() {
			super.clear();
			fields.getChildren().clear();
		}
		
		@Override
		public boolean remove(Object o) {
			for (int i = 0; i < size(); i++) {
				if (o.equals(get(i))) {
					set(i, null);
					return true;
				}
			}
			return false;
		}
	};
	
	private VBox fields = new VBox(8);
	private Label label = new Label();
	private Button add = new Button();
	private ScrollPane scroll = new ScrollPane(fields);
	private BorderPane upper = new BorderPane();
	private VBox wrapper = new VBox(6, upper, scroll);
	
	public VariableTab(String title, String labelText) {
		super(title);
		add.setAlignment(Pos.CENTER_RIGHT);
		add.setOnAction(e -> fieldProperties.add(new SimpleStringProperty()));
		add.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/add.png"))));
		fields.setPadding(new Insets(7));
		scroll.setFitToWidth(true);
		upper.setLeft(label);
		upper.setRight(add);
		label.setText(labelText);
		
		BorderPane.setAlignment(label, Pos.CENTER_LEFT);
		VBox.setVgrow(scroll, Priority.ALWAYS);
		setPane(wrapper);
	}
	
	private void insertField(StringProperty property) {
		TextField field = new TextField();
		Button remove = new Button("X");
		HBox container = new HBox(7, field, remove);
		HBox.setHgrow(field, Priority.ALWAYS);
		
		field.textProperty().bindBidirectional(property);
		remove.setOnAction(e -> {
			fieldProperties.remove(property);
			fields.getChildren().remove(container);
		});
		
		fields.getChildren().add(container);
		field.requestFocus();
	}
	
	public List<StringProperty> getFieldProperties() {
		return fieldProperties;
	}
}
