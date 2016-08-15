package orionis.delta.experimentum.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Invoker {
	public static void main(String[] args) {
		List<Callable<Void>> tasks = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			int value = i;
			tasks.add(() -> {
				Thread.sleep(500 + (1 * 300));
				System.out.println("Thread " + value + " has finished");
				return null;
			});
		}
		System.out.println("started");
		try {
			for (Future<Void> f : Executors.newCachedThreadPool().invokeAll(tasks)) {
				System.out.println(f);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("finished");
	}
}
