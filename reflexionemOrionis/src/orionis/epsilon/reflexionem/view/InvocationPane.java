package orionis.epsilon.reflexionem.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class InvocationPane extends BorderPane {
	private Label pathLabel = new Label();
	private Label nameLabel = new Label();
	private HBox methodInformation = new HBox(2, pathLabel, nameLabel);
	private Label messageLabel = new Label();
	private VBox informationContainer = new VBox(20, methodInformation, messageLabel);
	private List<Object> parameterList = new ArrayList<>();
	private VBox parametersContainer = new VBox();
	private Object reference;
	private Label referenceLabel = new Label();
	private VBox constructionContainer = new VBox(5, referenceLabel, parametersContainer);
	private Label methodSignature = new Label();
	private Button invoke = new Button("Invocar");
	private VBox invokeContainer = new VBox(10, methodSignature, invoke);
	
	public InvocationPane() {
		setMinWidth(300);
		
		setTop(informationContainer);
		setCenter(constructionContainer);
		setBottom(invokeContainer);
		
		informationContainer.setAlignment(Pos.BOTTOM_CENTER);
		methodInformation.setAlignment(Pos.CENTER);
		constructionContainer.setAlignment(Pos.TOP_LEFT);
		invokeContainer.setAlignment(Pos.CENTER);
		referenceLabel.setFont(Font.font(24));
		nameLabel.setFont(Font.font(22));
		nameLabel.setPadding(new Insets(0, 0, 5, 0));
		constructionContainer.setPadding(new Insets(30, 6, 6, 10));
		
		referenceLabel.setOnDragOver(e -> e.acceptTransferModes(TransferMode.MOVE));
		referenceLabel.setOnDragDropped(e -> {
			InstanceBox instance = ((InstanceBox) e.getGestureSource());
			reference = instance.getInstance();
			referenceLabel.setText("Objeto: " + instance.getIdentifier());
			updateMethodSignature();
		});
	}
	
	public void setParameters(Class<?> object, List<Class<?>> parameters) {
		referenceLabel.setText("Objeto: " + (object == null ? "static" : object.getSimpleName()));
		parameterList.clear();
		parametersContainer.getChildren().clear();
		for (int i = 0; i < parameters.size(); i++) {
			parameterList.add(null);
			Label label = new Label(parameters.get(i).toString());
			label.setFont(Font.font(18));
			label.setOnDragOver(e -> e.acceptTransferModes(TransferMode.MOVE));
			int hack = i;
			label.setOnDragDropped(e -> {
				InstanceBox instance = ((InstanceBox) e.getGestureSource());
				parameterList.set(hack, instance.getInstance());
				label.setText(instance.getIdentifier());
				updateMethodSignature();
				e.setDropCompleted(true);
			});
			parametersContainer.getChildren().add(label);
		}
		
		updateMethodSignature();
	}
	
	private void updateMethodSignature() {
		String signature = referenceLabel.getText().substring(referenceLabel.getText().indexOf(" "));
		if (!signature.equals("static") && !signature.equals("nenhum")) {
			StringBuilder builder = new StringBuilder(signature);
			if (nameLabel.getText() != null) builder.append(".").append(nameLabel.getText());
			builder.append("(");
			boolean first = true;
			for (Node node : parametersContainer.getChildren()) {
				if (!first) builder.append(", ");
				builder.append(((Labeled) node).getText());
				first = false;
			}
			builder.append(");");
			signature = builder.toString();
		} else {
			signature = null;
		}
		methodSignature.setText(signature);
	}
	
	public void setMethod(String methodPath, String methodName) {
		pathLabel.setText(methodPath + ".");
		nameLabel.setText(methodName);
	}
	
	public void setMessage(String message) {
		messageLabel.setText(message);
	}
	
	public Button getInvoke() {
		return invoke;
	}

	public List<Object> getParameters() {
		return parameterList;
	}
	
	public Object getReference() {
		return reference;
	}
}
