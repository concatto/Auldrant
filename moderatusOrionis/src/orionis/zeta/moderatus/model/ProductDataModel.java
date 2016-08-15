package orionis.zeta.moderatus.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import orionis.zeta.moderatus.view.ImageContainer;
import orionis.zeta.moderatus.web.WebServer;
import orionis.zeta.moderatus.web.database.ProductKey;

public class ProductDataModel {
	private static final int CHARACTER_LIMIT = 255;
	private static final int CHARACTER_LIMIT_NAME = 70;
	private String code;
	private String category;
	private List<Attribute> characteristics = new ArrayList<>();
	private List<Attribute> contents = new ArrayList<>();
	
	private StringProperty name;
	private StringProperty description;
	private StringProperty stock;
	private StringProperty price;
	private StringProperty discount;
	private StringProperty weight;
	private ImageContainer[] containers;
	private List<StringProperty> characteristicFields;
	private List<StringProperty> contentFields;

	public ProductDataModel(ImageContainer[] containers,
			StringProperty name, StringProperty description, StringProperty stock, StringProperty weight,
			StringProperty price, StringProperty discount,
			List<StringProperty> characteristicFields, List<StringProperty> contentFields) {
		
		this.containers = containers;
		/* General tab */
		this.name = name;
		this.description = description;
		this.stock = stock;
		this.weight = weight;
		/* Price tab */
		this.price = price;
		this.discount = discount;
		/* Variable tabs */
		this.characteristicFields = characteristicFields;
		this.contentFields = contentFields;
	}
	
	public void reset() {
		String empty = "";
		name.set(empty);
		description.set(empty);
		stock.set(empty);
		price.set(empty);
		discount.set(empty);
		weight.set(empty);
		
		for (ImageContainer container : containers) container.reset();
		characteristics.clear();
		characteristicFields.clear();
		contents.clear();
		contentFields.clear();
	}
	
	public void placeData(Product product) {
		reset();
		code = product.getCode();
		category = product.getCategory();
		characteristics.addAll(product.getCharacteristics().stream().map(Attribute::new).collect(Collectors.toList()));
		contents.addAll(product.getContents().stream().map(Attribute::new).collect(Collectors.toList()));
		
		name.set(product.getName());
		description.set(product.getDescription());
		stock.set(product.getStock());
		price.set(product.getPrice());
		discount.set(product.getDiscount());
		weight.set(product.getWeight());
		
		String[] pathArray = {product.getImagePath(), product.getExtraImage1(), product.getExtraImage2(), product.getExtraImage3(), product.getExtraImage4()};
		for (int i = 0; i < pathArray.length; i++) {
			if (pathArray[i] == null) {
				containers[i].reset();
			} else {
				containers[i].setImageFromURL(WebServer.encodeURL(pathArray[i]));
			}
		}
		
		for (Attribute characteristic : characteristics) {
			characteristicFields.add(new SimpleStringProperty(characteristic.getValue()));
		}
		
		for (Attribute content : contents) {
			contentFields.add(new SimpleStringProperty(content.getValue()));
		}
	}
	
	public ImageContainer[] filterImageContainers(ImageContainer[] containers) {
		return Arrays.stream(containers).filter(t -> t.isChanged() && t.hasImage()).toArray(ImageContainer[]::new);
	}
	
	public void beginUpload(ImageContainer[] containers) throws IOException, InterruptedException {
		List<Callable<Void>> callables = Arrays.stream(containers).map(container -> {
			Callable<Void> callable = () -> {
				WebServer.upload(new File(new URI(container.getPath())));
				return null;
			};
			return callable;
		}).collect(Collectors.toList());
		
		try {
			List<Future<Void>> results = Executors.newCachedThreadPool().invokeAll(callables);
			for (Future<Void> future : results) future.get();
		} catch (ExecutionException e) {
			throw new IOException(e.getCause().getLocalizedMessage(), e.getCause());
		}
	}
	
	public Product getData() throws IllegalStateException {
		Map<ProductKey, String> data = new HashMap<>();
		
		if (code != null) data.put(ProductKey.CODE, code);
		data.put(ProductKey.CATEGORY, category);
		data.put(ProductKey.NAME, name.get());
		data.put(ProductKey.DESCRIPTION, description.get());
		data.put(ProductKey.STOCK, stock.get());
		data.put(ProductKey.PRICE, price.get());
		data.put(ProductKey.DISCOUNT, discount.get());
		data.put(ProductKey.WEIGHT, weight.get());
		
		Pattern pattern = Pattern.compile("\\d+(\\,\\d+)?");
		data.forEach((key, value) -> {
			if (value != null && pattern.matcher(value).matches()) {
				value = value.replaceAll(",", ".");
				data.put(key, value);
			}
		});
		
		ProductKey[] imageKeys = {
				ProductKey.MAIN_IMAGE,
				ProductKey.EXTRA_IMAGE_1,
				ProductKey.EXTRA_IMAGE_2,
				ProductKey.EXTRA_IMAGE_3,
				ProductKey.EXTRA_IMAGE_4
		};
		for (int i = 0; i < containers.length; i++) {
			String imageName = containers[i].getImageName();
			data.put(imageKeys[i], imageName == null ? "null" : imageName);
		}
		
		Function<StringProperty, Attribute> function = property -> {
			if (property == null) return null;
			return new Attribute(property.get());
		};
		
		List<Attribute> characteristics = characteristicFields.stream().map(function).collect(Collectors.toList());
		List<Attribute> content = contentFields.stream().map(function).collect(Collectors.toList());
		
		Product product = new Product(data, characteristics, content);
		validateProduct(product);
		return product;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	private void validateProduct(Product product) throws IllegalStateException {
		Map<ProductKey, String> data = product.getData();
		
		/* If any value is empty or longer than 255 characters (except for Description). */
		data.forEach((k, v) -> {
			/* If any value is empty */
			/* Short-circuit is important */
			if (v == null || v.isEmpty()) {
				throw new IllegalStateException("Todos os campos devem ser preenchidos.");
			}
			/* If any field has more than 255 characters (except for Description) */
			if (v.length() > CHARACTER_LIMIT && !k.equals(ProductKey.DESCRIPTION)) {
				throw new IllegalStateException("Nenhum campo pode possuir mais de " + CHARACTER_LIMIT + " caracteres (exceto a Descrição).");
			}
			/* If the Name has more than 70 characters */
			if (k.equals(ProductKey.NAME) && v.length() > CHARACTER_LIMIT_NAME) {
				throw new IllegalStateException("O nome do produto não pode possuir mais de " + CHARACTER_LIMIT_NAME + " caracteres.");
			}
			/* If the Main Image is not set */
			if (k.equals(ProductKey.MAIN_IMAGE) && v.equals("null")) {
				throw new IllegalStateException("É obrigatória a presença da imagem principal.");
			}
		});
		
		/* If the main image is not set */
		if (data.get(ProductKey.MAIN_IMAGE) == "null") {
			throw new IllegalStateException("É obrigatória a presença da imagem principal.");
		}
		
		
	}
}
