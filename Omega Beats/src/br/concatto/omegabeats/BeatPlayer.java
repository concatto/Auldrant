package br.concatto.omegabeats;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

public class BeatPlayer {
	private static final String CONFLICT = "dddllqq  dddllqq    dddllqq  dddlqq        dddllqq  dddllqq    dddllqq  dddlqq        dddllqq  dddllqq    dddllqq  dddlqq        dddllqq  dddllqq    dddllqq  dddlqq    cc    113";
	private static final Map<Character, DrumElement> keyMap = new HashMap<>();
	static {
		keyMap.put(' ', DrumElement.NONE);
		keyMap.put('l', DrumElement.LOW_TOM);
		keyMap.put('p', DrumElement.HAND_CLAP);
		keyMap.put('c', DrumElement.CRASH_CYMBAL);
		keyMap.put('t', DrumElement.TOM);
		keyMap.put('s', DrumElement.SNARE);
		keyMap.put('d', DrumElement.DRUM);
		keyMap.put('q', DrumElement.SPLASH_CYMBAL);
	}
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Synthesizer synth;
	private MidiChannel channel;
	private Future<?> task;
	private volatile long delay;
	
	public BeatPlayer() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			
			File file = new File(getClass().getClassLoader().getResource("FluidR3.SF2").toURI());
			Soundbank bank;
			try {
				bank = MidiSystem.getSoundbank(file);
			} catch (IOException | InvalidMidiDataException e) {
				e.printStackTrace();
				return;
			}
			synth.open();
			channel = synth.getChannels()[9];
			if (synth.isSoundbankSupported(bank)) {
				Instrument instrument = bank.getInstruments()[16];
				synth.loadInstrument(instrument);
				channel.programChange(instrument.getPatch().getProgram());
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void execute(String command) {
		task = executor.submit(() -> {
			boolean cancelled = false;
			while (!cancelled) {
				for (char c : command.toCharArray()) {
					DrumElement instrument = keyMap.get(c);
					if (instrument != null) {
						if (instrument != DrumElement.NONE) {
							play(instrument);
						} else {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								cancelled = true;
							}
						}
					}
					
					if (Thread.currentThread().isInterrupted()) {
						cancelled = true;
					}
					
					if (cancelled) break;
				}
			}
		});
	}
	
	public void stop() {
		task.cancel(true);
	}

	private void play(DrumElement instrument) {
		channel.noteOn(instrument.getValue(), 127);
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
}
