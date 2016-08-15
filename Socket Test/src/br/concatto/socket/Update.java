package br.concatto.socket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Update {
	private String password;
	private int port;
	private String description;
	private Commands commands;

	public Update(String password, int port, String description, Commands commands) {
		this.password = password;
		this.port = port;
		this.description = description;
		this.commands = commands;
	}
	
	public void push() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:8000/servers.php").openConnection();
		
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		
		Map<String, String> map = new HashMap<>();
		map.put("pass", password);
		map.put("port", String.valueOf(port));
		map.put("desc", description);
		map.put("commands", commands.toString());
		
		String query = map.entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));
		
		OutputStream out = conn.getOutputStream();
		out.write(query.getBytes());
		out.flush();
		out.close();
		
		CharBuffer buf = CharBuffer.allocate(256);
		new InputStreamReader(conn.getInputStream()).read(buf);
		buf.flip();
		System.out.println(buf.toString());
	}
}
