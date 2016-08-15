package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FromNothing {
	private Pattern icon = Pattern.compile(".*displayId=\"(\\d+)\".*");
	private Pattern quality = Pattern.compile(".*<quality id=\"(\\d+)\">.*");
	private Pattern type = Pattern.compile(".*<class id=\"(\\d+)\">.*");
	private Pattern subType = Pattern.compile(".*subclass id=\"(\\d+)\".*");
	private String[] types = {"7", "4", "0", "13", "15", "8", "5", "1", "10", "6"};
	private String[] qualities = {"2", "3", "4"};
	private AtomicInteger index = new AtomicInteger(108860);
	
	public FromNothing() {
		List<List<Map<String, String>>> data = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			List<Map<String, String>> second = new ArrayList<>();
			for (int j = 0; j < 5; j++) {
				second.add(new HashMap<>());
			}
			data.add(second);
		}
		
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			new Thread(() -> {
				int id;
				while ((id = index.incrementAndGet()) < 121000) {
					try {
						URLConnection connection = new URL("http://www.wowhead.com/item=" + id + "&xml").openConnection();
						String xml = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
						
						Matcher typeMatcher = type.matcher(xml);
						if (typeMatcher.matches() && typeMatcher.group(1).equals("2")) {
							Matcher qualityMatcher = quality.matcher(xml);
							if (qualityMatcher.matches() && isQualityValid(qualityMatcher.group(1))) {
								Matcher subTypeMatcher = subType.matcher(xml);
								if (subTypeMatcher.matches() && isTypeValid(subTypeMatcher.group(1))) {
									
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	private boolean isQualityValid(String quality) {
		for (String currentQuality : qualities) {
			if (currentQuality.equals(quality)) return true;
		}
		return false;
	}
	
	private boolean isTypeValid(String subType) {
		for (String type : types) {
			if (subType.equals(type)) return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		new FromNothing();
	}
}
