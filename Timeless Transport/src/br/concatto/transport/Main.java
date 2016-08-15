package br.concatto.transport;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {	
	public static void main(String[] args) {		
		Connection connection = Jsoup.connect("http://www.emuparadise.me/soundtracks/highquality/Valkyrie_Profile_Original_Soundtrack/147");
		try {
			Document doc = connection.get();
			Elements tables = doc.select("#content table");
			
			for (Element table : tables) {
				Elements rows = table.select("tr");
				rows.stream()
						.map(row -> row.select("a"))
						.filter(elements -> !elements.isEmpty())
						.map(row -> row.get(0))
						.map(anchor -> "http://www.emuparadise.me" + anchor.attr("href"))
//						.parallel()
						.filter(Main::checkExistence)
						.map(Jsoup::connect)
						.map(Main::getDoc)
						.map(Main::findLink)
						.forEach(uri -> writeFile(uri.toASCIIString()));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean checkExistence(String url) {
		url = url.substring(0, url.lastIndexOf('/'));
		url = url.substring(url.lastIndexOf('/') + 1);
		url = url.replace('_', ' ');
		File file = Paths.get("C:/Home/Valkyrie Profile Soundtrack/" + url + ".mp3").toFile();
		return !file.exists();
	}
	
	private static void writeFile(String path) {
		try {
			URL url = new URL(path);
			String name = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
			System.out.println("Downloading " + path);
			Files.copy(url.openStream(), Paths.get("C:/Home/Valkyrie Profile Soundtrack/").resolve(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static URI findLink(Document doc) {
		Elements children = doc.select("#content").get(0).children();
		String href = children.stream().filter(e -> !e.attr("href").isEmpty()).findFirst().get().attr("href");
		try {
			return new URI("http", "50.7.161.234", "/998ajxYxajs13jAKhdca" + href.substring(href.indexOf("/bks")), null);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	private static Document getDoc(Connection conn) {
		try {
			return conn.get();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
