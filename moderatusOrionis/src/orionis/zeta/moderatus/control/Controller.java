package orionis.zeta.moderatus.control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import orionis.zeta.moderatus.model.Attribute;
import orionis.zeta.moderatus.model.Category;
import orionis.zeta.moderatus.model.Product;
import orionis.zeta.moderatus.model.Triplet;
import orionis.zeta.moderatus.view.Dialogs;
import orionis.zeta.moderatus.view.LoginStage;
import orionis.zeta.moderatus.view.MainStage;
import orionis.zeta.moderatus.view.ProductCreationStage;
import orionis.zeta.moderatus.view.ProductEditionStage;
import orionis.zeta.moderatus.web.database.AttributeHeader;
import orionis.zeta.moderatus.web.database.Database;
import orionis.zeta.moderatus.web.database.DatabaseTask;
import orionis.zeta.moderatus.web.database.ProductKey;

public class Controller extends Application {
	private TaskHandler handler;
	private ProductEditionStage edition;
	private ProductCreationStage creation;
	private MainStage mainStage;
	private Database database;
	
	@Override
	public void start(Stage primaryStage) {
		LoginStage login = new LoginStage();
		login.show();
		login.setAction(password -> database = new Database(password));
		login.setOnSuccess(this::begin);
		login.setOnCancel(this::stop);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void begin() {
		mainStage = new MainStage(this);
		creation = new ProductCreationStage(this, mainStage);
		edition = new ProductEditionStage(this, mainStage);
		handler = new TaskHandler(database, mainStage);
		mainStage.show();
		mainStage.setOnCloseRequest(e -> handler.terminate());
		
		updateCategories(MainStage.FIRST_ITEM);
	}
	
	private void updateAllProducts(List<Category> categories) throws SQLException {
		List<Product> products = parseProductData(database.queryProducts(), database.queryCharacteristics(), database.queryContents());
		
		for (Category category : categories) {
			String code = category.getCode();
			List<Product> filtered = products.stream().filter(t -> t.getCategory().equals(code)).collect(Collectors.toList());
			category.setProducts(filtered);
		}
	}
	
	private List<Product> parseProductData(List<Map<ProductKey, String>> data, List<Triplet> rawCharacteristics, List<Triplet> rawContents) {
		return data.stream().map(map -> {
			List<Attribute> characteristics = parseAttributeData(rawCharacteristics, map.get(ProductKey.CODE));
			List<Attribute> contents = parseAttributeData(rawContents, map.get(ProductKey.CODE));
			return new Product(map, characteristics, contents);
		}).collect(Collectors.toList());
	}
	
	/**
	 * Converts Triplets to Attributes, keeping only Triplets whose second values
	 * are the same as <i>relativeCode</i>.
	 * @param data the Triplets to be converted
	 * @param relativeCode the value to be compared
	 * @return a List of Attributes.
	 */
	private List<Attribute> parseAttributeData(List<Triplet> data, String relativeCode) {
		return data.stream()
				.filter(c -> c.getSecond().equals(relativeCode))
				.map(c -> new Attribute(c.getFirst(), c.getThird()))
				.collect(Collectors.toList());
	}
	
	public void updateCategories(String newSelection) {
		handler.createUpdate("Obtendo dados...", () -> {
			List<Category> categories = database.queryCategories().stream().map(Category::new).collect(Collectors.toList());
			updateAllProducts(categories);
			return categories;
		}, result -> {
			mainStage.setCategories(result);
			mainStage.setSelectedCategory(newSelection);
		});
	}
	
	public void updateProducts(boolean force) {
		updateProducts(force, null);
	}
	
	public void updateProducts(boolean force, String newSelection) {
		Category category = mainStage.getSelectedCategory();
		
		if (category.getProducts() == null || force) {
			handler.createUpdate("Atualizando produtos...", () -> {
				return parseProductData(database.queryProducts(category.getCode()), database.queryCharacteristics(), database.queryContents());
			}, result -> {
				category.setProducts(result);
				mainStage.setProducts(category.getProducts());
				mainStage.setSelectedProduct(newSelection);
			});
		} else {
			mainStage.setProducts(category.getProducts());	
		}
	}

	public void createCategory() {
		String name = Dialogs.showInput(mainStage, "Digite um nome para a nova categoria:");
		if (name == null) return;
		
		handler.createDatabaseTask("Criando categoria...", () -> {
			database.executeCategoryCreation(name);
		}, () -> {
			Dialogs.showMessage(mainStage, String.format("Categoria %s criada com sucesso.", name));
			updateCategories(name);
		}, "Erro na inserção das informações no Banco de Dados.");
	}
	
	public void renameCategory(Category category) {
		String newName = Dialogs.showInput(mainStage, "Digite um novo nome para a categoria " + category.getName() + ":");
		if (newName == null) return;
		
		 handler.createDatabaseTask("Renomeando categoria...", () -> {
			database.executeCategoryRename(newName, category.getCode());
		}, () -> {
			Dialogs.showMessage(mainStage, "Categoria renomeada com sucesso.");
			updateCategories(newName);
		}, "Erro na inserção das informações no Banco de Dados.");
	}
	
	public void removeCategory(Category category) {
		boolean option = Dialogs.showOption(mainStage, "Deseja remover a categoria " + category.getName() + "? Todos os produtos desta categoria também serão removidos.");
		if (option == false) return;
		
		handler.createDatabaseTask("Removendo categoria...", () -> {
			database.executeCategoryRemoval(category.getCode());
		}, () -> {
			Dialogs.showMessage(mainStage, String.format("Categoria %s e seus produtos removidos com sucesso.", category.getName()));
			updateCategories(MainStage.FIRST_ITEM);
		}, "Erro na remoção das informações do Banco de Dados.");
	}

	public void createProduct(Product newProduct) {
		handler.createDatabaseTask("Criando produto...", () -> {
			database.executeProductCreation(newProduct.getData());
		}, () -> {
			Dialogs.showMessage(creation, String.format("Produto %s criado com sucesso.", newProduct.getName()));
			creation.close();
			updateProducts(true, newProduct.getName());
		}, creation, "Erro na inserção das informações no Banco de Dados.");
	}
	
	public void editProduct(Product newProduct, Product oldProduct) {
		Map<ProductKey, String> difference = new HashMap<>();
		Map<ProductKey, String> newData = newProduct.getData();
		Map<ProductKey, String> oldData = oldProduct.getData();
		
		newData.keySet().forEach(t -> {
			String value = newData.get(t);
			if (!value.equals(oldData.get(t))) {
				difference.put(t, value);
			}
		});
		
		AttributeDifferences[] diffs = {
					AttributeDifferences.process(AttributeHeader.CHARACTERISTIC,
							newProduct.getCharacteristics(),
							oldProduct.getCharacteristics()),
					AttributeDifferences.process(AttributeHeader.CONTENT,
							newProduct.getContents(),
							oldProduct.getContents())
		};
		
		List<DatabaseTask> tasks = new ArrayList<>();
		
		for (AttributeDifferences diff : diffs) {
			AttributeHeader header = diff.getHeader();
			if (diff.hasChanged()) tasks.add(() -> database.executeAttributeEdition(diff.getChangedAttributesAsMap(), header));
			if (diff.hasAdded()) tasks.add(() -> database.executeAttributeCreation(diff.getAddedValues(), oldProduct.getCode(), header));
			if (diff.hasRemoved()) tasks.add(() -> database.executeAttributeRemoval(diff.getRemovedCodes(), header));
		}
		
		if (difference.size() > 0) tasks.add(() -> database.executeProductEdition(difference, oldProduct.getCode()));
		
		if (tasks.isEmpty()) {
			Dialogs.showMessage(edition, "Nenhuma edição realizada.");
			return;
		}
		
		handler.createDatabaseTask("Editando produto...", tasks, () -> {
			Dialogs.showMessage(edition, "Edição realizada com sucesso.");
			edition.close();
			updateProducts(true, newProduct.getName());
		}, edition, "Erro na inserção das informações no Banco de Dados.");
	}
	
	public void removeProduct(Product product) {
		boolean option = Dialogs.showOption(mainStage, String.format("Deseja remover o produto %s?", product.getName()));
		if (!option) return;
		
		handler.createDatabaseTask("Removendo produto...", () -> {
			database.executeProductRemoval(product.getCode());
		}, () -> {
			Dialogs.showMessage(mainStage, String.format("Produto %s removido com sucesso.", product.getName()));
			updateProducts(true);
		}, "Erro na remoção das informações do Banco de Dados.");
	}
	
	public void initProductCreation(Category category) {
		creation.initialize(category);
	}
	
	public void initProductEdition(Product product) {
		edition.initialize(product);
	}
	
	public void executeServerCleaning() {
		handler.cleanServer();
	}
	
	public void executeDatabaseCleaning() {
		handler.cleanDatabase();
	}

//	public void showConnectionInformation() {
//		try {
//			DatabaseMetaData metadata = database.getMetaData();
//			String info = String.format("Endereço: %s\nVersão: %s\n", metadata.getURL(), metadata.getDatabaseProductVersion());
//			Dialogs.showMessage(mainStage, info);
//		} catch (SQLException e) {
//			Dialogs.showError(mainStage, "Um erro ocorreu ao acessar o Banco de Dados.", e);
//		}
//	}
	
	@Override
	public void stop() {
		if (mainStage != null && mainStage.isShowing()) mainStage.close();
		if (handler != null) handler.terminate();
		Platform.exit();
	}
}
