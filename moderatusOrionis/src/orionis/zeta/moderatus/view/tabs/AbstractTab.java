package orionis.zeta.moderatus.view.tabs;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public abstract class AbstractTab extends Tab {
	private StackPane root = new StackPane();
	
	public AbstractTab(String title) {
		super(title);
		setContent(root);
	}
	
	public StackPane getRoot() {
		return root;
	}
	
	protected void setPane(Pane pane) {
		ObservableList<Node> children = root.getChildren();
		pane.setPadding(new Insets(6, 12, 12, 12));
		
		if (children.isEmpty()) {
			children.add(pane);
		} else {
			children.set(0, pane);
		}
	}
}
