package orionis.zeta.moderatus.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.TextAlignment;
import orionis.zeta.moderatus.model.Product;

public class ProductTable extends TableView<Product> {
	private TableColumn<Product, String> name = new TableColumn<>("Nome");
	private TableColumn<Product, String> stock = new TableColumn<>("Estoque");
	
	public ProductTable() {
		name.setCellValueFactory(value -> new SimpleStringProperty(value.getValue().getName()));
		stock.setCellValueFactory(value -> new SimpleStringProperty(value.getValue().getStock()));
		stock.setCellFactory(column -> new ProductCell());
		
		getColumns().add(name);
		getColumns().add(stock);
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		Label placeholder = new Label("Esta categoria não possui nenhum produto.\n"
				+ "Utilize o botão \"Adicionar\" para criar um produto.");
		
		setPlaceholder(placeholder);
		placeholder.setTextAlignment(TextAlignment.CENTER);
	}
	
	private class ProductCell extends TableCell<Product, String> {
		public ProductCell() {
			setAlignment(Pos.CENTER_RIGHT);
		}
		
		@Override
		protected void updateItem(String item, boolean empty) {
			setText(empty ? null : item);
		}
	}
}
