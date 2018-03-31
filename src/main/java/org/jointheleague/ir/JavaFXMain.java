package org.jointheleague.ir;

import javax.swing.UIManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JavaFXMain extends Application {
	static {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			Program.exit(throwable);
		});

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Program.exit(e);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Thread thread = new Thread(() -> {
			Main.main();
		});
		thread.setName(Program.APPLICATION_INTERNAL + "-MainThread");
		thread.start();
	}

	public static void main(String[] args) {
		Platform.setImplicitExit(false);
		Application.launch(JavaFXMain.class, new String[0]);
	}
}
