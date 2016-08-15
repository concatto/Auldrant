package orionis.delta.melodia.control;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Controller extends Application {
	private int SAMPLING_RATE = 44100;
	private int SAMPLE_SIZE = 2;
	private ByteBuffer buffer;
	private SourceDataLine line;
	
	private Synthesizer synth;
	private Sequencer sequencer;
	private Sequence sequence;
	private FlowPane flow;
	private Track[] tracks;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
//		testar(primaryStage);
		test();
	}

	private void testar(Stage primaryStage) throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File("E:/Synthesia/Custom"));
		chooser.getExtensionFilters().add(new ExtensionFilter("Arquivos MIDI", "*.mid", "*.midi"));
		
		File target = new File("C:/Users/Fernando/Downloads/Phoenix Wright Ace Attorney - The Fragrance of Dark Coffee - Godots Theme.mid");
//		File target = new File("E:/Synthesia/Custom/Sortidas/Shadow of the Colossus - The Opened Way.mid");
		
		sequence = MidiSystem.getSequence(target);
		sequencer.setSequence(sequence);
		
		tracks = sequence.getTracks();
		flow = new FlowPane(Orientation.VERTICAL, 5, 5);
		
		int trackIndex = 0;
		for (Track track : tracks) {
			CheckBox box = new CheckBox(String.format("Track %d (%d events)", trackIndex++, track.size()));
			box.setSelected(true);
			flow.getChildren().add(box);
		}
		
		Button button = new Button("OK");
		VBox root = new VBox(20, flow, button);
		root.setPadding(new Insets(10));
		
		button.setOnAction(event -> new Thread(() -> openFire()).start());
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	private void openFire() {
		float tempo = sequencer.getTempoInBPM();
		int resolution = sequence.getResolution();
		double clocks = tempo * resolution / 60000d;
		
		List<MidiEvent> events = new ArrayList<>();
		
		Pattern trackNumber = Pattern.compile("Track (\\d+) \\(.*");
		
		flow.getChildren().forEach(node -> {
			CheckBox box = (CheckBox) node;
			if (box.isSelected()) {
				String text = box.getText();
				Matcher matcher = trackNumber.matcher(text);
				if (matcher.matches()) {
					int index = Integer.parseInt(matcher.group(1));
					for (int i = 0; i < tracks[index].size(); i++) {
						MidiEvent event = tracks[index].get(i);
						byte status = event.getMessage().getMessage()[0];
						events.add(event);
					}
				}
			}
		});
		
		int[] octaves = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		events.sort((first, second) -> first.getTick() > second.getTick() ? 1 : first.getTick() == second.getTick() ? 0 : -1);
		
		events.forEach(event -> {
			int note = event.getMessage().getMessage()[1];
			int index = Math.round(note / 12);
			octaves[index]++;
		});
		
		for (int i = 0; i < octaves.length; i++) {
			System.out.printf("Octave %d: %d notes\n", i, octaves[i]);
		}
		
		MidiChannel channel = synth.getChannels()[0];
		
		try {
			synth.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		
		String[] simplified = {"C", "D", "E", "F", "G", "A", "B"};
		String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
		
		long last = 0;
		int lastNote = 60;
		long time = 0;
		for (MidiEvent event : events) {
			byte[] message = event.getMessage().getMessage();
			time = Math.round(event.getTick() / clocks);
			if (message[message.length - 1] != 0 && message[0] == -112) {
				if (time != last) {
					channel.noteOn(lastNote, 100);
					long frequency = Math.round(440 * Math.pow(2, (lastNote - 69) / 12D));
					System.out.printf("beep(%d, %d);\n", frequency, (time - last));
					lastNote = message[1];
				}
				
				try {
					Thread.sleep(time - last);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
				
				last = time;
				
//				System.out.println(message[0] + " = " + message[1] + ", " + time);
				
				int octave = Math.round(message[1]/12);
				String note = notes[message[1] % 12];
				
				int noteIndex = -1;
				for (int i = 0; i < simplified.length; i++) {
					if (note.charAt(0) == simplified[i].charAt(0)) {
						noteIndex = i;
						break;
					}
				}
				
				int octaveShift = 2;
				int index = ((octave - octaveShift) * 7) + noteIndex;
//				System.out.printf("MIDI: %d; Octave: %d; Note: %s; Message: %s;\n", message[1], octave, note, Arrays.toString(message));
				
//				char key = keys[index];
//				if (note.length() > 1) key = Character.toUpperCase(key);
//				System.out.printf("%s ", key);
			} else {
				long frequency = Math.round(440 * Math.pow(2, (message[1] - 69) / 12D));
				channel.noteOff(message[1]);
				if (lastNote == frequency) System.out.println("End note: " + lastNote);
			}
		}
		channel.noteOn(lastNote, 100);
		long frequency = Math.round(440 * Math.pow(2, (lastNote - 69) / 12D));
		System.out.printf("beep(%d, %d);\n", frequency, (time - last));
	}
	
	public void test() throws LineUnavailableException, InterruptedException {
		AudioFormat format = new AudioFormat(SAMPLING_RATE, 16, 1, true, true);
		Info info = new Info(SourceDataLine.class, format);
 
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format);
		line.start();
		
		buffer = ByteBuffer.allocate(line.getBufferSize());
		
		double[] notes = {
				220, 247, 370, 247, 370, 370, 330,
				0,
				247, 370, 247, 370, 330, 294, 294, 277, 294, 294,
				196, 220, 233, 262, 294, 330, 349, 392, 440,
				87, 110, 110, 117, 123,
				440, 440, 370, 370, 294, 294, 247, 294,
				247, 294, 330, 294, 330, 370, 294, 294, 220,
				0,
				294, 262, 294, 349, 330, 294, 330, 262,
				0, 0,
				262, 523, 494,
				0, 0,
				349, 330, 294, 330, 294, 262, 247, 262, 330, 294,
				0, 0,
				220, 247, 262, 247, 330, 220,
				0, 0,
				494, 740, 880, 740, 587, 494, 587,
				0, 0, 0,
				440, 494, 740, 659, 587, 659, 740,
				0,
				587, 880, 466, 880, 466, 880, 784,
				698, 784, 880, 659, 698, 523, 440, 523,
				0, 0, 0,
				587, 659, 698,
				0,
				659, 698, 659, 494, 523, 587, 587, 523, 494, 523, 587, 659, 880, 988,
				0,
				1047, 1047,
				0,
				880, 880,
				0,
				1047, 988,
				123,
				117,
				0
		};
		
		double[] times = {
				277, 277, 277, 277, 1667, 557, 1110,
				557,
				277, 277, 277, 277, 557, 277, 277, 277, 277, 1110,
				277, 277, 277, 277, 277, 277, 277, 277, 1390,
				277, 834, 277, 557, 557,
				277, 557, 277, 557, 277, 557, 277, 557,
				277, 277, 277, 277, 277, 277, 277, 557, 277,
				834,
				277, 277, 277, 557, 557, 277, 277, 2222,
				277, 277,
				277, 277, 1390,
				277, 577,
				277, 277, 277, 277, 557, 557, 277, 277, 521, 869,
				277, 833,
				277, 277, 277, 834, 834, 2500,
				834, 557,
				277, 277, 277, 277, 277, 277, 277,
				557, 277, 277,
				277, 277, 277, 277, 277, 557, 277,
				557,
				277, 834, 277, 277, 277, 557, 557,
				277, 277, 277, 277, 277, 557, 277, 557,
				277, 557, 277,
				277, 277, 277,
				625,
				104, 104, 834, 277, 277, 277, 277, 277, 277, 277, 277, 277, 557, 277,
				277,
				557, 834,
				557,
				277, 834,
				557,
				277, 834,
				834,
				557,
				277
		};
		
		for (int i = 0; i < 151; i++) {
			beep(notes[i], times[i]);
		}
		
		line.drain();
		line.close();
	}
	
	private void beep(double frequency, double time) {
		double samples = SAMPLING_RATE * (time / 1000);
		double cyclePosition = 0;
		while (samples > 0) {
			double cycleIncrement = frequency / SAMPLING_RATE;
			buffer.clear();
 
			int currentSamples = line.available() / SAMPLE_SIZE;
			for (int i = 0; i < currentSamples; i++) {
				buffer.putShort((short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * cyclePosition)));
 
				cyclePosition += cycleIncrement;
				if (cyclePosition > 1) cyclePosition -= 1;
			}
 
			line.write(buffer.array(), 0, buffer.position());
			samples -= currentSamples;
			
			while (line.getBufferSize() / 2 < line.available()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void sleep(double time) {
		try {
			Thread.sleep(Math.round(time));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Controller() throws MidiUnavailableException {
		synth = MidiSystem.getSynthesizer();
		sequencer = MidiSystem.getSequencer();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
