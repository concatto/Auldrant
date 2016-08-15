package orionis.zeta.moderatus.view;

import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import orionis.zeta.moderatus.control.Controller;
import orionis.zeta.moderatus.model.Category;
import orionis.zeta.moderatus.model.Product;
import orionis.zeta.moderatus.web.database.DatabaseItem;

public class MainStage extends Stage {
	public static final String FIRST_ITEM = "";
	private Button addCategory = new Button("Adicionar");
	private Button add = new Button("Adicionar");
	private Button edit = new Button("Editar");
	private Button remove = new Button("Remover");
	private CategoryTable categories = new CategoryTable();
	private ProductTable products = new ProductTable();
	private StackPane addCategoryContainer = new StackPane(addCategory);
	private VBox categoriesContainer = new VBox(5, categories, addCategoryContainer);
	private VBox productsContainer = new VBox(5, products);
	private VBox topControlsContainer = new VBox(7, add, edit, remove);
	private Button refresh = new Button("Atualizar");
	private BorderPane controlsContainer = new BorderPane();
	private SplitPane split = new SplitPane();
	private HBox mainContent = new HBox(15, split, controlsContainer);
	private Menu file = new Menu("Arquivo");
	private Menu clean = new Menu("Limpeza");
	private Menu cleanImages = new Menu("Limpar Imagens");
	private MenuItem exit = new MenuItem("Fechar");
	private MenuItem cleanServerImages = new MenuItem("Do servidor");
	private MenuItem cleanDatabaseImages = new MenuItem("Do banco de dados");
	private MenuBar menu = new MenuBar();
	private VBox root = new VBox(menu, mainContent);
	private Scene scene = new Scene(root);
	private Controller controller;
	
	public MainStage(Controller controller) {
		this.controller = controller;
		configure();
	}
	
	private void configure() {
		setScene(scene);
		setTitle("moderatusOrionis");
		setMinHeight(300);
		setMinWidth(650);
		
		menu.getMenus().addAll(file, clean);
		file.getItems().add(exit);
		clean.getItems().add(cleanImages);
		cleanImages.getItems().addAll(cleanServerImages, cleanDatabaseImages);
		split.getItems().addAll(categoriesContainer, productsContainer);
		categories.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		products.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		controlsContainer.setTop(topControlsContainer);
		controlsContainer.setBottom(refresh);
		
		/* Style */
		scene.getStylesheets().add(MainStage.class.getResource("mainstyle.css").toExternalForm());
		split.getItems().forEach(t -> t.getStyleClass().add("listcontainer"));
		split.setDividerPosition(0, 0.32);
		controlsContainer.setId("controls");
		mainContent.setId("content");
		addCategoryContainer.getStyleClass().add("margintop");
		addCategory.prefWidthProperty().bind(((Region) addCategory.getParent()).widthProperty());
		VBox.setVgrow(categories, Priority.ALWAYS);
		VBox.setVgrow(products, Priority.ALWAYS);
		VBox.setVgrow(mainContent, Priority.ALWAYS);
		HBox.setHgrow(split, Priority.ALWAYS);
		
		/* Properties */
		ReadOnlyObjectProperty<Category> selectedCategory = categories.getSelectionModel().selectedItemProperty();
		ReadOnlyObjectProperty<Product> selectedProduct = products.getSelectionModel().selectedItemProperty();
		add.disableProperty().bind(selectedCategory.isNull());
		remove.disableProperty().bind(selectedProduct.isNull());
		edit.disableProperty().bind(selectedProduct.isNull());
		
		/* Listeners */
		add.setOnAction(e -> controller.initProductCreation(selectedCategory.get()));
		edit.setOnAction(e -> controller.initProductEdition(selectedProduct.get()));
		remove.setOnAction(e -> controller.removeProduct(selectedProduct.get()));
		addCategory.setOnAction(e -> controller.createCategory());
		
		exit.setOnAction(e -> controller.stop());
		cleanServerImages.setOnAction(e -> controller.executeServerCleaning());
		cleanDatabaseImages.setOnAction(e -> controller.executeDatabaseCleaning());
		
		refresh.setOnAction(e -> {
			String selection = selectedProduct.get() != null ? selectedProduct.get().getName() : null;
			controller.updateProducts(true, selection);
		});
		
		selectedCategory.addListener((obs, old, newValue) -> {
			if (newValue != null) {
				controller.updateProducts(false);
			}
		});
		
		categories.setRowFactory(view -> {
			TableRow<Category> row = new TableRow<>();
			MenuItem edit = new MenuItem("Renomear");
			MenuItem remove = new MenuItem("Remover");
			ContextMenu menu = new ContextMenu(edit, remove);
			edit.setOnAction(e -> controller.renameCategory(row.getItem()));
			remove.setOnAction(e -> controller.removeCategory(row.getItem()));
			row.setOnContextMenuRequested(e -> view.getSelectionModel().select(row.getItem()));
			row.itemProperty().addListener((obs, old, newValue) -> row.setContextMenu(newValue != null ? menu : null));
			return row;
		});
	}
	
	public void setCategories(List<Category> items) {
		categories.setItems(FXCollections.observableArrayList(items));
	}
	
	public void setProducts(List<Product> items) {
		products.setItems(FXCollections.observableArrayList(items));
		Platform.runLater(() -> products.getSelectionModel().clearSelection());
	}
	
	public List<Product> getCurrentProducts() {
		return products.getItems();
	}

	public List<Category> getCategories() {
		return categories.getItems();
	}
	
	public Category getSelectedCategory() {
		Category category = categories.getSelectionModel().getSelectedItem();
		return category == null ? categories.getItems().get(0) : category;
	}
	
	public void setSelectedCategory(String categoryName) {
		selectFirst(categoryName, categories);
	}
	
	public void setSelectedProduct(String productName) {
		selectFirst(productName, products);
	}
	
	public <T extends DatabaseItem> void selectFirst(String name, TableView<T> list) {
		Platform.runLater(() -> {
			if (name == null) {
				list.getSelectionModel().clearSelection();
			} else if (name.equals(FIRST_ITEM) || name.isEmpty()) {
				list.getSelectionModel().select(0);
			} else {
				int index = 0;
				for (T item : list.getItems()) {
					if (item.getName().equals(name)) {
						list.getSelectionModel().select(index);
						list.requestFocus();
						return;
					}
					index++;
				}
				list.getSelectionModel().select(0);
			}
		});
	}
}
