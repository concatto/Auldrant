package orionis.epsilon.reflexionem.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassHandler {
	public ClassHandler() {
		
	}
	
	public List<Class<?>> searchClass(String className) {
		List<Class<?>> classes = new ArrayList<>();
		try {
			classes.add(Class.forName(className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classes;
	}

	public List<Method> listMethods(Class<?> selectedClass) {
		return selectedClass != null ? Arrays.asList(selectedClass.getDeclaredMethods()) : new ArrayList<>();
	}
	
	public List<Class<?>> getPrimitives() {
		List<Class<?>> primitives = new ArrayList<>();
		primitives.add(Byte.class);
		primitives.add(Short.class);
		primitives.add(Integer.class);
		primitives.add(Long.class);
		primitives.add(Float.class);
		primitives.add(Double.class);
		primitives.add(Boolean.class);
		primitives.add(String.class);
		return primitives;
	}
}
