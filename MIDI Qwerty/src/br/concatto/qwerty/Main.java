package br.concatto.qwerty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Main extends Application {
	private static final int NOTE_WIDTH = 8;
	private SimpleBooleanProperty playing = new SimpleBooleanProperty(false);
	private ImageView stop = new ImageView(new Image(Main.class.getResourceAsStream("/ic_stop_black_24dp_1x.png")));
	private ImageView pause = new ImageView(new Image(Main.class.getResourceAsStream("/ic_pause_black_24dp_1x.png")));
	private ImageView play = new ImageView(new Image(Main.class.getResourceAsStream("/ic_play_arrow_black_24dp_1x.png")));
	
	private Sequencer sequencer;
	private volatile boolean alive = true;
	
	private static final char[] KEYS = {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
		'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
		'z', 'x', 'c', 'v', 'b', 'n'
	};
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File("E:/Synthesia/Custom/"));
		File file = chooser.showOpenDialog(primaryStage);
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(file);
			sequencer = MidiSystem.getSequencer();
			sequencer.setSequence(sequence);
		} catch (InvalidMidiDataException | IOException | MidiUnavailableException e) {
			e.printStackTrace();
			return;
		}
		
		float divisor = sequence.getResolution() / 32f;
		
		Track[] tracks = sequence.getTracks();
		List<NoteInformation> info = parseNotes(tracks, divisor);
		
		StackPane canvas = new StackPane();
		canvas.setAlignment(Pos.TOP_CENTER);
		canvas.setPrefSize(127 * NOTE_WIDTH, Math.ceil(sequence.getTickLength() / divisor));
		
		Line pointer = new Line(0, 0, canvas.getPrefWidth(), 0);
		pointer.setStroke(Color.DARKGRAY);
		pointer.setStrokeWidth(1.3);
		
		ScrollPane scrollPane = new ScrollPane(canvas);
		scrollPane.setPrefViewportWidth(canvas.getPrefWidth() + 2);
		scrollPane.setPrefViewportHeight(800);
		
		class NoteRectangle extends StackPane {
			public NoteInformation info;
			
			public NoteRectangle(NoteInformation info) {
				super();
				setAlignment(Pos.TOP_LEFT);
				Rectangle rect = new Rectangle(info.getWidth(), info.getHeight());
				rect.setFill(Color.SKYBLUE);
				setTranslateX(info.getX());
				setTranslateY(info.getY());
				
				int nota = info.getNote() - (12 * 3);
				boolean sharp = isSharp(nota); 
				if (sharp) nota--;
				int index = (int) (Math.floor((1 / 12f) * (7 * nota + 16))) - 1;
				
				char key = (index < 0 || index >= KEYS.length) ? '?' : KEYS[index];
				String note = String.valueOf(key);
				if (sharp) {
					note = note.toUpperCase();
					if (Character.isDigit(key)) {
						note = "^" + note;
					}
				}
				Text text = new Text(note);
				text.setFont(Font.font("Calibri", 16));
				
				getChildren().addAll(rect, text);
				
				this.info = info;
			}
		}
		
		for (NoteInformation i : info) {
			canvas.getChildren().add(new NoteRectangle(i));
		}
		
		canvas.getChildren().add(pointer);
		
		VBox tracksPane = new VBox(5);
		
		for (int i = 0; i < tracks.length; i++) {
			CheckBox check = new CheckBox(i + " (" + tracks[i].size() + ")");
			check.setSelected(true);
			check.selectedProperty().addListener((obs, oldValue, newValue) -> {
				int index = Character.getNumericValue(check.getText().charAt(0));
				sequencer.setTrackMute(index, !newValue);
				canvas.getChildren().stream()
						.filter(n -> n instanceof NoteRectangle)
						.map(n -> (NoteRectangle) n)
						.filter(r -> r.info.getTrack() == index)
						.forEach(r -> r.setVisible(newValue));
			});
			tracksPane.getChildren().add(check);
		}
		
		
		Thread pointerThread = new Thread(() -> {
			while (alive) {
				double y = (sequencer.getTickPosition() / divisor) - 10;
				Platform.runLater(() -> pointer.setTranslateY(Math.round(y)));
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		pointerThread.start();
		
		primaryStage.setOnCloseRequest(e -> {
			if (sequencer.isOpen()) {
				sequencer.stop();
				sequencer.close();
			}
			alive = false;
		});
		
		Button stopButton = new Button();
		stopButton.setGraphic(stop);
		Button playPauseButton = new Button();
		playPauseButton.graphicProperty().bind(Bindings.when(playing).then(pause).otherwise(play));
		playPauseButton.setOnAction(e -> switchSequencerState());
		
		HBox topPanel = new HBox(5, stopButton, playPauseButton);
		HBox mainContent = new HBox(10, tracksPane, scrollPane);
		VBox root = new VBox(10, topPanel, mainContent);
		
		root.setPadding(new Insets(5));
		tracksPane.setMinWidth(Region.USE_PREF_SIZE);
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	private static boolean isSharp(int nota) {
		nota = nota % 12;
		return nota < 5 ? (nota % 2 == 1) : (nota % 2 == 0);
	}

	private static List<NoteInformation> parseNotes(Track[] tracks, float divisor) {
		List<NoteInformation> info = new ArrayList<>();
		MidiEvent start;
		MidiEvent end;
		double height;
		int program;
		int note;
		byte[] endMessage;
		
		for (int i = 0; i < tracks.length; i++) {
			program = 0;
			Track track = tracks[i];
			
			for (int j = 0; j < track.size(); j++) {
				start = track.get(j);
				note = start.getMessage().getMessage()[1] & 0xFF;
				
				if (isNoteEvent(start) && isNoteOn(start.getMessage())) {
					for (int k = j; k < track.size(); k++) {
						end = track.get(k);
						endMessage = end.getMessage().getMessage();
						
						if (((endMessage[1] & 0xFF) == note) && isNoteEvent(end) && !isNoteOn(end.getMessage())) {
							height = Math.ceil((end.getTick() - start.getTick()) / divisor) - 1;
							
							//Note, X, Y, width, height, program, track index
							info.add(new NoteInformation(
									endMessage[1],
									endMessage[1] * NOTE_WIDTH,
									Math.round(start.getTick() / divisor),
									NOTE_WIDTH - 1, height, program, i));
							
							break;
						}
					}
				} else if (isProgramChange(start)) {
					program = start.getMessage().getMessage()[1] & 0xFF;
				}
			}
		}
		
		return info;
	}
	
	private void switchSequencerState() {
		if (playing.get()) {
			sequencer.stop();
		} else {
			if (!sequencer.isOpen()) {
				try {
					sequencer.open();
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
			}
			
			sequencer.start();
		}
		
		//Invert
		playing.set(!playing.get());
	}
	
	private static boolean isNoteEvent(MidiEvent event) {
		int status = event.getMessage().getStatus() >> 4;
		return status == 8 || status == 9;
	}
	
	private static boolean isProgramChange(MidiEvent event) {
		return event.getMessage().getStatus() >> 4 == 0xC;
	}
	
	private static boolean isNoteOn(MidiMessage message) {
		byte[] bytes = message.getMessage();
		return ((bytes[0] & 0xFF) >> 4) == 9 && bytes[2] > 0;
	}
}
