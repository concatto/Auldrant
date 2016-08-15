package br.univali.pushsynth.ui;

import java.util.prefs.Preferences;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SynthScene extends Scene {
	private static final String CHAVE_MODO = "modo_teclas";	
	private Modo modo;
	private Preferences prefs;
	
	private HBox sustenidas;
	private HBox naturais;
	private MenuBar barraMenu;
	private StackPane teclas;
	private VBox afinacao;
	private Slider afinador;
	private VBox raiz;
	
	public SynthScene() {
		super(new VBox());
		raiz = (VBox) getRoot();
		teclas = new StackPane();
		
		prefs = Preferences.userNodeForPackage(SynthScene.class);
		modo = Modo.valueOf(prefs.get(CHAVE_MODO, Modo.SETE_TECLAS.toString()));
		
		inicializarMenu();
		inicializarNaturais();
		inicializarSustenidas(); 
		inicializarAfinacao();
		
		teclas.getChildren().add(naturais);
		raiz.getChildren().addAll(barraMenu, teclas, afinacao);
		
		alterarModo(modo);
	}
	
	private void inicializarAfinacao() {
		afinador = new Slider(-100, 100, 0);
		afinador.setDisable(true);
		afinador.setShowTickMarks(true);
		
		Label grave = new Label("Grave");
		Label agudo = new Label("Agudo");
		
		AnchorPane labelsAfinador = new AnchorPane(grave, agudo);
		
		AnchorPane.setLeftAnchor(grave, 4d);
		AnchorPane.setRightAnchor(agudo, 4d);
		
		afinacao = new VBox(afinador, labelsAfinador);
		afinacao.setPadding(new Insets(6, 4, 6, 4));
	}

	private void inicializarMenu() {
		barraMenu = new MenuBar();
		Menu modoMenu = new Menu("Modo");
		
		ToggleGroup modoToggle = new ToggleGroup();
		RadioMenuItem seteItem = new RadioMenuItem("Sete teclas");
		RadioMenuItem dozeItem = new RadioMenuItem("Doze teclas");
		
		seteItem.setOnAction(e -> alterarModo(Modo.SETE_TECLAS));
		seteItem.setToggleGroup(modoToggle);
		dozeItem.setOnAction(e -> alterarModo(Modo.DOZE_TECLAS));
		dozeItem.setToggleGroup(modoToggle);
		modoToggle.selectToggle(modo == Modo.SETE_TECLAS ? seteItem : dozeItem);
		
		modoMenu.getItems().addAll(seteItem, dozeItem);
		barraMenu.getMenus().add(modoMenu);
	}

	private void alterarModo(Modo modo) {
		this.modo = modo;
		prefs.put(CHAVE_MODO, modo.toString());
		
		if (modo == Modo.SETE_TECLAS) {
			teclas.getChildren().remove(sustenidas);
		} else {
			teclas.getChildren().add(sustenidas);
		}
	}

	private void inicializarNaturais() {
		naturais = new HBox(1.2);
		naturais.setPadding(new Insets(1, 0, 1.3, 0));
		naturais.setBackground(new Background(new BackgroundFill(Tecla.OUTLINE, CornerRadii.EMPTY, Insets.EMPTY)));
		
		for (int i = 0; i < 7; i++) {
			naturais.getChildren().add(new TeclaNatural());
		}
	}

	private void inicializarSustenidas() {
		sustenidas = new HBox(26);
		sustenidas.setPadding(new Insets(0, 0, 0, 33.5));
		
		for (int i = 0; i < 6; i++) {
			sustenidas.getChildren().add(new TeclaSustenida(i == 2));
		}
	}
	
	private Tecla obterTecla(int indice) {
		if (modo == Modo.SETE_TECLAS) {
			return (Tecla) naturais.getChildren().get(indice);
		} else {
			//Natural caso <= 4 e par ou > 4 e ímpar; Sustenida se <= 4 e ímpar ou > 4 e par
			HBox teclas = (indice % 2 == (indice <= 4 ? 0 : 1)) ? naturais : sustenidas;
			
			int indiceAjustado = indice / 2;
			if (indice > 4) indiceAjustado -= (indice % 2);
			
			return (Tecla) teclas.getChildren().get(indiceAjustado);
		}
	}
	
	public void pressionar(int tecla) {		
		obterTecla(tecla).pressionar();
	}
	
	public void soltar(int tecla) {
		obterTecla(tecla).soltar();
	}
	
	public void afinar(int afinacao) {
		afinador.setValue(afinacao);
	}
}
