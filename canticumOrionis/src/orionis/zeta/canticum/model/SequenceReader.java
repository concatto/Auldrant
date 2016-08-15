package orionis.zeta.canticum.model;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import orionis.zeta.canticum.controller.Controller;

public class SequenceReader {
	private Controller controller = Controller.getInstance();
	private Sequencer sequencer;
	private Sequence sequence;
	private Synthesizer synth;
	private Receiver receiver;
	private ScheduledExecutorService executor;

	public SequenceReader() {
		try {
			sequencer = MidiSystem.getSequencer();
			synth = MidiSystem.getSynthesizer();
			synth.loadAllInstruments(synth.getDefaultSoundbank());
			receiver = synth.getReceiver();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public int playSequence(File sequenceFile) {
		try {
			int trackAmount = 0;
			sequence = MidiSystem.getSequence(sequenceFile);
			sequencer.setSequence(sequence);
			Track[] tracks = sequence.getTracks();
			synth.open();
			executor = Executors.newScheduledThreadPool(tracks.length);
			for (int i = 0; i < tracks.length; i++) {
				Track track = tracks[i];
				int trackSize = track.size();
				int trackNumber = i;
				if (trackSize > 5) trackAmount++;
				for (int j = 0; j < trackSize; j++) {
					MidiEvent event = track.get(j);
					long stamp = (long) (event.getTick() * (sequencer.getTempoInMPQ() / sequence.getResolution()));
					executor.schedule(() -> {
						receiver.send(event.getMessage(), -1);
						controller.sendByte(event.getMessage().getMessage(), trackNumber);
					}, stamp, TimeUnit.MICROSECONDS);
				}
			}
			return trackAmount;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void stop() {
		executor.shutdownNow();
		synth.close();
	}
}
