package orionis.epsilon.reflexionem.controller;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.stage.Stage;
import orionis.epsilon.reflexionem.model.ClassHandler;
import orionis.epsilon.reflexionem.view.MainStage;

public class Controller extends Application {
	private ClassHandler classHandler = new ClassHandler();
	private MainStage mainStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = new MainStage();
		mainStage.setOnSearchClass(theClass -> mainStage.setClasses(classHandler.searchClass(theClass)));
		mainStage.setOnClassSelection(selectedClass -> mainStage.setMethods(classHandler.listMethods(selectedClass)));
		mainStage.setOnMethodSelection(selectedMethod -> {
			if (selectedMethod != null) {
				mainStage.setInvocationInformation(selectedMethod.getDeclaringClass().getName(), selectedMethod.getName());
				mainStage.setInvocationMessage(messageFromMethodModifier(selectedMethod.getModifiers()));
				Class<?> reference = selectedMethod.getDeclaringClass();
				if (Modifier.isStatic(reference.getModifiers())) reference = null;
				mainStage.setInvocationParameters(reference, Arrays.asList(selectedMethod.getParameterTypes()));
			} else {
				mainStage.setInvocationInformation(null, null);
				mainStage.setInvocationMessage("Nenhum método selecionado.");
				mainStage.setInvocationParameters(void.class, new ArrayList<>());
			}
		});
		
		mainStage.setOnInvocation((method, parameters) -> {
			try {
				Object result = method.invoke(mainStage.getReference(), parameters.toArray());
				if (result != null) {
					boolean contains = false;
					for (Object object : mainStage.getInstances().values()) {
						if (object.equals(result)) {
							contains = true;
							break;
						} 
					}
					if (!contains) System.out.println("A invocação retornou um Objeto do tipo " + result.getClass().getSimpleName() + ". Deseja mantê-lo ou descartá-lo?");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		mainStage.setOnPrimitiveCreation((primitive, value) -> {
			Class<?> theClass = primitive.getSimpleName().equals("String") ? Object.class : String.class;
			try {
				mainStage.addInstance(value, primitive.getMethod("valueOf", theClass).invoke(null, value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		mainStage.setOnInstantiation(theClass -> {
			try {
				mainStage.addInstance(theClass.getSimpleName(), theClass.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		mainStage.setPrimitives(classHandler.getPrimitives());
		mainStage.show();
	}
	
	private String messageFromMethodModifier(int modifier) {
		String message;
		if (Modifier.isPrivate(modifier)) {
			message = "Este método é private.\nNão é possível invocá-lo.";
		} else if (Modifier.isPublic(modifier)) {
			if (Modifier.isStatic(modifier)) {
				message = "Este método é static.\nÉ possível invocá-lo.";
			} else {
				message = "Este método é public.\nÉ necessário uma instância da classe para invocá-lo.";
			}
		} else {
			message = "Este método não está disponível.";
		}
		return message;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
