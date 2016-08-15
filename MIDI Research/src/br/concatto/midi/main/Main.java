package br.concatto.midi.main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

public class Main {
	public Main() {
		try {
			File file = new File("C:/Drum Test.mid");
			MidiDetails details = MidiDetails.analyze(file);
			Synthesizer synth = MidiSystem.getSynthesizer();
			MidiChannel channel = synth.getChannels()[9];
			synth.open();
			
			Sequence sequence = MidiSystem.getSequence(file);
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.setSequence(sequence);
			System.out.println(sequencer.getTempoInMPQ() + ", " + details.getMicrosecondsPerQuarter());
			System.out.println(sequencer.getTempoInBPM() + ", " + details.getBeatsPerMinute());
			
			int tempo = details.getMicrosecondsPerQuarter();
			int resolution = sequence.getResolution();
			
			Track[] tracks = sequence.getTracks();
			Track track = tracks[1];
			
			float previousTime = 0;
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				int[] data = unsignBytes(message.getMessage());
				int command = data[0] / 16;
				System.out.printf("%d, %s\n", event.getTick(), Arrays.toString(data));
				
				if (command == 9 && data[2] > 0) {
					float time = event.getTick() * (tempo / resolution);
					long sleep = Math.round((time - previousTime) / 1000);
					channel.noteOn(data[1], data[2]);
					Thread.sleep(sleep);
					previousTime = time;
				}
			}
			
		} catch (InvalidMidiDataException | IOException | MidiUnavailableException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private int[] unsignBytes(byte[] bytes) {
		int[] unsigned = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			unsigned[i] = bytes[i] & 0xFF;
		}
		return unsigned;
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
