package orionis.zeta.moderatus.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import orionis.zeta.moderatus.model.Attribute;
import orionis.zeta.moderatus.web.database.AttributeHeader;

public class AttributeDifferences {
	private List<Attribute> changedAttributes;
	private List<String> addedValues;
	private List<String> removedCodes;
	private AttributeHeader header;
	
	public AttributeDifferences(AttributeHeader header, List<Attribute> changedAttributes, List<String> addedValues, List<String> removedCodes) {
		this.header = header;
		this.changedAttributes = changedAttributes;
		this.addedValues = addedValues;
		this.removedCodes = removedCodes;
	}

	private static boolean isValid(Attribute attribute) {
		return attribute != null && attribute.getValue() != null && !attribute.getValue().isEmpty();
	}
	
	public static AttributeDifferences process(AttributeHeader header, List<Attribute> newData, List<Attribute> oldData) {
		List<Attribute> changedAttributes = new ArrayList<>();
		List<String> addedValues = new ArrayList<>();
		List<String> removedCodes = new ArrayList<>();
		int newSize = newData.size();
		int oldSize = oldData.size();
		
		for (int i = 0; i < newSize; i++) {
			Attribute newChar = newData.get(i);
			if (i < oldSize) {
				Attribute oldChar = oldData.get(i);
				/* Short-circuit is very important here */
				if (!isValid(newChar)) {
					removedCodes.add(oldChar.getCode());
				} else if (!newChar.getValue().equals(oldChar.getValue())) {
					changedAttributes.add(new Attribute(oldChar.getCode(), newChar.getValue()));
				}
			} else if (isValid(newChar)) {
				addedValues.add(newChar.getValue());
			}
		}
		
		if (oldSize > newSize) {
			for (Attribute c : oldData.subList(newSize, oldSize)) {
				removedCodes.add(c.getCode());
			}
		}
		
		return new AttributeDifferences(header, changedAttributes, addedValues, removedCodes);
	}
	
	public Map<String, String> getChangedAttributesAsMap() {
		return attributesToMap(changedAttributes);
	}
	
	public List<Attribute> getChangedAttributes() {
		return changedAttributes;
	}
	
	public List<String> getAddedValues() {
		return addedValues;
	}
	
	public List<String> getRemovedCodes() {
		return removedCodes;
	}
	
	public boolean hasRemoved() {
		return !removedCodes.isEmpty();
	}
	
	public boolean hasAdded() {
		return !addedValues.isEmpty();
	}
	
	public boolean hasChanged() {
		return !changedAttributes.isEmpty();
	}
	
	public AttributeHeader getHeader() {
		return header;
	}
	
	public static Map<String, String> attributesToMap(List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			if (attribute.getCode() == null) throw new IllegalArgumentException("Todos os Attributes devem possuir Code.");
		}
		return attributes.stream().collect(Collectors.toMap(Attribute::getCode, Attribute::getValue));
	}
}
