package orionis.epsilon.reflexionem.view;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

public class InstancePane extends ScrollPane {
	private TilePane content = new TilePane();
	private ObservableMap<String, Object> instances = FXCollections.observableHashMap();
	
	public InstancePane() {
		instances.addListener((MapChangeListener<String, Object>) change -> {
			if (change.wasAdded()) {
				content.getChildren().add(new InstanceBox(change.getKey(), change.getValueAdded()));
			} else {
				
			}
		});
		
		setContent(content);
		setFitToWidth(true);
		setMinHeight(USE_PREF_SIZE);
		setMaxHeight(USE_PREF_SIZE);
		setPrefHeight(100);
		getStylesheets().add(getClass().getResource("instancestyle.css").toExternalForm());
		
		setDisabled(true);
	}
	
	public void addInstance(String identifier, Object instance) {
		instances.put(identifier, instance);
	}
	
	public Object getInstance(String identifier) {
		return instances.get(identifier);
	}
	
	public ObservableMap<String, Object> getInstances() {
		return instances;
	}
}
