package org.jointheleague.ir;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JavaFXMain extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Main.main(new String[0]);
	}

	public static void main(String[] args) {
		Platform.setImplicitExit(false);
		Application.launch(JavaFXMain.class, new String[0]);
	}
}
