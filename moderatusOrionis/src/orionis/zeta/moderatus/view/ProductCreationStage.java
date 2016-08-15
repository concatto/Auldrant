package orionis.zeta.moderatus.view;

import javafx.stage.Stage;
import orionis.zeta.moderatus.control.Controller;
import orionis.zeta.moderatus.model.Category;
import orionis.zeta.moderatus.web.database.DatabaseItem;

public class ProductCreationStage extends ProductStage {
	public ProductCreationStage(Controller controller, Stage owner) {
		super(owner);
		setTitle("Criação de Produto");
		setTerminalOperation(controller::createProduct);
	}

	@Override
	public void initialize(DatabaseItem item) {
		Category cat = (Category) item;
		getDataModel().reset();
		getDataModel().setCategory(cat.getCode());
		prepareAndShow();
	}
}
