package br.concatto.grandsynth;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

public class C {
	private Path path = Paths.get("C:/hi.txt");
	private SourceDataLine line;
	
	private volatile byte[] buffer;
	private boolean active = true;
	private int offset;
	private Lock lock = new ReentrantLock();
	
	public C() {
		RandomAccessFile raf;
		File file;
		
		try {
			file = new File(getClass().getClassLoader().getResource("genusrmusescore.sf2").toURI());
			raf = new RandomAccessFile(file, "r");
		} catch (URISyntaxException | FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			InstrumentData[] essence = collectEssence(raf);
			
			
			JFrame frame = new JFrame();
			frame.setSize(320, 180);
			frame.getContentPane().addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int index = 242;
					lock.lock();
					if (buffer != null) {
						byte[] newBuffer = new byte[Math.max(buffer.length - offset, essence[index].data.length)];
						for (int i = 0; i < newBuffer.length; i++) {
							int sum = 0;
							int length = 0;
							if (i < essence[index].data.length) {
								sum += essence[index].data[i];
								length++;
							}
							
							if (offset + i < buffer.length) {
								sum += buffer[offset + i];
								length++;
							}
							newBuffer[i] = (byte) (sum / length);
						}
						buffer = newBuffer;
						offset = 0;
					} else {
						buffer = essence[index].data;
					}
					lock.unlock();
				}
			});
			new Thread(() -> {
				try {
					audio();
				} catch (LineUnavailableException e1) {
					e1.printStackTrace();
				}
			}).start();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			frame.getContentPane().setFocusable(true);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void parseSoundbank(RandomAccessFile raf) throws IOException {
		StringBuilder builder = new StringBuilder();
		raf.seek(0x1dca628);
		byte[] buf = new byte[46];
		
		Pattern pattern = Pattern.compile("[^\\u0020-\\u007a].*");
		while (raf.read(buf, 0, buf.length) > 0) {
			String instr = new String(Arrays.copyOf(buf, 20));
			if (instr.equals("EOS")) break;
			Matcher matcher = pattern.matcher(instr);
			if (matcher.find()) {
				instr = instr.substring(0, matcher.start());
			}
			builder.append(instr);
			
			int sum = 0;
			int start = 0;
			byte[] dword = new byte[4];
			for (int i = 0; i < 5; i++) {
				sum = 0;
				start = 19 + (dword.length * i);
				for (int j = 0; j < dword.length; j++) {
					dword[j] = buf[start + dword.length - j];
					sum += (dword[j] & 0xFF) << (8 * (dword.length - j - 1));
				}
				if (i < 4) sum *= 2;
				builder.append(":").append(sum);
			}
			
			start += dword.length + 1;
			builder.append(":").append(buf[start] & 0xFF);
			builder.append(System.getProperty("line.separator"));
		}
		BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		writer.write(builder.toString());
		writer.close();
	}
	
	static class InstrumentData {
		public String name;
		public int start;
		public int end;
		public int loopStart;
		public int loopEnd;
		public int sampleRate;
		public int originalNote;
		public byte[] data;
		
		public InstrumentData(String name, int start, int end, int loopStart, int loopEnd, int sampleRate, int originalNote) {
			this.name = name;
			this.start = start;
			this.end = end;
			this.loopStart = loopStart;
			this.loopEnd = loopEnd;
			this.sampleRate = sampleRate;
			this.originalNote = originalNote;
		}
		
		@Override
		public String toString() {
			return String.format("[%s: start %d, end %d, sLoop %d, eLoop %d, sample %d, note %d]", name, start, end, loopStart, loopEnd, sampleRate, originalNote);
		}
		
		public static InstrumentData parse(String s) {
			String[] data = s.split(":");
			if (data.length != 7) throw new IllegalArgumentException();
			return new InstrumentData(
					data[0],
					Integer.parseInt(data[1]),
					Integer.parseInt(data[2]),
					Integer.parseInt(data[3]),
					Integer.parseInt(data[4]),
					Integer.parseInt(data[5]),
					Integer.parseInt(data[6])
			);
		}
	}
	
	private void audio() throws LineUnavailableException {
		if (line == null) {
			line = AudioSystem.getSourceDataLine(null);
		}
		
		line.open(new AudioFormat(8000, 16, 2, true, false));
		line.start();
		while (active) {
			lock.lock();
			if (buffer != null && buffer.length > offset + 32) {
				line.start();
				line.write(buffer, offset, 32);
				offset += 32;
			} else {
				line.drain();
				line.stop();
			}
			lock.unlock();
		}
		line.stop();
		line.close();
	}
	
	private InstrumentData[] collectEssence(RandomAccessFile raf) throws IOException {		
		InstrumentData[] points = Files.lines(path).map(InstrumentData::parse).toArray(InstrumentData[]::new);
		Arrays.stream(points).forEach(info -> {
			try {
				raf.seek(info.start);
				byte[] audio = new byte[(4 * Math.round(info.end/4)) - (4 * Math.round(info.start / 4))];
				raf.read(audio);
				info.data = audio;
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		return points;
	}
	
	public static void main(String[] args) {
		new C();
	}
}
