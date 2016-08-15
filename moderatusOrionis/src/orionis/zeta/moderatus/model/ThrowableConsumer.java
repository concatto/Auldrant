package orionis.zeta.moderatus.model;

@FunctionalInterface
public interface ThrowableConsumer<T> {
	public void accept(T t) throws Exception;
}
