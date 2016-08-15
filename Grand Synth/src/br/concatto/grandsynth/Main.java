package br.concatto.grandsynth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private static final String KEY_INSTRUMENT = "instr";
	private static final String KEY_TRANSPOSITION = "transp";
	
	private static final char[] KEYS = {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
		'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
		'z', 'x', 'c', 'v', 'b', 'n', 'm'
	};
	
	private static final Border FOCUSED = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
	private static final Border NO_FOCUS = BorderFactory.createLineBorder(Color.RED);
	boolean a = false;
	
	private JPanel raiz;
	private JPanel painelEntrada;
	private JLabel transpositionLabel = new JLabel();
	private JLabel programLabel = new JLabel("Carregando Soundbank...");
	private JLabel instrumentLabel = new JLabel();
	private JButton obterFoco = new JButton("Obter foco");

	private Synthesizer synth;
	private MidiChannel channel;
	private int indexInstrument;
	private int transposition;
	
	private Preferences prefs;
	private Soundbank bank;
	private Instrument[] instruments;

	public Main() {
		super("Grand Synth");
		prefs = Preferences.userNodeForPackage(getClass());
		indexInstrument = prefs.getInt(KEY_INSTRUMENT, 0);
		transposition = prefs.getInt(KEY_TRANSPOSITION, 2);
		
		atualizarTransposition();
		CompletableFuture.runAsync(this::prepararSynthesizer).exceptionally(this::apresentarErro).thenRun(this::atualizarInstrumento);
		
		raiz = new JPanel();
		painelEntrada = new JPanel();
		
		raiz.setFocusable(true);
		raiz.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		painelEntrada.setPreferredSize(new Dimension(260, 100));
		painelEntrada.setFocusable(true);
				
		painelEntrada.addKeyListener(new KeyAdapter() {
			private int extra = 1 * 12;
			private Set<Integer> pressed = new HashSet<>();
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					indexInstrument = a ? 48 : 19;
					a = !a;
					atualizarInstrumento();
					return;
				}
				
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
					atualizarInstrumento(e.getKeyCode() == KeyEvent.VK_UP);
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_LEFT:
					atualizarTransposition(e.getKeyCode() == KeyEvent.VK_RIGHT);
					break;
				}

				Optional<Integer> buscarTecla = buscarTecla(e.getExtendedKeyCode());
				
				char c = Character.toLowerCase((char) e.getExtendedKeyCode());
				for (int i = 0; i < KEYS.length; i++) {
					if (c == KEYS[i]) {
						boolean shift = e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK;
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
						if (!pressed.contains(noteNumber) && !(hasSharp && pressed.contains(originalNoteNumber + 1)) && indexInstrument != 97 /* Bug no soundbank */) {
							System.out.println(noteNumber);
							channel.noteOn(noteNumber, 127);						
							channel.noteOn(noteNumber + extra, 127);
							pressed.add(noteNumber);
						}
						break;
					}
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {				
				char c = Character.toLowerCase((char) e.getExtendedKeyCode());				
				for (int i = 0; i < KEYS.length; i++) {
					if (c == KEYS[i]) {
						int pitch = i + 1;
						int note = pitch * 2 - (2 + ((pitch - 1) / 7) + ((pitch + 3) / 7));
						int noteNumber = note + (transposition * 12);
						boolean hasSharp = pitch % 7 != 0 && (pitch + 4) % 7 != 0;
						
						if (hasSharp) {
							channel.noteOff(noteNumber + 1, 90);
							channel.noteOff(noteNumber + 1 + extra, 90);
							pressed.remove(noteNumber + 1);
						}
						
						if (pressed.contains(noteNumber)) {
							channel.noteOff(noteNumber, 90);
							channel.noteOff(noteNumber + extra, 90);
							pressed.remove(noteNumber);
						}
						break; 
					}
				}
			}
		});
		
		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				painelEntrada.requestFocusInWindow();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				/* Nada */
			}
		});
		
		painelEntrada.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				painelEntrada.setBorder(NO_FOCUS);
				obterFoco.setEnabled(true);
			}

			@Override
			public void focusGained(FocusEvent e) {
				painelEntrada.setBorder(FOCUSED);
				obterFoco.setEnabled(false);
			}
		});
		
		obterFoco.addActionListener(e -> painelEntrada.requestFocusInWindow());		
		
		obterFoco.setAlignmentX(CENTER_ALIGNMENT);
		transpositionLabel.setAlignmentX(CENTER_ALIGNMENT);
		instrumentLabel.setVerticalAlignment(SwingConstants.CENTER);
		instrumentLabel.setHorizontalAlignment(SwingConstants.CENTER);
		programLabel.setAlignmentX(CENTER_ALIGNMENT);
		programLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		painelEntrada.setLayout(new BorderLayout());
		painelEntrada.add(instrumentLabel, BorderLayout.CENTER);
		
		raiz.setLayout(new BoxLayout(raiz, BoxLayout.Y_AXIS));
		raiz.add(transpositionLabel);
		raiz.add(programLabel);
		raiz.add(painelEntrada);
		raiz.add(Box.createRigidArea(new Dimension(0, 5)));
		raiz.add(obterFoco);
		add(raiz);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	private boolean isSharp(int nota) {
		nota = nota % 12;
		return nota < 5 ? (nota % 2 == 1) : (nota % 2 == 0);
	}
	
	private Optional<Integer> buscarTecla(int codigo) {
		char tecla = Character.toLowerCase((char) codigo);
		for (int i = 0; i < KEYS.length; i++) {
			if (tecla == KEYS[i]) return Optional.of(i + 1);
		}
		return Optional.empty();
	}
	
	private void atualizarInstrumento(boolean incrementar) {
		synth.unloadInstrument(instruments[indexInstrument]);
		indexInstrument += incrementar ? (indexInstrument < instruments.length - 1 ? 1 : 0) : (indexInstrument > 0 ? -1 : 0);
		atualizarInstrumento();
	}
	
	private void atualizarInstrumento() {
		Instrument instrument = instruments[indexInstrument];
		synth.loadInstrument(instrument);
		
		instrumentLabel.setText(instrument.getName());
		channel.programChange(instrument.getPatch().getBank(), instrument.getPatch().getProgram());
		programLabel.setText(String.format("Bank: %d. Program: %d. Index: %d.", instrument.getPatch().getBank(), instrument.getPatch().getProgram(), indexInstrument));
		prefs.putInt(KEY_INSTRUMENT, indexInstrument);
	}
	
	private void atualizarTransposition(boolean incrementar) {
		transposition += incrementar ? 1 : -1;
		atualizarTransposition();
	}
	
	private void atualizarTransposition() {
		transpositionLabel.setText("Transposition: " + String.valueOf(transposition));
		prefs.putInt(KEY_TRANSPOSITION, transposition);
	}
	
	private void prepararSynthesizer() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			channel = synth.getChannels()[0];
		} catch (MidiUnavailableException e) {
			return;
		}
		
		try {
			URL u = Main.class.getClassLoader().getResource("genusrmusescore.sf2");
			bank = MidiSystem.getSoundbank(u);
			if (!synth.isSoundbankSupported(bank)) throw new RuntimeException("Problema no soundbank");
			instruments = bank.getInstruments();
		} catch (InvalidMidiDataException | IOException e) {
			return;
		}
	}
	
	private Void apresentarErro(Throwable erro) {
		erro.printStackTrace();
		programLabel.setText(erro.getClass().getName());
		return null;
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Main().setVisible(true);
	}
}
