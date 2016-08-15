package orionis.zeta.moderatus.view;

import java.io.IOException;
import java.net.ConnectException;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orionis.zeta.moderatus.model.Product;
import orionis.zeta.moderatus.model.ProductDataModel;
import orionis.zeta.moderatus.view.tabs.CharacteristicsTab;
import orionis.zeta.moderatus.view.tabs.ContentTab;
import orionis.zeta.moderatus.view.tabs.GeneralTab;
import orionis.zeta.moderatus.view.tabs.ImagesTab;
import orionis.zeta.moderatus.view.tabs.PricesTab;
import orionis.zeta.moderatus.view.tabs.VariableTab;
import orionis.zeta.moderatus.web.database.DatabaseItem;

public abstract class ProductStage extends Stage {
	private static final String NEW_PRODUCT = "Novo Produto";
	private ProductDataModel dataModel;
	private Label titleText = new Label(NEW_PRODUCT);
	private GeneralTab general = new GeneralTab();
	private PricesTab prices = new PricesTab();
	private ImagesTab images = new ImagesTab();
	private VariableTab characteristics = new CharacteristicsTab();
	private VariableTab content = new ContentTab();
	private TabPane tabPane = new TabPane();
	private Button finish = new Button("Concluir");
	private Button cancel = new Button("Cancelar");
	private HBox buttons = new HBox(7, finish, cancel);
	private VBox root = new VBox(titleText, tabPane, buttons);
	private Scene scene = new Scene(root);
	private ProgressStage progress = new ProgressStage();
	private Consumer<Product> terminalOperation;
	
	public ProductStage(Stage owner) {
		dataModel = new ProductDataModel(images.getImages(),
				general.getNameProperty(), general.getDescriptionProperty(), general.getStockProperty(), general.getWeightProperty(),
				prices.getPriceProperty(), prices.getDiscountProperty(),
				characteristics.getFieldProperties(), content.getFieldProperties()
		);
		
		scene.getStylesheets().add(getClass().getResource("editstyle.css").toExternalForm());
		tabPane.getTabs().addAll(general, prices, images, characteristics, content);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		titleText.setId("title");
		buttons.setId("buttons");
		root.setId("root");
		VBox.setVgrow(tabPane, Priority.ALWAYS);
		setScene(scene);
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		
		general.getNameProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				titleText.textProperty().unbind();
				titleText.setText(NEW_PRODUCT);
			} else {
				if (!titleText.textProperty().isBound()) {
					titleText.textProperty().bind(general.getNameProperty());
				}
			}
		});
		
		cancel.setOnAction(e -> close());
		finish.addEventHandler(ActionEvent.ACTION, e -> {
			Task<Product> task = new Task<Product>() {
				@Override
				protected Product call() throws Exception {
					Product product = null;
					product = dataModel.getData();
					ImageContainer[] filtered = dataModel.filterImageContainers(images.getImages());
					if (filtered.length > 0) {
						Platform.runLater(() -> progress.start("Realizando upload..."));
						dataModel.beginUpload(filtered);
					}
					return product;
				}
			};
			
			task.setOnSucceeded(evt -> {
				progress.finish();
				try {
					terminalOperation.accept(task.get());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			
			task.setOnFailed(evt -> {
				progress.finish();
				String message;
				Throwable exception = task.getException();
				if (exception instanceof ConnectException) {
					message = "Falha ao conectar com o servidor de upload.";
				} else if (exception instanceof IOException) {
					message = "Erro durante o upload do arquivo.";
				} else {
					message = "Falha na "
							+ (this instanceof ProductCreationStage ? "criação" : "edição")
							+ " do produto.";
				}
				Dialogs.showError(this, message, task.getException());
			});
			
			new Thread(task).start();
		});
	}
	
	public ProductDataModel getDataModel() {
		return dataModel;
	}
	
	public void setTerminalOperation(Consumer<Product> terminalOperation) {
		this.terminalOperation = terminalOperation;
	}
	
	public abstract void initialize(DatabaseItem item);
	
	protected void prepare() {
		tabPane.getSelectionModel().select(0);
		images.updateSelection(null);
	}
	
	public void prepareAndShow() {
		prepare();
		show();
	}
}
