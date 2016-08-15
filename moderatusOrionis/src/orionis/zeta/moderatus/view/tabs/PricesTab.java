package orionis.zeta.moderatus.view.tabs;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import orionis.zeta.moderatus.model.BindingListener;
import orionis.zeta.moderatus.model.FormattedConverter;
import orionis.zeta.moderatus.view.NumberField;

public class PricesTab extends AbstractTab {
	private Label priceLabel = new Label("Preço");
	private TextField price = new NumberField("0,00", 2);
	private Label discountLabel = new Label("Desconto");
	private TextField discount = new NumberField("0,00", 2);
	private Label percentLabel = new Label("Percentual");
	private TextField percent = new NumberField("0", 2);
	private Label total = new Label("Total: R$ 0,00");
	private CheckBox check = new CheckBox("Incluir desconto");
	private VBox topLeft = new VBox(5, priceLabel, price, check);
	private HBox top = new HBox(15, topLeft, total);
	private VBox bottomLeft = new VBox(5, discountLabel, discount);
	private VBox bottomRight = new VBox(5, percentLabel, percent);
	private HBox bottom = new HBox(15, bottomLeft, bottomRight);
	private Separator separator = new Separator();
	private VBox content = new VBox(10, top, separator, bottom);
	
	public PricesTab() {
		super("Preços");
		setPane(content);
		
		top.setAlignment(Pos.CENTER_LEFT);
		separator.setPadding(new Insets(3, 0, 0, 0));
		bottom.getChildren().forEach(container -> {
			Pane pane = (Pane) container;
			HBox.setHgrow(pane, Priority.ALWAYS);
			pane.getChildren().forEach(element -> element.disableProperty().bind(check.selectedProperty().not()));
		});
		
		FormattedConverter converter = new FormattedConverter();
		
		discount.textProperty().addListener((obs, old, newValue) -> {
			if (!discount.isFocused()) {
				if (newValue.isEmpty()) discount.setText("0");
				if (!percent.isFocused()) {
					check.setSelected(converter.toDouble(newValue) > 0);
				}
			}
		});
		
		selectedProperty().addListener((obs, oldValue, newValue) -> topLeft.prefWidthProperty().bind(bottomLeft.widthProperty()));
		
		//Bidirectional binding between text and numbers
		TextField[] fields = {percent, price, discount};
		SimpleDoubleProperty[] properties = new SimpleDoubleProperty[3];
		for (int i = 0; i < fields.length; i++) {
			SimpleDoubleProperty property = new SimpleDoubleProperty(0);
			fields[i].textProperty().bindBidirectional(property, converter);
			properties[i] = property;
		}
		
		SimpleDoubleProperty hundred = new SimpleDoubleProperty(100);
		BindingListener discountTarget = new BindingListener(properties[2], properties[1], properties[0], hundred);
		BindingListener percentTarget = new BindingListener(properties[0], properties[2], hundred, properties[1]);
		
		for (TextField field : fields) {
			//If the percent field is changed, the discount field changes; else, the percent field changes.
			BindingListener listener = field == percent ? discountTarget : percentTarget;
			field.textProperty().addListener(listener);
			field.focusedProperty().addListener((obs, oldValue, newValue) -> {
				//If input is empty when focus is lost
				if (!newValue && field.getText().isEmpty()) field.setText("0");
			});
		}
		
		total.textProperty().bind(properties[1].subtract(properties[2]).asString("Total: R$ %.2f"));
	}
	
	public StringProperty getPriceProperty() {
		return price.textProperty();
	}
	
	public StringProperty getDiscountProperty() {
		return discount.textProperty();
	}
}
