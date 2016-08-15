package orionis.zeta.moderatus.view;

import javafx.stage.Stage;
import orionis.zeta.moderatus.control.Controller;
import orionis.zeta.moderatus.model.Product;
import orionis.zeta.moderatus.web.database.DatabaseItem;

public class ProductEditionStage extends ProductStage {
	private Product product;
	
	public ProductEditionStage(Controller controller, Stage owner) {
		super(owner);
		setTitle("Edição de Produto");
		setTerminalOperation(data -> {
			controller.editProduct(data, product);
		});
	}

	@Override
	public void initialize(DatabaseItem item) {
		this.product = (Product) item;
		getDataModel().placeData((Product) item);
		prepareAndShow();
	}
}
