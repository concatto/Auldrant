package orionis.zeta.moderatus.view.tabs;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import orionis.zeta.moderatus.view.NumberField;

public class GeneralTab extends AbstractTab {
	private static final double SMALL_FIELD = 70;
	private Label nameLabel = new Label("Nome");
	private TextField name = new TextField();
	private VBox nameContainer = new VBox(5, nameLabel, name);
	private NumberField stock = new NumberField();
	private Label stockLabel = new Label("Estoque");
	private VBox stockContainer = new VBox(5, stockLabel, stock);
	private NumberField weight = new NumberField();
	private VBox weightContainer = new VBox(5, new Label("Peso"), weight);
	private HBox topContent = new HBox(10, nameContainer, stockContainer, weightContainer);
	private Label descriptionLabel = new Label("Descrição");
	private TextArea description = new TextArea();
	private VBox content = new VBox(5, topContent, descriptionLabel, description);
	
	public GeneralTab() {
		super("Geral");
		stock.setPrefWidth(SMALL_FIELD);
		weight.setPrefWidth(SMALL_FIELD);
		setPane(content);
		HBox.setHgrow(nameContainer, Priority.ALWAYS);
	}
	
	public StringProperty getNameProperty() {
		return name.textProperty();
	}
	
	public StringProperty getDescriptionProperty() {
		return description.textProperty();
	}
	
	public StringProperty getStockProperty() {
		return stock.textProperty();
	}
	
	public StringProperty getWeightProperty() {
		return weight.textProperty();
	}
}