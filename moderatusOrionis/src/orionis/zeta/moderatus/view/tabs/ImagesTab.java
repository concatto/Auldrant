package orionis.zeta.moderatus.view.tabs;

import java.io.File;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import orionis.zeta.moderatus.view.ImageContainer;

public class ImagesTab extends AbstractTab {
	private ImageContainer mainImage = new ImageContainer(this);
	private Button insert = new Button("Inserir");
	private Button change = new Button("Alterar");
	private Button remove = new Button("Remover");
	private HBox buttonContainer = new HBox(5, insert);
	private BorderPane top = new BorderPane();
	private Separator separator = new Separator();
	private ImageContainer extraImage1 = new ImageContainer(this);
	private ImageContainer extraImage2 = new ImageContainer(this);
	private ImageContainer extraImage3 = new ImageContainer(this);
	private ImageContainer extraImage4 = new ImageContainer(this);
	private HBox bottom = new HBox(14, extraImage1, extraImage2, extraImage3, extraImage4);
	private FileChooser chooser = new FileChooser();
	private VBox content = new VBox(10, top, separator, bottom);
	private ImageContainer focusedImage = mainImage;
	private ImageContainer[] images;
	
	public ImagesTab() {
		super("Imagens");
		chooser.getExtensionFilters().add(new ExtensionFilter("Imagens", "*.png", "*.jpg", "*.bmp", "*.gif"));
		separator.setPadding(new Insets(3, 0, 0, 0));
		top.setPadding(new Insets(8, 0, 0, 0));
		top.setLeft(mainImage);
		top.setRight(buttonContainer);
		setPane(content);
		updateSelection(mainImage);
		
		Stream<ImageContainer> extras = bottom.getChildren().stream().map(ImageContainer.class::cast);
		images = Stream.concat(Stream.of(mainImage), extras).toArray(ImageContainer[]::new);
		
		EventHandler<ActionEvent> chooseListener = e -> {
			File file = chooser.showOpenDialog(getTabPane().getScene().getWindow());
			if (file == null) return;
			focusedImage.setImageFromURL(file.toURI().toString());
		};
		
		insert.setOnAction(chooseListener);
		change.setOnAction(chooseListener);
		remove.setOnAction(e -> {
			focusedImage.nullify();
		});
	}
	
	/**
	 * Selects an ImageContainer. If null, the main container will be selected.
	 * @param newSelection the container to be selected
	 */
	public void updateSelection(ImageContainer newSelection) {
		if (newSelection == null) newSelection = mainImage;
		
		focusedImage.setSelected(false);
		newSelection.setSelected(true);
		switchButtons(newSelection);
		focusedImage = newSelection;
	}
	
	public void switchButtons(ImageContainer newSelection) {
		buttonContainer.getChildren().clear();
		if (newSelection.hasImage()) {
			buttonContainer.getChildren().add(change);
			if (newSelection != mainImage) buttonContainer.getChildren().add(remove);
		} else {
			buttonContainer.getChildren().add(insert);
		}
	}
	
	public void switchButtonStatus(boolean enabled) {
		Button[] buttons = {insert, change, remove};
		for (Button button : buttons) {
			button.setDisable(!enabled);
		}
	}
	
	public ImageContainer[] getImages() {
		return images;
	}
}
