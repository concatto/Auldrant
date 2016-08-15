package br.concatto.favouritetwittersearch;

public class Tweet {
	private String name;
	private String content;
	
	public Tweet(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}
}
