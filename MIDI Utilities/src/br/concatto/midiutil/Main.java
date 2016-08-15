package br.concatto.midiutil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JFrame {	
	private class NoteList extends ArrayList<Rectangle> {
		private Color noteColor;

		public NoteList(Color noteColor) {
			this.noteColor = noteColor;
		}
		
		public Color getNoteColor() {
			return noteColor;
		}
	}
	
	private class NotePanel extends JPanel {
		private List<NoteList> notes;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			for (NoteList rects : notes) {
				g.setColor(rects.getNoteColor());
				for (Rectangle rect : rects) {
					g.fillRect(rect.x, rect.y, rect.width, rect.height);
				}
			}
		}

		public void drawNotes(List<NoteList> notes) {
			this.notes = notes;
			repaint();
		}
	}

	private static final char[] KEYS = {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
		'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
		'z', 'x', 'c', 'v', 'b', 'n', 'm'
	};
	
	private static final char[] NOTES = {'C', 'D', 'E', 'F', 'G', 'A', 'B'};
	
	private static final int NOTE_WIDTH = 8;
	private static final Color[] COLORS = {
		Color.ORANGE, Color.RED, Color.BLUE, Color.GREEN,
		Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.PINK
	};
	private JPanel root;
	private NotePanel notePanel;
	private JScrollPane scroll;
	
	public Main() {
		super("MIDI Utilities");
		File file = new File("C:/r.mid");
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(file);
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		Track[] tracks = sequence.getTracks();
		
		root = new JPanel();
		notePanel = new NotePanel();
		
		scroll = new JScrollPane(notePanel);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		
		List<NoteList> structure = new ArrayList<>();
		
		int height = 0;
		for (int i = 0; i < tracks.length; i++) {
			Track track = tracks[i];
			NoteList rects = new NoteList(COLORS[i % COLORS.length]);
			Rectangle rect = new Rectangle();
			for (int j = 0; j < track.size(); j++) {
				rect.width = NOTE_WIDTH;
				
				MidiEvent event = track.get(j);
				int[] data = unsignBytes(event.getMessage().getMessage());
				int command = data[0] / 16;
				
				if (command == 9) {
					if (data[2] == 0) {
						rect.height = (int) event.getTick() - rect.y;
						if (rect.y + rect.height > height) height = rect.y + rect.height;
						rects.add(new Rectangle(rect));
					} else {
						rect.y = 5 + (int) event.getTick();
						rect.x = data[1] * (NOTE_WIDTH + 2);
					}
				}
			}
			
			if (!rects.isEmpty()) {
				structure.add(rects);
			}
		}
		
		//if (y > y & y < y2 | y2 > y & y2 < y2 
//		notePanel.setPreferredSize(new Dimension(127 * (NOTE_WIDTH + 2), height));
		notePanel.drawNotes(structure);
		
		Synthesizer synth;
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
		} catch (MidiUnavailableException e1) {
			e1.printStackTrace();
			return;
		}
		
		File f = new File("C:/Users/Fernando/Downloads/GeneralUser GS MuseScore v1.442.sf2");
		Soundbank bank;
		try {
			bank = MidiSystem.getSoundbank(f);
		} catch (IOException | InvalidMidiDataException e) {
			e.printStackTrace();
			return;
		}

		MidiChannel channel = synth.getChannels()[0];
		if (synth.isSoundbankSupported(bank)) {
			synth.loadAllInstruments(bank);
		}
		
		notePanel.setFocusable(true);		
		notePanel.addKeyListener(new KeyAdapter() {
			private int transposition = 2;
			private int extra = 1 * 12;
			private Set<Integer> pressed = new HashSet<>();
			private int program = 40;
			{channel.programChange(0, program);}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					channel.programChange(0, ++program);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					channel.programChange(0, --program);
				}
				char c = Character.toLowerCase((char) e.getExtendedKeyCode());
				for (int i = 0; i < KEYS.length; i++) {
					if (c == KEYS[i]) {
						boolean shift = e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK;
						int pitch = i + 1;
						int note = pitch * 2 - (1 + ((pitch - 1) / 7) + ((pitch + 3) / 7));
						if (shift) {
							if (pitch % 7 != 0 && (pitch + 4) % 7 != 0) {
								note++;
							} else {
								return;
							}
						}
						int noteNumber = note + (transposition * 12);
						if (!pressed.contains(noteNumber)) {
							channel.noteOn(noteNumber, 100);
							channel.noteOn(noteNumber + extra, 100);
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
						int note = pitch * 2 - (1 + ((pitch - 1) / 7) + ((pitch + 3) / 7));
						int noteNumber = note + (transposition * 12);
						
						if (pitch % 7 != 0 && (pitch + 4) % 7 != 0) {
							channel.noteOff(noteNumber + 1, 100);
							channel.noteOff(noteNumber + 1 + extra, 100);
							pressed.remove(noteNumber + 1);
						}
						
						if (pressed.contains(noteNumber)) {
							channel.noteOff(noteNumber, 100);
							channel.noteOff(noteNumber + extra, 100);
							pressed.remove(noteNumber);
						}
						break; 
					}
				}
			}
		});
		
		root.add(scroll);
		root.setLayout(new GridLayout());
		add(root);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private static int[] unsignBytes(byte[] bytes) {
		int[] unsigned = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			unsigned[i] = bytes[i] & 0xFF;
		}
		return unsigned;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Main());
	}
	
	private static String translateReality(String virtual) {
		StringBuilder b = new StringBuilder();
		char[] array = virtual.toCharArray();
		boolean asteriskFound = false;
		
		for (char c : array) {			
			if (c == '*') asteriskFound = true;
			
			char lower = Character.toLowerCase(c);
			int index = -1;
			for (int i = 0; i < KEYS.length; i++) {
				if (lower == KEYS[i]) {
					index = i;
					break;
				}
			}
			
			if (index >= 0 && !asteriskFound) {
				char note = NOTES[index % NOTES.length];
				
				b.append(note);
				if (Character.isUpperCase(c)) b.append('#');
				System.out.println(Math.ceil(index / NOTES.length));
				b.append((int) Math.floor(index / NOTES.length) + 2);
			} else {
				b.append(c);
			}
		}
		return b.toString();
	}
}
