package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FromFile {
	private int topCounter = 0;
	private int subCounter = 0;
	private Pattern pattern = Pattern.compile(".*displayId=\"(\\d+)\".*");

	public FromFile() {
		List<List<Map<String, String>>> data = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			List<Map<String, String>> second = new ArrayList<>();
			for (int j = 0; j < 8; j++) {
				second.add(new HashMap<>());
			}
			data.add(second);
		}
		
		String[] topTypes = {"Cloth", "Leather", "Mail", "Plate"};
		String[] subTypes = {"Chest", "Wrists", "Waist", "Head", "Hands", "Legs", "Shoulders", "Feet"};
		
		try {
			File output = new File("C:/out.txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			out.write(topTypes[0] + "\r\n");
			
			Files.lines(Paths.get("C:", "itemdata.txt"))
				.filter(line -> line.length() >= 11)
				.map(line -> line.substring(11))
				.filter(line -> Character.isDigit(line.charAt(0)))
				.forEach(line -> {
					if (subCounter == 8) {
						subCounter = 0;
						topCounter++;
						try {
							out.write(topTypes[topCounter] + "\r\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try {
						out.write(subTypes[subCounter] + "\r\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					Map<String, String> current = data.get(topCounter).get(subCounter);
					for (String id : line.split(",")) {
						String icon = getIcon(id);
						if (!current.containsKey(icon)) {
							current.put(icon, id);
							try {
								out.write(String.format("i(%s,%s,2,100,nil,nil,2,nil,5,nil,nil,2)\r\n", id, icon));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					subCounter++;
					try {
						out.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private String getIcon(String id) {
		try {
			URL url = new URL("http://www.wowhead.com/item=" + id + "&xml");
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();
			Matcher matcher = pattern.matcher(line);
			matcher.matches();
			return matcher.group(1);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static void main(String[] args) {
		new FromFile();
	}
}
