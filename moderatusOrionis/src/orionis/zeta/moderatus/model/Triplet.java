package orionis.zeta.moderatus.model;

public class Triplet {
	private String first;
	private String second;
	private String third;
	
	public Triplet(String first, String second, String third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public String getFirst() {
		return first;
	}
	
	public String getSecond() {
		return second;
	}
	
	public String getThird() {
		return third;
	}
	
	@Override
	public String toString() {
		return first + ", " + second + ", " + third;
	}

	@Override
	public int hashCode() {
		return first.hashCode() * 13 + second.hashCode() * 7 + third.hashCode();
	}
}
