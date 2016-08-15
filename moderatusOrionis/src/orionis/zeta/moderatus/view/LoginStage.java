package orionis.zeta.moderatus.view;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import orionis.zeta.moderatus.model.ThrowableConsumer;

public class LoginStage extends Stage {
	private Label label = new Label("Senha");
	private PasswordField password = new PasswordField();
	private VBox passwordContainer = new VBox(5, label, password);
	private Button confirm = new Button("OK");
	private Button cancel = new Button("Cancelar");
	private HBox buttons = new HBox(7, confirm, cancel);
	private VBox root = new VBox(16, passwordContainer, buttons);
	private Scene scene = new Scene(root);
	private ProgressStage loginProgress = new ProgressStage(this);
	private ThrowableConsumer<String> action;
	private Runnable success;
	
	public LoginStage() {
		root.setPadding(new Insets(8, 28, 14, 28));
		root.setAlignment(Pos.CENTER);
		buttons.setAlignment(Pos.CENTER_RIGHT);
		confirm.prefWidthProperty().bind(cancel.widthProperty());
		
		setWidth(260);
		setTitle("Login");
		setScene(scene);
		setResizable(false);
		focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) password.requestFocus();
		});
		
		EventHandler<ActionEvent> handler = e -> {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					loginProgress.start("Conectando...");
					action.accept(password.getText());
					return null;
				}
			};
			
			task.setOnSucceeded(evt -> {
				loginProgress.close();
				close();
				success.run();
			});
			
			task.setOnScheduled(evt -> {
				loginProgress.start("Conectando...");
			});
			
			task.setOnFailed(evt -> {
				loginProgress.close();
				Dialogs.showError(this, "Erro ao criar a conexão com o Banco de Dados.", task.getException());
			});
			
			new Thread(task).start();
		};
		
		confirm.setOnAction(handler);
		password.setOnAction(handler);
	}
	
	public void setOnCancel(Runnable cancelAction) {
		cancel.setOnAction(e -> cancelAction.run());
	}

	public void setOnSuccess(Runnable success) {
		this.success = success;
	}

	public void setAction(ThrowableConsumer<String> action) {
		this.action = action;
	}
}