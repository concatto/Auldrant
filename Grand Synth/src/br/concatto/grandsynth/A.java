package br.concatto.grandsynth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class A extends Application {
	private Synthesizer synth;
	private MidiChannel channel;
	private Soundbank bank;
	private Instrument[] instruments;
	private static final char[] KEYS = {
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
			'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
			'Z', 'X', 'C', 'V', 'B', 'N', 'M'
		};

	Set<Integer> pressed = new HashSet<>();
	int transposition = 3;
	private SourceDataLine line;
	private byte[] bufs;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		TextField f = new TextField();
		Label programLabel = new Label("Program: temp");
		StackPane keyPane = new StackPane();
		VBox root = new VBox(programLabel, keyPane, f);
		Scene scene = new Scene(root);
		
		SimpleIntegerProperty prop = new SimpleIntegerProperty(1);
		
		programLabel.textProperty().bind(new StringBinding() {
			{
				bind(prop);
			}
			
			@Override
			protected String computeValue() {
				return "Program: " + prop.get();
			}
		});
		
		try {
			InputStream stream = new FileInputStream(new File("C:/Users/Fernando/Music/infra.wav"));
			AudioFormat format = new AudioFormat(48000, 16, 2, true, false);
			line = AudioSystem.getSourceDataLine(format);
			line.open();
			line.start();
			
			long overflow = Integer.MAX_VALUE;
			overflow = ((overflow + 1) * 2) / 16384;
			int len = (int) overflow;
			bufs = new byte[len];
			stream.read(bufs);
			
			
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
		
		
		keyPane.setPrefSize(200, 100);
		keyPane.setOnKeyPressed(e -> {
			f.appendText(e.getText());
//			line.write(bufs, 0, bufs.length);
			
			if (e.getCode().isDigitKey() || e.getCode().isLetterKey()) {
				char c = e.getText().toUpperCase().charAt(0);
				for (int i = 0; i < KEYS.length; i++) {
					if (c == KEYS[i]) {
						int pitch = i + 1;
						int note = pitch * 2 - (2 + ((pitch - 1) / 7) + ((pitch + 3) / 7));
						boolean hasSharp = pitch % 7 != 0 && (pitch + 4) % 7 != 0;
						int original = note;
						if (e.isShiftDown()) {
							if (hasSharp) {
								note++;
							} else {
								return;
							}
						}
						int noteNumber = note + transposition * 12;
						int originalNoteNumber = original + transposition * 12;
						if (!pressed.contains(noteNumber) && !(hasSharp && pressed.contains(originalNoteNumber + 1))) {
							channel.noteOn(noteNumber, 127);
							pressed.add(noteNumber);
						}
						break;
					}
				}
			}
		});
		
		prepararSynthesizer();
		keyPane.setOnKeyReleased(e -> {
			if (e.getCode().isDigitKey() || e.getCode().isLetterKey()) {
				char c = e.getText().toUpperCase().charAt(0);
				for (int i = 0; i < KEYS.length; i++) {
					if (c == KEYS[i]) {
						int pitch = i + 1;
						int note = pitch * 2 - (2 + ((pitch - 1) / 7) + ((pitch + 3) / 7));
						int noteNumber = note + (transposition * 12);
						boolean hasSharp = pitch % 7 != 0 && (pitch + 4) % 7 != 0;
						
						if (hasSharp) {
							channel.noteOff(noteNumber + 1, 90);
							pressed.remove(noteNumber + 1);
						}
						
						if (pressed.contains(noteNumber)) {
							channel.noteOff(noteNumber, 90);
							pressed.remove(noteNumber);
						}
						break; 
					}
				}
			}
		});
		
		primaryStage.setOnShown(e -> keyPane.requestFocus());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private void prepararSynthesizer() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			channel = synth.getChannels()[0];
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			URL u = Main.class.getClassLoader().getResource("genusrmusescore.sf2");
			bank = MidiSystem.getSoundbank(u);
			if (!synth.isSoundbankSupported(bank)) throw new RuntimeException("Problema no soundbank");
			instruments = bank.getInstruments();
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		synth.loadInstrument(instruments[48]);
		channel.programChange(48);
	}
	
	private void play(char c, boolean shift) {
		for (int i = 0; i < KEYS.length; i++) {
			if (c == KEYS[i]) {
				int pitch = i + 1;
				int note = pitch * 2 - (2 + ((pitch - 1) / 7) + ((pitch + 3) / 7));
				boolean hasSharp = pitch % 7 != 0 && (pitch + 4) % 7 != 0;
				int original = note;
				if (shift) {
					if (hasSharp) {
						note++;
					} else {
						return;
					}
				}
				int noteNumber = note + transposition * 12;
				int originalNoteNumber = original + transposition * 12;
				System.out.println(c);
				channel.noteOn(noteNumber, 127);
				if (!pressed.contains(noteNumber) && !(hasSharp && pressed.contains(originalNoteNumber + 1))) {
					
//					pressed.add(noteNumber);
				}
				break;
			}
		}
	}
	
	private void stop(char c) {
		for (int i = 0; i < KEYS.length; i++) {
			if (c == KEYS[i]) {
				int pitch = i + 1;
				int note = pitch * 2 - (2 + ((pitch - 1) / 7) + ((pitch + 3) / 7));
				int noteNumber = note + (transposition * 12);
				boolean hasSharp = pitch % 7 != 0 && (pitch + 4) % 7 != 0;
				
				if (hasSharp) {
					channel.noteOff(noteNumber + 1, 90);
					pressed.remove(noteNumber + 1);
				}
				
				if (pressed.contains(noteNumber)) {
					channel.noteOff(noteNumber, 90);
					pressed.remove(noteNumber);
				}
				break; 
			}
		}
	}
}
