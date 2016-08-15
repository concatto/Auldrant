package orionis.zeta.moderatus.web.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import orionis.zeta.moderatus.model.Triplet;

public class Database {
	private static final String PRODUCTS_TABLE = "Produtos";
	private static final String CATEGORIES_TABLE = "Categorias";
	private Connection connection;
	
	public Database(String password) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		connection = DriverManager.getConnection("jdbc:mysql://localhost/Cardoso", "root", password);
	}
	
	public List<Map<CategoryKey,String>> queryCategories() throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(formatQuery("select %s, %s from %s", CategoryKey.CODE, CategoryKey.NAME, CATEGORIES_TABLE));
		return parseResultSet(result, CategoryKey.values());
	}
	
	public List<Map<ProductKey, String>> queryProducts(String categoryCode) throws SQLException {
		String rawQuery = "select %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s inner join %s on %s = %s";
		/* If the category parameter is not null, adds a "where" clause */
		if (categoryCode != null) rawQuery += formatQuery(" where %s.%s = %s", CATEGORIES_TABLE, CategoryKey.CODE, categoryCode);
		
		ProductKey[] keys = {
				ProductKey.CODE,
				ProductKey.NAME,
				ProductKey.DESCRIPTION,
				ProductKey.PRICE,
				ProductKey.DISCOUNT,
				ProductKey.CATEGORY,
				ProductKey.STOCK,
				ProductKey.AMOUNT_SOLD,
				ProductKey.WEIGHT,
				ProductKey.MAIN_IMAGE,
				ProductKey.EXTRA_IMAGE_1,
				ProductKey.EXTRA_IMAGE_2,
				ProductKey.EXTRA_IMAGE_3,
				ProductKey.EXTRA_IMAGE_4
		};
		
		Object[] extras = {PRODUCTS_TABLE, CATEGORIES_TABLE, ProductKey.CATEGORY, CategoryKey.CODE};
		
		Object[] elements = Stream.concat(Arrays.stream(keys), Arrays.stream(extras)).toArray();
		String query = formatQuery(rawQuery, elements);
		
		ResultSet result = connection.createStatement().executeQuery(query);
		return parseResultSet(result, keys);
	}
	
	public List<Triplet> queryCharacteristics() throws SQLException {
		return queryAttributes(AttributeHeader.CHARACTERISTIC);
	}
	
	public List<Triplet> queryContents() throws SQLException {
		return queryAttributes(AttributeHeader.CONTENT);
	}
	
	private List<Triplet> queryAttributes(AttributeHeader type) throws SQLException {
		String query = String.format("select %s, %s, %s from %s", type.getCode(), type.getProduct(), type.getValue(), type.getTable());
		ResultSet result = connection.createStatement().executeQuery(query);
		return parseTriplet(result);
	}
	
	private List<Triplet> parseTriplet(ResultSet result) throws SQLException {
		List<Triplet> list = new ArrayList<>();
		while (result.next()) {
			Triplet triplet = new Triplet(result.getString(1), result.getString(2), result.getString(3));
			list.add(triplet);
		}
		return list;
	}
	
	public List<Map<ProductKey, String>> queryProducts() throws SQLException {
		return queryProducts(null);
	}
	
	/* Receives and returns both CategoryKey and ProductKey. Uses a lot of generics. */
	private <E extends DatabaseKey> List<Map<E, String>> parseResultSet(ResultSet result, E[] keys) throws SQLException {
		List<Map<E, String>> list = new ArrayList<>();
		while (result.next()) {
			Map<E, String> map = new HashMap<>();
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], result.getString(i + 1));
			}
			list.add(map);
		}
		return list;
	}
	
	private String formatQuery(String string, Object... elements) {
		Object[] test = Arrays.stream(elements).map(value -> {
			if (value instanceof DatabaseKey) {
				return ((DatabaseKey) value).getValue();
			}
			return value;
		}).toArray();
		
		return String.format(string, test);
	}

	public void executeCategoryRename(String newName, String code) throws SQLException {
		String query = formatQuery("update %s set %s = '%s' where %s = %s", CATEGORIES_TABLE, CategoryKey.NAME, newName, CategoryKey.CODE, code);
		connection.createStatement().execute(query);
	}

	public void executeCategoryCreation(String name) throws SQLException {
		String query = formatQuery("insert into %s (%s) values (?)", CATEGORIES_TABLE, CategoryKey.NAME);
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, name);
		statement.execute();
	}

	public void executeCategoryRemoval(String code) throws SQLException {
		String query = formatQuery("delete from %s where %s = %s", CATEGORIES_TABLE, CategoryKey.CODE, code);
		connection.createStatement().execute(query);
	}

	public void executeProductRemoval(String code) throws SQLException {
		String query = formatQuery("delete from %s where %s = %s", PRODUCTS_TABLE, CategoryKey.CODE, code);
		connection.createStatement().execute(query);
	}

	public void executeProductCreation(Map<ProductKey, String> data) throws SQLException {
		String firstStart = "insert into " + PRODUCTS_TABLE + "(";
		String secondStart = ") values(";
		StringBuilder firstBuilder = new StringBuilder(firstStart);
		StringBuilder secondBuilder = new StringBuilder(secondStart);
		data.forEach((k, v) -> {
			/* if null, skip */
			if (v.equals("null")) return;
			appendIfNotFirst(firstBuilder, firstStart, ", ");
			firstBuilder.append(k.getValue());
			appendIfNotFirst(secondBuilder, secondStart, ", ");
			secondBuilder.append("'").append(v).append("'");
		});
		firstBuilder.append(secondBuilder).append(")");
		/* Result: insert into PRODUCTS_TABLE (keys of "data") values (values of "data") */ 
		connection.createStatement().execute(firstBuilder.toString());
	}
	
	public void executeProductEdition(Map<ProductKey, String> data, String code) throws SQLException {
		String initial = "update " + PRODUCTS_TABLE + " set ";
		StringBuilder query = new StringBuilder(initial);
		for (ProductKey key : data.keySet()) {
			appendIfNotFirst(query, initial, ", ");
			query.append(key.getValue()).append(" = ?");
		}
		
		query.append(formatQuery(" where %s = %s", ProductKey.CODE, code));
		PreparedStatement statement = connection.prepareStatement(query.toString());
		
		int count = 1;
		for (String value : data.values()) {
			if (!value.equals("null")) {
				statement.setString(count, value);
			} else {
				statement.setNull(count, Types.VARCHAR);
			}
			count++;
		}
		
		statement.execute();
	}
	
	public void executeAttributeEdition(Map<String, String> codesValues, AttributeHeader type) throws SQLException {
		String initial = formatQuery("update %s set %s = case %s", type.getTable(), type.getValue(), type.getCode());
		StringBuilder builder = new StringBuilder(initial);
		for (int i = 0; i < codesValues.size(); i++) {
			builder.append(" when ? then ?");
		}
		builder.append(" end where ").append(type.getCode()).append(" in (");
		
		String breakpoint = builder.toString();
		for (String key : codesValues.keySet()) {
			appendIfNotFirst(builder, breakpoint, ", ");
			builder.append(key);
		}
		builder.append(")");
		
		PreparedStatement statement = connection.prepareStatement(builder.toString());
		int index = 1;
		for (Entry<String, String> entry : codesValues.entrySet()) {
			statement.setString(index++, entry.getKey());
			statement.setString(index++, entry.getValue());
		}
		statement.execute();
	}
	
	public void executeAttributeRemoval(List<String> codes, AttributeHeader type) throws SQLException {
		String initial = formatQuery("delete from %s where %s in (", type.getTable(), type.getCode());
		StringBuilder builder = new StringBuilder(initial);
		for (String code : codes) {
			appendIfNotFirst(builder, initial, ", ");
			builder.append(code);
		}
		builder.append(")");
		
		connection.createStatement().execute(builder.toString());
	}
	
	public void executeAttributeCreation(List<String> values, String productCode, AttributeHeader type) throws SQLException {
		String initial = formatQuery("insert into %s (%s, %s) values ", type.getTable(), type.getProduct(), type.getValue());
		StringBuilder builder = new StringBuilder(initial);
		for (int i = 0; i < values.size(); i++) {
			appendIfNotFirst(builder, initial, ", ");
			builder.append("(").append(productCode).append(", ?)");
		}
		
		PreparedStatement statement = connection.prepareStatement(builder.toString());
		int index = 1;
		for (String value : values) {
			statement.setString(index++, value);
		}
		statement.execute();
	}
	
	public List<String> getMergedProductImages() throws SQLException {
		ProductKey[] keys = {ProductKey.MAIN_IMAGE, ProductKey.EXTRA_IMAGE_1, ProductKey.EXTRA_IMAGE_2, ProductKey.EXTRA_IMAGE_3, ProductKey.EXTRA_IMAGE_4};
		StringBuilder builder = new StringBuilder();
		for (ProductKey productKey : keys) {
			String key = productKey.getValue();
			appendIfNotFirst(builder, " union ");
			/* select Key from Table where Key is not null */
			builder.append("select ").append(key).append(" from ").append(PRODUCTS_TABLE).append(" where ").append(key).append(" is not null");
		}
		
		ResultSet result = connection.createStatement().executeQuery(builder.toString());
		List<String> list = new ArrayList<>();
		
		while (result.next()) list.add(result.getString(1));
		
		return list;
	}
	
	public List<String[]> getProductImagesAndCode() throws SQLException {
		String query = formatQuery("select %s, %s, %s, %s, %s from %s",
				ProductKey.CODE,
				ProductKey.EXTRA_IMAGE_1,
				ProductKey.EXTRA_IMAGE_2,
				ProductKey.EXTRA_IMAGE_3,
				ProductKey.EXTRA_IMAGE_4, PRODUCTS_TABLE);
		
		ResultSet result = connection.createStatement().executeQuery(query);
		
		List<String[]> list = new ArrayList<>();
		int limit = result.getMetaData().getColumnCount();
		while (result.next()) {
			String[] array = new String[limit];
			for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
				array[i] = result.getString(i + 1);
			}
			list.add(array);
		}
		
		return list;
	}
	
	/**
	 * Changes every element in the database matched by the Map to null.
	 * @param imageIndexKeyMap a Map where keys are the indeces of the Extra Images and values are Lists of their Codes.
	 */
	public void nullifyExtraImages(Map<Integer, List<String>> imageIndexKeyMap) throws SQLException {
		String code = ProductKey.CODE.getValue();
		String extra = ProductKey.EXTRA_IMAGE_1.getValue();
		extra = extra.substring(0, extra.length() - 1);
		
		String start = "update " + PRODUCTS_TABLE + " set ";
		StringBuilder builder = new StringBuilder(start);
		
		int parameterCount = 0;
		for (Integer imageIndex : imageIndexKeyMap.keySet()) {
			appendIfNotFirst(builder, start, ", ");
			builder.append(formatQuery("%s%d = case %s", extra, imageIndex, code));
			
			for (String primaryKey : imageIndexKeyMap.get(imageIndex)) {
				builder.append(formatQuery(" when %s then ? ", primaryKey));
				parameterCount++;
			}
			
			builder.append(formatQuery("else %s%d end", extra, imageIndex));
		}
		
		PreparedStatement statement = connection.prepareStatement(builder.toString());
		for (int i = 0; i < parameterCount; i++) {
			statement.setNull(i + 1, Types.VARCHAR);
		}
		
		statement.execute();
	}
	
	public DatabaseMetaData getMetaData() throws SQLException {
		return connection.getMetaData();
	}
	
	private static void appendIfNotFirst(StringBuilder builder, String toAppend) {
		appendIfNotFirst(builder, "", toAppend);
	}
	
	private static void appendIfNotFirst(StringBuilder builder, String breakpoint, String toAppend) {
		if (builder.length() > breakpoint.length()) builder.append(toAppend);
	}
}
