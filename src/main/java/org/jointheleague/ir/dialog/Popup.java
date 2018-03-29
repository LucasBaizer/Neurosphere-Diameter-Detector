package org.jointheleague.ir.dialog;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JDialog;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jointheleague.ir.Main;
import org.jointheleague.ir.Program;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class Popup {
	public static void create(String title, String url) {
		Parser parser = Parser.builder().build();
		Node node = null;
		try {
			node = parser.parse(new String(Files.readAllBytes(Program.getAsset("web/content/" + url + ".md").toPath()),
					StandardCharsets.UTF_8));
		} catch (IOException e) {
			Program.exit(e);
		}
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		String html = renderer.render(node);

		JFXPanel panel = new JFXPanel();

		JDialog dialog = new JDialog(Main.FRAME, title);
		dialog.getContentPane().add(panel);

		Platform.runLater(() -> {
			WebView webView = new WebView();
			panel.setScene(new Scene(webView));

			webView.getEngine()
					.loadContent("<html><head><link rel=\"stylesheet\" href=\"file:///"
							+ Program.getAsset("web/resources/bootstrap.min.css").getAbsolutePath().replace('\\', '/')
							+ " \"</head><body>" + html + "</body></html>");
			webView.getEngine().getLoadWorker().stateProperty().addListener(new WebViewRedirectListener(webView));

			dialog.setVisible(true);
			dialog.pack();
		});
	}

	private static class WebViewRedirectListener implements ChangeListener<Worker.State>, EventListener {
		private WebView webView;

		public WebViewRedirectListener(WebView webView) {
			this.webView = webView;
		}

		@Override
		public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue,
				Worker.State newValue) {
			if (newValue.equals(Worker.State.SUCCEEDED)) {
				Document document = webView.getEngine().getDocument();
				NodeList anchors = document.getElementsByTagName("a");
				for (int i = 0; i < anchors.getLength(); i++) {
					org.w3c.dom.Node node = anchors.item(i);
					EventTarget eventTarget = (EventTarget) node;
					eventTarget.addEventListener("click", this, false);
				}
			}
		}

		@Override
		public void handleEvent(Event event) {
			HTMLAnchorElement anchorElement = (HTMLAnchorElement) event.getCurrentTarget();
			String href = anchorElement.getHref();

			try {
				Desktop.getDesktop().browse(new URL(href).toURI());
			} catch (IOException | URISyntaxException e) {
				Program.exit(e);
			}

			event.preventDefault();
		}
	}
}
