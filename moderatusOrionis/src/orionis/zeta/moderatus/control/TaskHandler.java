package orionis.zeta.moderatus.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import orionis.zeta.moderatus.model.ProgressionStatus;
import orionis.zeta.moderatus.view.Dialogs;
import orionis.zeta.moderatus.view.ProgressStage;
import orionis.zeta.moderatus.view.ProgressiveStage;
import orionis.zeta.moderatus.web.WebServer;
import orionis.zeta.moderatus.web.database.Database;
import orionis.zeta.moderatus.web.database.DatabaseItem;
import orionis.zeta.moderatus.web.database.DatabaseTask;

public class TaskHandler {
	private ExecutorService executor = Executors.newCachedThreadPool();
	private ProgressStage progress;
	private Database database;
	private Stage owner;

	public TaskHandler(Database database, Stage owner) {
		this.database = database;
		this.owner = owner;
		
		progress = new ProgressStage(owner);
	}
	
	public void createDatabaseTask(String message, DatabaseTask onStart, Runnable onFinish, String errorMessage) {
		createDatabaseTask(message, onStart, onFinish, owner, errorMessage);
	}
	
	public void createDatabaseTask(String message, DatabaseTask onStart, Runnable onFinish, Stage errorOwner, String errorMessage) {
		createDatabaseTask(message, Collections.singletonList(onStart), onFinish, errorOwner, errorMessage);
	}
	
	/**
	 * Creates an asynchronous task, displaying an indeterminate Progress Indicator with <i>message</i> as a Label.
	 * Meant to be used with Database queries.
	 * @param message the message to be displayed
	 * @param onStart the Runnable that represents the task
	 * @param onFinish the Runnable that runs when the task is finished successfully
	 * @param errorOwner owner of the error dialog
	 * @param errorMessage message of the error dialog
	 */
	public void createDatabaseTask(String message, List<DatabaseTask> tasks, Runnable onFinish, Stage errorOwner, String errorMessage) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				/* Map DatabaseTask to Callable<Void> */
				List<Callable<Void>> callables = tasks.stream().map(t -> {
					Callable<Void> c = () -> {
						t.execute();
						return null;
					};
					return c;
				}).collect(Collectors.toList());
				
				List<Future<Void>> futures = executor.invokeAll(callables);
				/* Check for exceptions */
				for (Future<Void> future : futures) future.get();
				return null;
			}
		};
		
		task.setOnScheduled(e -> progress.start(message));
		
		task.setOnSucceeded(e -> {
			progress.finish();
			onFinish.run();
		});
		
		task.setOnFailed(e -> {
			progress.finish();
			Dialogs.showError(errorOwner, errorMessage, task.getException().getCause());
		});
		
		executor.submit(task);
	}
	
	public <T extends DatabaseItem> void createUpdate(String message, Callable<List<T>> onStart, Consumer<List<T>> onFinish) {
		Task<List<T>> task = new Task<List<T>>() {
			@Override
			protected List<T> call() throws Exception {
				return onStart.call();
			}
		};
		
		task.setOnSucceeded(e -> {
			Platform.runLater(() -> {
				try {
					onFinish.accept(task.get());
				} catch (Exception e1) {
					Dialogs.showError(owner, "Erro ao obter os resultados da consulta ao Banco de Dados.", e1);
				}
				progress.finish();
			});
		});
		
		task.setOnFailed(e -> {
			progress.finish();
			Platform.runLater(() -> Dialogs.showError(owner, "Erro ao consultar o Banco de Dados.", task.getException()));
		});
		
		task.setOnScheduled(t -> progress.start(message));
		
		executor.submit(task);
	}

	public void cleanServer() {
		Task<Boolean> task = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				updateMessage("Obtendo imagens do Banco de Dados...");
				List<String> databaseImages = database.getMergedProductImages();
				
				updateMessage("Obtendo imagens do Servidor...");
				List<String> serverImages = WebServer.getProductImages();
				
				if (isCancelled()) return null;
				updateValue(false);
				ListIterator<String> iterator = serverImages.listIterator();
				while (iterator.hasNext()) {
					/* Removes elements from serverImages that exist in databaseImages */
					if (databaseImages.contains(iterator.next())) {
						iterator.remove();
					}
				}
				
				if (!serverImages.isEmpty()) {
					updateMessage("Removendo imagens inutilizadas do Servidor...");
					String result = WebServer.removeFromServer(serverImages);
					updateMessage(String.format("%d imagens inutilizadas foram encontradas.\n%s foram removidas com sucesso.", serverImages.size(), result));
				} else {
					updateMessage("Nenhuma imagem inutilizada foi encontrada no servidor.");
				}
				
				return null;
			}
		};
		
		displayProgressiveTask(task);
	}
	
	public void cleanDatabase() {
		Task<Boolean> task = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				updateMessage("Obtendo imagens do Banco de Dados...");
				List<String> images = WebServer.getProductImages();
				
				updateMessage("Obtendo imagens do Servidor...");
				List<String[]> data = database.getProductImagesAndCode();
				
				if (isCancelled()) return null;
				updateValue(false);
				
				Map<Integer, List<String>> map = new HashMap<>();
				for (int i = 1; i < data.get(0).length; i++) {
					List<String> codes = new ArrayList<>();
					for (int j = 0; j < data.size(); j++) {
						String[] columns = data.get(j);
						String value = columns[i];
						if (value != null && !images.contains(value)) {
							codes.add(columns[0]);
						}
					}
					if (!codes.isEmpty()) map.put(i, codes);
				}
				
				if (!map.isEmpty()) {
					updateMessage("Removendo imagens inutilizadas do Banco de Dados...");
					database.nullifyExtraImages(map);
					updateMessage("Limpeza das imagens do Banco de Dados concluída com sucesso.");
				} else {
					updateMessage("Nenhuma imagem inutilizada foi encontrada no Banco de Dados.");
				}
				
				return null;
			}
		};
		
		displayProgressiveTask(task);
	}
	
	/**
	 * Displays a Task<Boolean> that progressively updates its message through updateMessage.
	 * The Task might invoke updateValue(false) to disable the action button (usually "Cancel").
	 * Once disabled, the button will only be activated again when the Task finishes, either by success or failure.
	 * @param task the Task to be executed
	 */
	public void displayProgressiveTask(Task<Boolean> task) {
		ProgressiveStage progression = new ProgressiveStage(owner);
		progression.bindMessage(task.messageProperty());
		progression.start("Inicializando...", () -> task.cancel(true));
		
		task.setOnSucceeded(e -> {
			progression.finish(ProgressionStatus.SUCCEEDED);
		});
		
		task.setOnCancelled(e -> {
			progression.unbindMessage();
			progression.setMessage("Limpeza cancelada.");
			progression.finish(ProgressionStatus.CANCELLED);
		});
		
		task.setOnFailed(e -> {
			Dialogs.showError(owner, "Um erro ocorreu ao tentar acessar o Banco de Dados ou o Servidor.", task.getException());
			progression.unbindMessage();
			progression.setMessage("Falha na limpeza do Servidor.");
			progression.finish(ProgressionStatus.FAILED);
		});
		
		task.valueProperty().addListener((obs, old, newValue) -> {
			if (newValue != null && newValue == false) progression.changeActionStatus(false);
		});
		
		executor.submit(task);
	}
	
	public void terminate() {
		if (!executor.isShutdown()) executor.shutdown();
	}
}
