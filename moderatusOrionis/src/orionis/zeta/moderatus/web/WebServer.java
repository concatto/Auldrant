package orionis.zeta.moderatus.web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class WebServer {
	private static final String HOST = "localhost";
	private static final String IMAGE_FOLDER = "/productimages/";
	
	public static void upload(File file) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL("http", HOST, 8000, "/classes/postfile.php").openConnection();
		
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=EpsilonOrionis; charset=utf-8");
		
		String start = "--EpsilonOrionis\r\n"
				+ "Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"\r\n"
				+ "Content-Type: image/jpeg\r\n\r\n";
		String finish = "\r\n--EpsilonOrionis--\r\n";
		
		BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
		FileInputStream in = new FileInputStream(file);
		
		byte[] imageData = new byte[(int) file.length()];
		in.read(imageData);
		
		out.write(start.getBytes());
		out.write(imageData);
		out.write(finish.getBytes());
		out.close();
		in.close();
		
		BufferedReader echo = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String result = echo.readLine();
		if (result.charAt(0) == '0') throw new IOException(result.substring(1));
	}
	
	public static String encodeURL(String fileName) {
		if (fileName == null) return null;
		try {
			URI uri = new URI("http", null, HOST, 8000, IMAGE_FOLDER + URLDecoder.decode(fileName, "UTF-8"), null, null);
			return uri.toASCIIString();
		} catch (URISyntaxException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Removes <i>filesToRemove</i> from the server.
	 * @param filesToRemove
	 * @return A String representing the amount of files removed;
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static String removeFromServer(List<String> filesToRemove) throws IOException {
		JSONArray jsonArray = new JSONArray();
		jsonArray.addAll(filesToRemove);
		
		HttpURLConnection deletion = (HttpURLConnection) new URL("http", HOST, 8000, "/classes/delete.php").openConnection();
		deletion.setRequestMethod("POST");
		deletion.setDoOutput(true);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(deletion.getOutputStream()));
		out.write("data=" + jsonArray.toJSONString());
		out.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(deletion.getInputStream()));
		return in.readLine();
	}
	
	public static List<String> getProductImages() throws IOException {
		URLConnection conn = new URL("http", HOST, 8000, "/classes/glob.php").openConnection();
		BufferedReader stream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		@SuppressWarnings("unchecked")
		List<String> files = (List<String>) JSONValue.parse(stream.readLine());
		
		return files.stream()
				.map(WebServer::encodeURL)
				.map(t -> t.substring(t.lastIndexOf("/") + 1))
				.collect(Collectors.toList());
	}
}