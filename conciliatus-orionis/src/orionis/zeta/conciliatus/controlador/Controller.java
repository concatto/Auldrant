package orionis.zeta.conciliatus.controlador;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import orionis.zeta.conciliatus.modelo.Contatos;
import orionis.zeta.conciliatus.modelo.Database;
import orionis.zeta.conciliatus.modelo.Inscrito;
import orionis.zeta.conciliatus.visao.AbstractLoginStage;
import orionis.zeta.conciliatus.visao.AbstractStage;
import orionis.zeta.conciliatus.visao.ContactsLoginStage;
import orionis.zeta.conciliatus.visao.DatabaseLoginStage;
import orionis.zeta.conciliatus.visao.DefaultKey;
import orionis.zeta.conciliatus.visao.MainScene;
import orionis.zeta.conciliatus.visao.SimpleDialog;
import orionis.zeta.conciliatus.visao.Status;
import orionis.zeta.conciliatus.visao.SynchronizationStage;
import orionis.zeta.conciliatus.visao.User;

public class Controller extends Application {
	private static Controller instance;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Contatos contacts = new Contatos();
	private Database database = new Database();
	private SynchronizationStage syncStage;
	private AbstractLoginStage databaseLogin;
	private AbstractLoginStage contactsLogin;
	private MainScene mainScene;
	private Stage mainStage;
	
	public Controller() {
		super();
		
		/* Singleton */
		synchronized (Controller.class) {
			if (instance != null) throw new UnsupportedOperationException("Uma instância já existe. Utilize o método getInstance.");
			instance = this;
			mainScene = new MainScene();
			databaseLogin = new DatabaseLoginStage();
		}
	}
	
	public void connectContacts(String user, String password) {
		Task<Void> connection = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(() -> mainScene.setStatus(Status.CONNECTING));
				contacts.conectar(user, password);
				return null;
			}
			
			@Override
			protected void failed() {
				mainScene.setStatus(Status.DISCONNECTED);
				contactsLogin.finishProgress();
				SimpleDialog.showMessage(contactsLogin, "Falha na autenticação com o serviço da Google. Verifique seus dados e tente novamente.");
				contactsLogin.focusFirst();
			}
			
			@Override
			protected void succeeded() {
				contactsLogin.setDefault(DefaultKey.CONTACTS_USER, user);
				contactsLogin.setDefault(DefaultKey.CONTACTS_PASSWORD, password);
				contactsLogin.finishProgress();
				contactsLogin.close();
				
				mainScene.setUser(User.register(contacts.getUser()));
				mainScene.setStatus(Status.CONNECTED);
			}
		};
		
		executor.submit(connection);
	}
	
	public void connectDatabase(String host, String user, String password) {
		Task<Void> connection = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				database.conectar(host, user, password);
				return null;
			}
			
			@Override
			protected void failed() {
				databaseLogin.finishProgress();
				SimpleDialog.showMessage(databaseLogin, "Falha na conexão com o Banco de Dados. Verifique seus dados ou sua conexão com a Internet.");
				databaseLogin.focusFirst();
			}
			
			@Override
			protected void succeeded() {
				databaseLogin.setDefault(DefaultKey.HOST, host);
				databaseLogin.setDefault(DefaultKey.DATABASE_USER, user);
				databaseLogin.setDefault(DefaultKey.DATABASE_PASSWORD, password);
				databaseLogin.finishProgress();
				databaseLogin.close();
				
				List<Inscrito> data = database.getInscritos().stream().map(t -> new Inscrito(t[0], t[1])).collect(Collectors.toList());
				mainScene.setTableData(data);
				mainStage.show();
				mainScene.focusConnect();
			}
		};
		
		executor.submit(connection);
	}
	
	public void cleanUp() {
		executor.shutdown();
	}
	
	public static Controller getInstance() {
		return instance;
	}

	public void showContactsLogin() {
		if (contactsLogin == null) contactsLogin = new ContactsLoginStage(mainStage);
		contactsLogin.show();
	}
	
	public void startSynchronization() {
		class InsertionTask extends Task<Boolean> {
			private Inscrito target;
			public InsertionTask(Inscrito target) {
				this.target = target;
			}
			
			@Override
			protected Boolean call() throws Exception {
				return contacts.adicionarContato(target);
			}
			
			@Override
			protected void succeeded() {
				try {
					syncStage.incrementCounter(get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		
		List<Inscrito> data = mainScene.getTableData();
		syncStage = new SynchronizationStage(data.size());
		syncStage.show();
		
		data.forEach(t -> executor.submit(new InsertionTask(t)));
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		AbstractStage.applyProperties(primaryStage);
		primaryStage.setScene(mainScene);
		
		mainStage = primaryStage;
		databaseLogin.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
