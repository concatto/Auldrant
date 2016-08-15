package orionis.zeta.moderatus.view;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import orionis.zeta.moderatus.view.tabs.ImagesTab;

public class ImageContainer extends StackPane {
	private static final double VIEW_SIZE = 70;
	private static final Image IMAGE_EMPTY = new Image(ImageContainer.class.getResourceAsStream("/empty.png"));
	private static final Image IMAGE_BAD_CONNECTION = new Image(ImageContainer.class.getResourceAsStream("/connection.png"));
	private static final Border BORDER_SELECTED = new Border(new BorderStroke(Color.DODGERBLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(2), new Insets(-2)));
	private static final Border BORDER_SELECTED_ERROR = new Border(new BorderStroke(Color.CRIMSON, BorderStrokeStyle.SOLID, null, new BorderWidths(2), new Insets(-2)));
	private static final Border BORDER_NORMAL = new Border(new BorderStroke(Color.SILVER, BorderStrokeStyle.SOLID, null, new BorderWidths(1), new Insets(-1)));
	private static final Border BORDER_NORMAL_ERROR = new Border(new BorderStroke(Color.CRIMSON, BorderStrokeStyle.SOLID, null, new BorderWidths(1), new Insets(-1)));
	private ImageView view = new ImageView();
	private ImagesTab owner;
	private FadeTransition fade = new FadeTransition(Duration.millis(120), view);
	private StringProperty imageName = new SimpleStringProperty();
	private String path;
	private boolean selected;
	private boolean failed;
	private boolean changed;
	
	public ImageContainer(ImagesTab owner) {
		this.owner = owner;
		setPrefSize(VIEW_SIZE, VIEW_SIZE);
		view.setFitHeight(VIEW_SIZE);
		view.setFitWidth(VIEW_SIZE);
		view.setPreserveRatio(true);
		getChildren().add(view);
		reset();
		
		setOnMousePressed(e -> owner.updateSelection(this));
		
		setOnDragDetected(e -> {
			if (hasImage() && !isFailed()) {
				Dragboard board = startDragAndDrop(TransferMode.MOVE);
				ClipboardContent clip = new ClipboardContent();
				clip.putImage(view.getImage());
				clip.putString(getPath());
				board.setContent(clip);
				board.setDragView(view.getImage());
				e.consume();
			}
		});
		
		setOnDragOver(e -> {
			if (e.getDragboard().hasImage() && e.getDragboard().hasString()) {
				e.acceptTransferModes(TransferMode.MOVE);
			}
			e.consume();
		});
		
		setOnDragDropped(e -> {
			ImageContainer container = (ImageContainer) e.getGestureSource();
			container.setImage(view.getImage(), getPath());
			setImage(e.getDragboard().getImage(), e.getDragboard().getString());
			owner.updateSelection(this);
			e.setDropCompleted(true);
			e.consume();
		});
	}
	
	public void reset() {
		path = null;
		failed = false;
		changed = false;
		setSelected(false);
		imageName.set(null);
		view.setImage(IMAGE_EMPTY);
	}
	
	private void setImage(Image image, String path) {
		fade.setFromValue(1);
		fade.setToValue(0);
		fade.setOnFinished(e -> {
			fade.setFromValue(0);
			fade.setToValue(1);
			fade.setOnFinished(null);
			fade.playFromStart();
			view.setImage(image == null ? IMAGE_EMPTY : image);
			this.path = path;
			if (path != null) {
				imageName.set(image == IMAGE_EMPTY ? null : path.substring(path.lastIndexOf("/") + 1));
				changed = !isHttp(path);
			} else {
				imageName.set(null);
				changed = true;
				failed = false;
			}
			
			
			failed = image == IMAGE_BAD_CONNECTION;
			if (selected) owner.switchButtons(this);
			updateBorder();
		});
		
		fade.play();
	}
	
	public void setImageFromURL(String path) {
		ProgressIndicator downloading = new ProgressIndicator();
		downloading.setMaxSize(20, 20);
		Task<InputStream> task = new Task<InputStream>() {
			@Override
			protected InputStream call() throws Exception {
				InputStream stream = new URL(path).openConnection().getInputStream();
				return stream;
			}
		};
		
		task.setOnScheduled(e -> {
			getChildren().add(downloading);
			owner.switchButtonStatus(false);
			setImage(null, path);
		});
		
		task.setOnSucceeded(e -> {
			owner.switchButtonStatus(true);
			getChildren().remove(downloading);
			try {
				Image image = new Image(task.get());
				setImage(image, path);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		task.setOnFailed(e -> {
			owner.switchButtonStatus(true);
			getChildren().remove(downloading);
			if (task.getException() instanceof FileNotFoundException) {
				nullify();
			} else {
				setImage(IMAGE_BAD_CONNECTION, path);
			}
		});
		
		new Thread(task).start();
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateBorder();
	}
	
	private void updateBorder() {
		if (failed) {
			setBorder(selected ? BORDER_SELECTED_ERROR : BORDER_NORMAL_ERROR);
		} else {
			setBorder(selected ? BORDER_SELECTED : BORDER_NORMAL);
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public boolean hasImage() {
		return imageName.get() != null;
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getImageName() {
		return imageName.get();
	}
	
	public StringProperty imageNameProperty() {
		return imageName;
	}
	
	public boolean isFailed() {
		return failed;
	}
	
	public void nullify() {
		setImage(null, null);
	}
	
	private boolean isHttp(String url) {
		try {
			return new URL(url).getProtocol().equals("http");
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
