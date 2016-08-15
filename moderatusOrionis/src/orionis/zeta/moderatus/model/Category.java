package orionis.zeta.moderatus.model;

import java.util.List;
import java.util.Map;

import orionis.zeta.moderatus.web.database.CategoryKey;
import orionis.zeta.moderatus.web.database.DatabaseItem;

public class Category implements DatabaseItem {
	private Map<CategoryKey, String> data;
	private List<Product> products;
	
	public Category(Map<CategoryKey, String> data) {
		this.data = data;
	}
	
	public void setName(String name) {
		data.put(CategoryKey.NAME, name);
	}
	
	@Override
	public String getName() {
		return data.get(CategoryKey.NAME);
	}
	
	@Override
	public String getCode() {
		return data.get(CategoryKey.CODE);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
