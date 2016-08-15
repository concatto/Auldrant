package orionis.zeta.moderatus.model;

import java.util.List;
import java.util.Map;
/* A static import of ProductKey could be used here. */



import orionis.zeta.moderatus.web.database.DatabaseItem;
import orionis.zeta.moderatus.web.database.ProductKey;

public class Product implements DatabaseItem {
	private Map<ProductKey, String> data;
	private List<Attribute> characteristics;
	private List<Attribute> contents;
	
	public Product(Map<ProductKey, String> data, List<Attribute> characteristics) {
		this.data = data;
		this.characteristics = characteristics;
	}
	
	public Product(Map<ProductKey, String> data, List<Attribute> characteristics, List<Attribute> contents) {
		this.data = data;
		this.characteristics = characteristics;
		this.contents = contents;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public Map<ProductKey, String> getData() {
		return data;
	}

	@Override
	public String getCode() {
		return data.get(ProductKey.CODE);
	}
	
	@Override
	public String getName() {
		return data.get(ProductKey.NAME);
	}

	public String getDescription() {
		return data.get(ProductKey.DESCRIPTION);
	}

	public String getPrice() {
		return data.get(ProductKey.PRICE);
	}

	public String getImagePath() {
		return data.get(ProductKey.MAIN_IMAGE);
	}

	public String getCategory() {
		return data.get(ProductKey.CATEGORY);
	}

	public String getStock() {
		return data.get(ProductKey.STOCK);
	}

	public String getAmountSold() {
		return data.get(ProductKey.AMOUNT_SOLD);
	}

	public String getWeight() {
		return data.get(ProductKey.WEIGHT);
	}

	public String getDiscount() {
		return data.get(ProductKey.DISCOUNT);
	}

	public String getExtraImage1() {
		return data.get(ProductKey.EXTRA_IMAGE_1);
	}

	public String getExtraImage2() {
		return data.get(ProductKey.EXTRA_IMAGE_2);
	}
	
	public String getExtraImage3() {
		return data.get(ProductKey.EXTRA_IMAGE_3);
	}
	
	public String getExtraImage4() {
		return data.get(ProductKey.EXTRA_IMAGE_4);
	}
	
	public List<Attribute> getCharacteristics() {
		return characteristics;
	}
	
	public List<Attribute> getContents() {
		return contents;
	}
}
