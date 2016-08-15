package orionis.epsilon.reflexionem.view;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainStage extends Stage {
	private InstancePane instances = new InstancePane();
	private TextField classField = new TextField();
	private Button findClass = new Button("Procurar");
	private HBox classFieldContainer = new HBox(8, classField, findClass);
	private ListView<Class<?>> classList = new ListView<>();
	private Button instantiate = new Button("Instanciar");
	private VBox classContainer = new VBox(10, classFieldContainer, classList, instantiate);
	private Tab classesTab = new Tab("Classes");
	private ListView<Class<?>> primitiveList = new ListView<>();
	private TextField primitiveField = new TextField();
	private Button create = new Button("Criar");
	private HBox primitiveFieldContainer = new HBox(8, primitiveField, create);
	private VBox primitiveContainer = new VBox(10, primitiveList, primitiveFieldContainer);
	private Tab primitivesTab = new Tab("Primitivos");
	private TabPane tabs = new TabPane();
	private ListView<Method> methodList = new ListView<>();
	private VBox methodContainer = new VBox(methodList);
	private Button invoke = new Button("Invocar");
	private InvocationPane invocation = new InvocationPane();
	private SplitPane split = new SplitPane();
	private VBox root = new VBox(15, instances, split);
	private Scene scene = new Scene(root);
	
	public MainStage() {
		setTitle("reflexionemOrionis");
		setScene(scene);
		
		tabs.getTabs().addAll(classesTab, primitivesTab);
		classesTab.setContent(classContainer);
		primitivesTab.setContent(primitiveContainer);
		split.getItems().addAll(tabs, methodContainer, invocation);
		
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		Insets ten = new Insets(10);
		Insets bigButton = new Insets(8, 30, 8, 30);
		classContainer.setPadding(ten);
		primitiveContainer.setPadding(ten);
		methodContainer.setPadding(ten);
		invocation.setPadding(ten);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.CENTER);
		classContainer.setAlignment(Pos.CENTER);
		tabs.setMinWidth(300);
		methodContainer.setMinWidth(300);
		instantiate.setPadding(bigButton);
		invoke.setPadding(bigButton);
		VBox.setVgrow(classList, Priority.ALWAYS);
		VBox.setVgrow(primitiveList, Priority.ALWAYS);
		VBox.setVgrow(methodList, Priority.ALWAYS);
		HBox.setHgrow(classField, Priority.ALWAYS);
		HBox.setHgrow(primitiveField, Priority.ALWAYS);
	}
	
	public void setOnSearchClass(Consumer<String> function) {
		findClass.setOnAction(e -> {
			classList.getSelectionModel().clearSelection();
			function.accept(classField.getText());
		});
	}
	
	public void setClasses(List<Class<?>> classes) {
		classList.setItems(FXCollections.observableArrayList(classes));
	}
	
	public void setOnClassSelection(Consumer<Class<?>> function) {
		classList.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> function.accept(newValue));
	}
	
	public void setMethods(List<Method> methods) {
		methodList.setItems(FXCollections.observableArrayList(methods));
	}
	
	public void setOnMethodSelection(Consumer<Method> function) {
		methodList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			function.accept(newValue);
		});
	}
	
	public void setInvocationMessage(String message) {
		invocation.setMessage(message);
	}
	
	public void setInvocationParameters(Class<?> object, List<Class<?>> parameters) {
		invocation.setParameters(object, parameters);
	}
	
	public void setOnInvocation(BiConsumer<Method, List<Object>> function) {
		invocation.getInvoke().setOnAction(e -> function.accept(methodList.getSelectionModel().getSelectedItem(), invocation.getParameters()));
	}
	
	public void setPrimitives(List<Class<?>> primitives) {
		primitiveList.setItems(FXCollections.observableArrayList(primitives));
	}
	
	public void setOnPrimitiveCreation(BiConsumer<Class<?>, String> function) {
		create.setOnAction(e -> function.accept(primitiveList.getSelectionModel().getSelectedItem(), primitiveField.getText()));
	}
	
	public void addInstance(String identifier, Object instance) {
		instances.addInstance(identifier, instance);
	}

	public void setInvocationInformation(String declaringClass, String methodName) {
		invocation.setMethod(declaringClass, methodName);
	}
	
	public Object getReference() {
		return invocation.getReference();
	}
	
	public Map<String, Object> getInstances() {
		return instances.getInstances();
	}
	
	public void setOnInstantiation(Consumer<Class<?>> function) {
		instantiate.setOnAction(e -> function.accept(classList.getSelectionModel().getSelectedItem()));
	}
}
