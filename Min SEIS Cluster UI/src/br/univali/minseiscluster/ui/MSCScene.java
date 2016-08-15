package br.univali.minseiscluster.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.ThreadingModel;

import br.univali.minseiscluster.model.Heuristic;
import br.univali.minseiscluster.model.HeuristicSnapshot;
import br.univali.minseiscluster.parser.HeuristicParser;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MSCScene extends Scene {
	private static final StringProperty RECALCULATE = new SimpleStringProperty("Calcular posições");
	private static final StringProperty STOP = new SimpleStringProperty("Parar");
	private BooleanProperty stable = new SimpleBooleanProperty(true);
	
	private Label timeLabel = new Label();
	private Button layoutButton = new Button();
	private Button runButton = new Button("Executar heurística");
	private HBox root;
	private VBox mainPane;
	private StackPane graphPane;
	private Viewer viewer;
	
	private Object pauseMonitor = new Object();
	private volatile BooleanProperty paused = new SimpleBooleanProperty(false);
	private volatile boolean layoutRunning = false;
	private Layout layout;
	private Graph graph;
	private Camera camera;
	private ReentrantLock lock = new ReentrantLock();
	private Heuristic heuristic;
	private List<CommunityPolygon> polygons;
	private SwingNode graphNode;
	private ViewPanel graphPanel;
	private Button saveButton;
	private FileChooser chooser = new FileChooser();
	private ListView<HeuristicSnapshot> snapList;
	private ReadOnlyIntegerProperty selectedIndex;
	private Button pauseButton;

	public MSCScene() {
		super(new HBox(5));
		try {
//			File file = chooser.showOpenDialog(null);
			File file = new File("C:/Users/Fernando/Downloads/magia.cgc");
			this.heuristic = HeuristicParser.parse(file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.graph = heuristic.getGraph();
		
		root = (HBox) getRoot();
		root.setPadding(new Insets(5));
		
		layout = new SpringBox();		
		viewer = new Viewer(graph, ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		
		graphPanel = viewer.addDefaultView(false);
		camera = graphPanel.getCamera();
		
		CommunityPolygon.prepareTransformer(node -> camera.transformGuToPx(node.x, node.y, node.z));
		graphNode = new SwingNode();
		graphNode.setContent(graphPanel);
		graphNode.setStyle("-fx-background-color: transparent");
		
		pauseButton = new Button();
		pauseButton.textProperty().bind(Bindings.when(paused).then("Continuar").otherwise("Pausar"));
		
		graphPane = new StackPane(graphNode);
		graphPane.setAlignment(Pos.TOP_LEFT);
	
		mainPane = new VBox(5);
		mainPane.getChildren().addAll(new HBox(5, layoutButton, runButton, pauseButton, timeLabel), graphPane, new ControlBar());
		
		snapList = new ListView<>(FXCollections.observableArrayList(heuristic.getSnapshots()));
		snapList.setMinWidth(120);
		snapList.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
			n.restoreTo(graph);
		});
		
		selectedIndex = snapList.getSelectionModel().selectedIndexProperty();
		
		saveButton = new Button("Salvar");
		saveButton.setMaxWidth(Double.MAX_VALUE);
		
		VBox snapPane = new VBox(5, saveButton, snapList);
		root.getChildren().addAll(mainPane, new Separator(Orientation.VERTICAL), snapPane);
		
		HBox.setHgrow(mainPane, Priority.ALWAYS);
		VBox.setVgrow(graphPane, Priority.ALWAYS);
		VBox.setVgrow(snapList, Priority.ALWAYS);
		
		polygons = heuristic.getCommunities().stream()
				.map(this::toGraphicNodes)
				.map(CommunityPolygon::new)
				.collect(Collectors.toList());
		
		for (int i = 0; i < polygons.size(); i++) {
			Color c = Color.hsb((360f / polygons.size()) * i, 1, 1);
			
			Polygon polygon = polygons.get(i);
			polygon.setStroke(c.deriveColor(1, 1, 1, 0.3));
			polygon.setFill(c.deriveColor(1, 1, 1, 0.1));
		}
		
		graphPane.getChildren().addAll(polygons);
		graph.addSink(layout);
		
		addListeners();
	}
	
	public List<GraphicNode> toGraphicNodes(List<String> ids) {
		return ids.stream()
				.map(id -> (GraphicNode) viewer.getGraphicGraph().getNode(id))
				.collect(Collectors.toList());
	}
	
	private void addListeners() {
		layoutButton.setOnAction(e -> {
			if (stable.get()) {
				recalculateLayout();
			} else {
				stopLayout();
			}
		});
		
		pauseButton.setOnAction(e -> {
			if (paused.get()) {
				paused.set(false);
				synchronized (pauseMonitor) {
					pauseMonitor.notify();
				}	
			} else {
				paused.set(true);
			}
		});
		
		runButton.setOnAction(e -> runHeuristic(300));
		layoutButton.textProperty().bind(Bindings.when(stable).then(RECALCULATE).otherwise(STOP));
		saveButton.setOnAction(e -> saveSnapshot());
	}
	
	private void saveSnapshot() {
		WritableImage snapshot = graphPane.snapshot(null, null);
		
		chooser.setInitialFileName("snapshot.png");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("Imagem .png", "*.png"));
		File file = chooser.showSaveDialog(getWindow());
		
		String text;
		AlertType type;
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
			text = "Imagem salva com sucesso!";
			type = AlertType.INFORMATION;
		} catch (IOException e) {
			e.printStackTrace();
			text = "Falha ao salvar imagem. (" + e.getClass().getSimpleName() + ")";
			type = AlertType.ERROR;
		}
		
		Alert alert = new Alert(type, text, ButtonType.OK);
		alert.setHeaderText(null);
		alert.setTitle("Mensagem");
		alert.show();
	}
	
	private void runHeuristic(int delay) {		
		new Thread(() -> {
			for (int i = selectedIndex.get(); i < snapList.getItems().size(); i++) {
				snapList.getSelectionModel().clearAndSelect(i);
				snapList.getFocusModel().focus(i);
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		snapList.requestFocus();
	}

	private void verifyPause() {
		if (paused.get()) {
			synchronized (pauseMonitor) {
				try {
					pauseMonitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void stopLayout() {
		layoutRunning = false;
	}

	private void recalculateLayout() {		
		new Thread(() -> {
			Platform.runLater(() -> polygons.forEach(CommunityPolygon::hide));
			
			lock.lock();
			Platform.runLater(() -> stable.set(false));
			
			layout.setStabilizationLimit(0.6f);
			layoutRunning = true;
			viewer.enableAutoLayout(layout);
			
			while (layoutRunning && layout.getStabilization() <= layout.getStabilizationLimit());
			
			Platform.runLater(() -> stable.set(true));
			layoutRunning = false;
			viewer.disableAutoLayout();
			layout.clear();
			
			lock.unlock();
			
			Platform.runLater(() -> polygons.forEach(CommunityPolygon::recalculateAndShow));
		}).start();
	}

	public void finish() {
		viewer.close();
	}
}
