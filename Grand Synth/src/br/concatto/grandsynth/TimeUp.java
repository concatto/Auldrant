package br.concatto.grandsynth;

public class TimeUp {
	private long time;
	private int note;
	private long duration;
	
	public TimeUp(long duration, int note) {
		this.duration = duration;
		this.note = note;
	}

	public long getTime() {
		return time;
	}

	public int getNote() {
		return note;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public void updateTime() {
		duration = System.currentTimeMillis() - duration;
	}
	
	@Override
	public String toString() {
		return String.format("%d dur %d wait %d", note, duration, time);
	}
}
