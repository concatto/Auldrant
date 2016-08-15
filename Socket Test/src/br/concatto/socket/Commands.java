package br.concatto.socket;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Commands {
	private List<Command> data;
	
	public Commands() {
		data = new ArrayList<>();
	}
	
	public Commands add(Command cmd) {
		data.add(cmd);
		return this;
	}
	
	public Commands add(int code, String description) {
		return add(new Command(code, description));
	}
	
	@Override
	public String toString() {
		JSONObject json = data.stream().collect(new CommandCollector());
		
		return json.toString();
	}
}
