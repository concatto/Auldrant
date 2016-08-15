package orionis.zeta.canticum.model;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class RandomSequence {
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);
	private Synthesizer synth;
	private MidiChannel[] channels;
	private Instrument[] instruments;
	private Random rng = new Random();
	private Runnable playNote = () -> channels[1].noteOn(rng.nextInt(100), rng.nextInt(50) + 30);
	
	public RandomSequence() {
		try {
			synth = MidiSystem.getSynthesizer();
			channels = synth.getChannels();
			instruments = synth.getAvailableInstruments();
			for (Instrument inst : instruments) {
				synth.loadInstrument(inst);
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void playSequence() {
		try {
			synth.open();
			executor.submit(playNote);
			for (int i = 0; i < 20; i++) {
				channels[0].programChange(rng.nextInt(50));
				executor.schedule(playNote, rng.nextInt(4000), TimeUnit.MILLISECONDS);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void stopSequence() {
		synth.close();
	}
	
	public void terminate() {
		stopSequence();
		executor.shutdown();
	}
}
