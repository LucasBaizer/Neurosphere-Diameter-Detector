package org.jointheleague.ir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bytedeco.javacpp.opencv_core.IplImage;

public class UIPanel extends JPanel {
	private static final long serialVersionUID = 5677751569635467335L;

	private static UIPanel instance;
	private ImageComponent ic;
	private JLabel imageSizeMicrometers;
	private JLabel outputAverage;
	private JButton detectButton;

	public static UIPanel getInstance() {
		return instance;
	}

	public UIPanel() {
		instance = this;

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0);

		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.X_AXIS));

		JLabel imageName = new JLabel("No Image Selected");
		JLabel imageSize = new JLabel("");
		JLabel imageSizeUm = imageSizeMicrometers = new JLabel("");
		JLabel greyscale = new JLabel("");
		JLabel compression = new JLabel("");
		info.add(imageName);
		info.add(Box.createRigidArea(new Dimension(20, 0)));
		info.add(imageSize);
		info.add(Box.createRigidArea(new Dimension(20, 0)));
		info.add(imageSizeUm);
		info.add(Box.createRigidArea(new Dimension(20, 0)));
		info.add(greyscale);
		info.add(Box.createRigidArea(new Dimension(20, 0)));
		info.add(compression);

		add(info, c);

		c.gridy = 1;
		ic = new ImageComponent(null).setInput(true);
		ic.onImageSelected().addObserver((source, event) -> {
			imageName.setText(event.getSourceFile().getName());
			imageSize.setText(event.getImage().getWidth() + "x" + event.getImage().getHeight() + " px");
			imageSizeUm.setText((int) Measurement.PIXELS.convert(Measurement.MICROMETERS, event.getImage().getWidth())
					+ "x" + (int) Measurement.PIXELS.convert(Measurement.MICROMETERS, event.getImage().getHeight())
					+ " μm");
			greyscale.setText(ic.isGreyscale() ? "Greyscale" : "Not Greyscale");

			if (event.getSourceFile().getName().toLowerCase().endsWith(".png")) {
				compression.setForeground(Color.GREEN);
				compression.setText("No Compression");
			} else {
				compression.setForeground(new Color(215, 215, 0));
				compression.setText("Possible Compression");
			}
		});
		add(ic, c);

		ImageComponent blurred = new ImageComponent(null);
		c.gridx = 1;
		add(blurred, c);

		DetectionPanel output = new DetectionPanel(ic.getPreferredSize());
		c.gridx = 2;
		add(output, c);

		JPanel outputInfo = new JPanel();
		outputInfo.setLayout(new BoxLayout(outputInfo, BoxLayout.X_AXIS));

		JLabel outputTime = new JLabel("");
		JLabel outputCircles = new JLabel("");
		JLabel outputAverage = this.outputAverage = new JLabel("");
		outputInfo.add(outputTime);
		outputInfo.add(Box.createRigidArea(new Dimension(20, 0)));
		outputInfo.add(outputCircles);
		outputInfo.add(Box.createRigidArea(new Dimension(20, 0)));
		outputInfo.add(outputAverage);
		outputInfo.add(Box.createRigidArea(new Dimension(20, 0)));

		c.gridy = 0;
		add(outputInfo, c);

		JPanel params = new JPanel();
		params.setLayout(new BoxLayout(params, BoxLayout.X_AXIS));

		JTextField blurFactor = new JTextField(5);
		blurFactor.setText(Cache.get("BlurFactor"));

		JTextField minimumSize = new JTextField(5);
		minimumSize.setText(Cache.get("MinimumSize"));

		JTextField minimumDist = new JTextField(5);
		minimumDist.setText(Cache.get("MinimumDistance"));

		params.add(new JLabel("Blur Factor: "));
		params.add(blurFactor);
		params.add(Box.createRigidArea(new Dimension(10, 0)));
		params.add(new JLabel("Minimum Size: "));
		params.add(minimumSize);
		params.add(Box.createRigidArea(new Dimension(10, 0)));
		params.add(new JLabel("Minimum Distance: "));
		params.add(minimumDist);

		c.gridx = 0;
		c.gridy = 2;
		add(params, c);

		JButton detect = detectButton = new JButton("Measure");
		c.gridx = 0;
		c.gridy = 3;
		add(detect, c);

		JPanel outputTools = new JPanel();
		outputTools.setLayout(new BoxLayout(outputTools, BoxLayout.Y_AXIS));

		JButton addButton = new JButton("Add");
		JButton exportButton = new JButton("Export");
		addButton.setEnabled(false);
		exportButton.setEnabled(false);

		outputTools.add(addButton);
		outputTools.add(exportButton);

		c.gridx = 3;
		c.gridy = 1;
		add(outputTools, c);

		ic.onScaleChanged().addObserver((source, scale) -> {
			addButton.setEnabled(false);
			exportButton.setEnabled(false);

			outputTime.setText("");
			outputCircles.setText("");
			outputAverage.setText("");

			blurred.setImage((IplImage) null);
			blurred.refresh();
			output.setImage(null);
			output.removeAll();

			blurred.setPreferredSize(ic.getPreferredSize());
			output.setPreferredSize(ic.getPreferredSize());
		});
		detect.addActionListener(e -> {
			if (ic.getImage() == null) {
				JOptionPane.showMessageDialog(null, "Please select an image to measure.", Program.APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			double blur;
			try {
				blur = Double.parseDouble(blurFactor.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Please input a valid blur amount.", Program.APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			double min;
			try {
				min = Double.parseDouble(minimumSize.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Please input a valid minimum size.", Program.APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			double dist;
			try {
				dist = Double.parseDouble(minimumDist.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Please input a valid minimum distance.", Program.APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			Cache.save("BlurFactor", Double.toString(blur));
			Cache.save("MinimumSize", Double.toString(min));
			Cache.save("MinimumDistance", Double.toString(dist));

			Detector detector = new Detector(ic.getSelectedFile(), blur, min, dist);
			detector.onBlur().addObserver((source, image) -> {
				blurred.setBackgroundText(null);
				blurred.setImage(image);
				blurred.refresh();
			});
			Thread detectorThread = new Thread(() -> {
				addButton.setEnabled(false);
				exportButton.setEnabled(false);

				if (addButton.getActionListeners().length > 0) {
					addButton.removeActionListener(addButton.getActionListeners()[0]);
				}
				if (exportButton.getActionListeners().length > 0) {
					exportButton.removeActionListener(exportButton.getActionListeners()[0]);
				}

				outputTime.setText("");
				outputCircles.setText("");
				outputAverage.setText("");

				blurred.setImage((IplImage) null);
				output.setImage(null);
				output.removeAll();

				blurred.refresh();
				output.revalidate();
				output.repaint();

				long time = System.currentTimeMillis();

				blurred.setBackgroundText("Initializing detector...");
				blurred.refresh();
				try {
					detector.initialize();
				} catch (IOException ex) {
					Program.exit(ex);
				}
				if (blur > 0.0) {
					blurred.setBackgroundText(
							!ic.isGreyscale() ? "Converting to greyscale and blurring..." : "Blurring...");
				} else {
					blurred.setBackgroundText(!ic.isGreyscale() ? "Converting to greyscale..." : null);
				}
				blurred.refresh();

				detector.blur();

				output.setBackgroundText("Applying Hough Transform to image...");
				output.revalidate();
				output.repaint();

				DetectionList detected = detector.detect();

				long passed = System.currentTimeMillis() - time;

				output.setBackgroundText(null);
				output.setImage(ic.getRenderImage());

				for (Detection detection : detected) {
					output.add(new DetectionComponent(detected, detection, ic.getScale()));
				}

				output.revalidate();
				output.repaint();

				outputTime.setText("Took " + new BigDecimal(passed / 1000d).round(new MathContext(3)) + "s");
				outputCircles.setText(detected.size() + " neurospheres");
				outputAverage.setText("Average "
						+ (int) Measurement.PIXELS.convert(Measurement.MICROMETERS, detected.getAverageDiameter())
						+ "μm");

				addButton.setEnabled(true);
				exportButton.setEnabled(true);
				addButton.addActionListener(event -> {
					Detection detec = new Detection(
							new Point(ic.getImage().getWidth() / 2, ic.getImage().getHeight() / 2),
							detected.getAverageDiameter() / 2);

					detected.add(detec);

					DetectionComponent comp = new DetectionComponent(detected, detec, ic.getScale());
					comp.setColor(Color.RED);
					output.add(comp);
					output.revalidate();
					output.repaint();
				});
				exportButton.addActionListener(event -> {
					JDialog dialog = new JDialog(Main.FRAME, "Export");
					dialog.getContentPane().add(new ExportPanel(dialog, ic.getSelectedFile().getName(), detected));
					dialog.setVisible(true);

					Dimension size = dialog.getPreferredSize();
					size.width *= 1.3;
					size.height *= 1.3;
					dialog.setSize(size);
				});

				detected.onListChanged().addObserver((source, detection) -> {
					outputCircles.setText(detected.size() + " neurospheres");
					outputAverage.setText("Average "
							+ (int) Measurement.PIXELS.convert(Measurement.MICROMETERS, detected.getAverageDiameter())
							+ "μm");
				});
			});
			detectorThread.start();
		});
	}

	public ImageComponent getInputComponent() {
		return ic;
	}

	public JLabel getImageSizeMicrometers() {
		return imageSizeMicrometers;
	}

	public JButton getDetectButton() {
		return detectButton;
	}

	public JLabel getOutputAverage() {
		return outputAverage;
	}
}
