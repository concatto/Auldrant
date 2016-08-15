package br.concatto.socket;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.json.JSONObject;

public class CommandCollector implements Collector<Command, JSONObject, JSONObject> {
	@Override
	public Supplier<JSONObject> supplier() {
		return JSONObject::new;
	}

	@Override
	public BiConsumer<JSONObject, Command> accumulator() {
		return (json, elemento) -> {
			try {
				json.put(String.valueOf(elemento.getCode()), URLEncoder.encode(elemento.getDescription(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		};
	}

	@Override
	public BinaryOperator<JSONObject> combiner() {
		return (a, b) -> {
			for (String key : a.keySet()) {
				b.put(key, a.get(key));
			}
			return b;
		};
	}

	@Override
	public Function<JSONObject, JSONObject> finisher() {
		return null;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return EnumSet.of(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
	}
}