package br.concatto.comparator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class Decompresser {
	private static final Pattern leadingZeros = Pattern.compile("^0*");
	private static List<Long> xrefTable = new ArrayList<>();
	
	public static void parse(File file) {
		try (RandomAccessFile r = new RandomAccessFile(file, "r")) {
			long xref = findXref(r);
			parseXref(r, xref);
			
			
			parseText(r, xrefTable.get(2));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void parseText(RandomAccessFile r, long entry) throws IOException {
		System.out.println("Entry " + entry);
		r.seek(entry);
		if (!r.readLine().endsWith("obj")) throw new IllegalStateException("Not an object.");

		StringBuilder content = new StringBuilder();
		String portion = r.readLine();
		if (portion.charAt(0) != '<') return;
		
		while (true) {
			content.append(portion);
			if (portion.endsWith(">>")) break;
			portion = r.readLine();
		}
		
		String[] values = content.toString().split("/");
		boolean found = false;
		for (String value : values) {
			if (value.startsWith("FlateDecode")) {
				found = true;
				break;
			}
		}
		
		if (!found) return;
		
		if (!r.readLine().equals("stream")) throw new IllegalStateException("Does not appear to contain stream.");
		
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		byte[] end = "\nendstream".getBytes();
		int count = 0;
		while (count < end.length) {
			byte b = r.readByte();
			data.write(b);
			
			if (b == end[count]) count++;
			else count = 0;
		}
		
		byte[] in = new byte[data.size() - end.length];
		byte[] optimization = data.toByteArray();
		for (int i = 0; i < in.length; i++) {
			in[i] = optimization[i];
		}
		
		try {
			decompress(new ByteArrayInputStream(in), new ByteArrayOutputStream());
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
	}

	private static void parseXref(RandomAccessFile r, long xref) throws IOException {
		r.seek(xref);
		if (!r.readLine().equals("xref")) throw new IllegalStateException("Wrong byte offset.");
		
		String line;
		while (!(line = r.readLine()).equals("trailer")) {
			String[] control = line.split(" ");
			System.out.println(line);
			for (int i = 0; i < Integer.parseInt(control[1]); i++) {
				String entry = r.readLine().trim();
				if (entry.length() != 18) continue;
				
				if (entry.charAt(entry.length() - 1) == 'n') {
					entry = entry.substring(0, entry.indexOf(' '));
					entry = leadingZeros.matcher(entry).replaceFirst("");
					xrefTable.add(Long.parseLong(entry));
				}
			}
		}
		
		String prev = "/Prev ";
		int prevIndex;
		
		while (!(line = r.readLine()).equals("startxref")) {
			if ((prevIndex = line.lastIndexOf(prev)) != -1) {
				prevIndex += prev.length();
				String offset = line.substring(prevIndex, line.indexOf('/', prevIndex));
				parseXref(r, Long.parseLong(offset));
				return;
			}
		}
	}

	private static long findXref(RandomAccessFile r) throws IOException {
		final byte[] limit = "startxref".getBytes();
		int count = limit.length - 1;
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		
		long pointer = r.length() - 1;
		r.seek(pointer);
		
		while (true) {
			r.seek(pointer--);
			
			byte b = r.readByte();
			data.write(b);
			
			if (b == limit[count]) count--;
			else count = limit.length - 1;
			
			if (count == -1) break;
		}
		
		String result = new StringBuilder(data.toString()).reverse().toString();
		result = result.substring(limit.length, result.indexOf("%%EOF")).trim();
		return Long.parseLong(result);
	}
	
	private static void decompress(InputStream in, OutputStream out) throws IOException, DataFormatException {
		byte[] buf = new byte[2048];
		int read = in.read(buf);
		if (read > 0) {
			Inflater inflater = new Inflater();
			inflater.setInput(buf, 0, read);
			byte[] res = new byte[2048];
			while (true) {				
				int resRead = inflater.inflate(res);

				if (resRead != 0) {
					out.write(res, 0, resRead);
					System.out.println(new String(res));
					continue;
				}
				if (inflater.finished() || inflater.needsDictionary() || in.available() == 0) {
					break;
				}
				read = in.read(buf);
				inflater.setInput(buf, 0, read);
			}
		}
		out.close();
	}
}
