package orionis.zeta.moderatus.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import orionis.zeta.moderatus.model.Category;

public class CategoryTable extends TableView<Category> {
	private TableColumn<Category, String> category = new TableColumn<>("Categoria");
	
	public CategoryTable() {
		category.setCellValueFactory(value -> new SimpleStringProperty(value.getValue().getName()));
		
		getColumns().add(category);
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
}
