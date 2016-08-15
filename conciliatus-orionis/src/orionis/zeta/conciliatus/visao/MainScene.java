package orionis.zeta.conciliatus.visao;

import java.util.List;

import orionis.zeta.conciliatus.controlador.Controller;
import orionis.zeta.conciliatus.modelo.Inscrito;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainScene extends Scene {
	private static final double UPPER_CONTAINER_HEIGHT = 35;
	private static final double MININUM_COLUMN_WIDTH = 100;
	private static final double INITIAL_HEIGHT = 500;
	private static final double INITIAL_WIDTH = 500;
	private static final double SYNC_WIDTH = 150;
	private static final double SYNC_HEIGHT = SYNC_WIDTH / 1.25;
	private static final double TILE_WIDTH = INITIAL_WIDTH / 2.5;
	
	private Controller controller = Controller.getInstance();
	private Text user = new Text("Usuário: ");
	private Label userValue = new Label();
	private HBox userContainer = new HBox(user, userValue);
	private Text status = new Text("Status: ");
	private Label statusValue = new Label();
	private HBox statusContainer = new HBox(status, statusValue);
	private FlowPane infoContainer = new FlowPane(Orientation.VERTICAL, userContainer, statusContainer);
	private Button connect = new Button("Conectar");
	private Button synchronize = new Button("Sincronizar!");
	private Separator separator = new Separator(Orientation.VERTICAL);
	private TilePane upperContainer = new TilePane(infoContainer, connect);
	private StackPane stackedUpperContainer = new StackPane(upperContainer, separator);
	private HBox synchronizeContainer = new HBox(15, synchronize);
	private TableView<Inscrito> table = new TableView<>();
	private VBox tableContainer = new VBox(table);
	private VBox root;
	
	public MainScene() {
		this(new VBox(30));
	}
	
	private MainScene(VBox root) {
		super(root, INITIAL_WIDTH, INITIAL_HEIGHT);
		this.root = root;
		
		configure();
	}

	@SuppressWarnings("unchecked")
	private void configure() {
		TableColumn<Inscrito, String> nameColumn = new TableColumn<Inscrito, String>("Nome");
		TableColumn<Inscrito, String> emailColumn = new TableColumn<Inscrito, String>("E-mail");
		nameColumn.setCellValueFactory(param -> param.getValue().getPropriedadeNome());
		emailColumn.setCellValueFactory(param -> param.getValue().getPropriedadeEmail());
		nameColumn.setMinWidth(MININUM_COLUMN_WIDTH);
		emailColumn.setMinWidth(MININUM_COLUMN_WIDTH);
		
		table.getColumns().setAll(nameColumn, emailColumn);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		user.setStyle("-fx-font-weight: bold");
		status.setStyle("-fx-font-weight: bold");
		
		root.setAlignment(Pos.CENTER);
		tableContainer.setAlignment(Pos.CENTER);
		upperContainer.setAlignment(Pos.CENTER);
		synchronizeContainer.setAlignment(Pos.CENTER);
		infoContainer.setAlignment(Pos.CENTER_LEFT);
		
		separator.visibleProperty().bind(widthProperty().greaterThanOrEqualTo(INITIAL_WIDTH));
		upperContainer.setPrefTileWidth(TILE_WIDTH);
		synchronize.setPrefWidth(SYNC_WIDTH);
		synchronize.setPrefHeight(SYNC_HEIGHT);
		infoContainer.setPrefHeight(UPPER_CONTAINER_HEIGHT);
		
		infoContainer.setPadding(new Insets(0, 0, 0, 20));
		root.setPadding(new Insets(20, 50, 30, 50));
		
		setUser(User.NONE);
		setStatus(Status.DISCONNECTED);
		connect.setOnAction(e -> controller.showContactsLogin());
		synchronize.setOnAction(e -> controller.startSynchronization());
		
		root.getChildren().addAll(stackedUpperContainer, tableContainer, synchronizeContainer);
	}
	
	public void setTableData(List<Inscrito> tableData) {
		table.setItems(FXCollections.observableArrayList(tableData));
	}
	
	public List<Inscrito> getTableData() {
		return table.getItems();
	}
	
	public void setUser(User user) {
		userValue.setText(user.getValue());
	}
	
	public void setStatus(Status status) {
		statusValue.setTextFill(status.getColor());
		statusValue.setText(status.getValue());
		
		connect.setDisable(!status.equals(Status.DISCONNECTED));
		synchronize.setDisable(!status.equals(Status.CONNECTED));
	}
	
	public void focusConnect() {
		connect.requestFocus();
	}
}
