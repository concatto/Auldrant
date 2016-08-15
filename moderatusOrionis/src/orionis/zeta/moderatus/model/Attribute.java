package orionis.zeta.moderatus.model;

public class Attribute {
	private String code;
	private String value;
	
	public Attribute(Attribute characteristic) {
		this.code = characteristic.code;
		this.value = characteristic.value;
	}
	
	public Attribute(String code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public Attribute(String value) {
		this.value = value;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return (code == null ? "<no code>" : code) + " = " + value;
	}
}
