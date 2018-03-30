package org.jointheleague.ir;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JDialog;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
	public static JDialog create(String title, String url) {
		JDialog dialog = new JDialog(Main.FRAME, title);

		Thread dialogThread = new Thread(() -> {
			String html = null;
			try {
				html = new String(
						Files.readAllBytes(Program
								.getAsset("web/content/" + url + "." + (url.equals("Load") ? "html" : "md")).toPath()),
						StandardCharsets.UTF_8);
			} catch (IOException e) {
				Program.exit(e);
			}
			if (!url.equals("Load")) {
				html = wrapMarkdown(html);
			}

			JFXPanel panel = new JFXPanel();

			dialog.getContentPane().add(panel);

			String finalHtml = html;
			Platform.runLater(() -> {
				WebView webView = new WebView();
				panel.setScene(new Scene(webView));

				webView.getEngine().loadContent(finalHtml);
				webView.getEngine().getLoadWorker().stateProperty().addListener(new WebViewRedirectListener(webView));

				dialog.setVisible(true);
				if (!url.equals("Load")) {
					dialog.pack();
				}
			});
		});
		dialogThread.start();

		return dialog;
	}

	private static String wrapMarkdown(String markdown) {
		return "<html><head><link rel=\"stylesheet\" href=\"file:///"
				+ Program.getAsset("web/resources/bootstrap.min.css").getAbsolutePath().replace('\\', '/')
				+ "\"</head><body style=\"margin: 10px;\">"
				+ HtmlRenderer.builder().build().render(Parser.builder().build().parse(markdown)).replace("$LOCAL",
						"file:///" + Program.getDataFolder().getAbsolutePath() + File.separator + "web" + File.separator
								+ "content")
				+ "</body></html>";
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
					Node node = anchors.item(i);
					EventTarget eventTarget = (EventTarget) node;
					eventTarget.addEventListener("click", this, false);
				}
			}
		}

		@Override
		public void handleEvent(Event event) {
			HTMLAnchorElement anchorElement = (HTMLAnchorElement) event.getCurrentTarget();
			String href = anchorElement.getHref();

			if (!href.startsWith("file:///")) {
				try {
					Desktop.getDesktop().browse(new URL(href).toURI());
				} catch (IOException | URISyntaxException e) {
					Program.exit(e);
				}
			} else {
				try {
					webView.getEngine().loadContent(
							wrapMarkdown(new String(Files.readAllBytes(Paths.get(href.substring("file:///".length()))),
									StandardCharsets.UTF_8)));
					webView.getEngine().reload();
				} catch (IOException e) {
					Program.exit(e);
				}
			}

			event.preventDefault();
		}
	}
}
