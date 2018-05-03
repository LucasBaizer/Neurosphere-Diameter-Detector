package org.jointheleague.ir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
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
import javax.swing.SwingUtilities;

import org.apache.commons.csv.CSVPrinter;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class MeasurePanel extends JPanel {
	private static final long serialVersionUID = 5677751569635467335L;

	private static MeasurePanel instance;
	private ImageComponent ic;
	private DetectionPanel output;
	private JLabel imageSizeMicrometers;
	private JLabel outputTime;
	private JLabel outputCircles;
	private JLabel outputAverage;
	private JButton detectButton;
	private JButton addButton;
	private JButton exportButton;
	private JButton insertButton;

	public static MeasurePanel getInstance() {
		return instance;
	}

	public MeasurePanel() {
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

		output = new DetectionPanel(ic.getPreferredSize());
		c.gridx = 2;
		add(output, c);

		JPanel outputInfo = new JPanel();
		outputInfo.setLayout(new BoxLayout(outputInfo, BoxLayout.X_AXIS));

		outputTime = new JLabel("");
		outputCircles = new JLabel("");
		outputAverage = new JLabel("");
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

		addButton = new JButton("Add");
		exportButton = new JButton("Export");
		insertButton = new JButton("Insert");
		addButton.setEnabled(false);
		exportButton.setEnabled(false);
		insertButton.setEnabled(false);

		outputTools.add(addButton);
		outputTools.add(exportButton);
		outputTools.add(insertButton);

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

			blurred.setForcedSize(ic.getPreferredSize());
			output.setPreferredSize(ic.getPreferredSize());
		});
		detect.addActionListener(e -> {
			if (ic.getImage() == null) {
				JOptionPane.showMessageDialog(Main.FRAME, "Please select an image to measure.",
						Program.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			double blur;
			try {
				blur = Double.parseDouble(blurFactor.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(Main.FRAME, "Please input a valid blur amount.", Program.APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			double min;
			try {
				min = Double.parseDouble(minimumSize.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(Main.FRAME, "Please input a valid minimum size.",
						Program.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			double dist;
			try {
				dist = Double.parseDouble(minimumDist.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(Main.FRAME, "Please input a valid minimum distance.",
						Program.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
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

				output.setBackgroundText(null);
				output.setImage(ic.getRenderImage());

				onFinish(detected, ic.getScale(), System.currentTimeMillis() - time);
			});
			detectorThread.start();
		});
	}

	public void onFinish(DetectionList detected, double scale, long passed) {
		for (Detection detection : detected) {
			output.add(new DetectionComponent(detected, detection, scale));
		}

		output.revalidate();
		output.repaint();

		outputTime.setText("Took " + new BigDecimal(passed / 1000d).round(new MathContext(3)) + "s");
		outputCircles.setText(detected.size() + " organoids");
		outputAverage.setText("Average "
				+ (int) Measurement.PIXELS.convert(Measurement.MICROMETERS, detected.getAverageDiameter()) + "μm");

		for (ActionListener a : addButton.getActionListeners()) {
			addButton.removeActionListener(a);
		}
		for (ActionListener a : exportButton.getActionListeners()) {
			exportButton.removeActionListener(a);
		}
		for (ActionListener a : insertButton.getActionListeners()) {
			insertButton.removeActionListener(a);
		}

		addButton.setEnabled(true);
		exportButton.setEnabled(true);
		insertButton.setEnabled(true);
		addButton.addActionListener(event -> {
			Detection detec = new Detection(new Point(ic.getImage().getWidth() / 2, ic.getImage().getHeight() / 2),
					Math.max(detected.getAverageDiameter() / 2,
							(int) Measurement.MICROMETERS.convert(Measurement.PIXELS, 100)));

			detected.add(detec);

			DetectionComponent comp = new DetectionComponent(detected, detec, ic.getScale());
			comp.setColor(Color.RED);
			output.add(comp);
			output.revalidate();
			output.repaint();
		});
		exportButton.addActionListener(event -> {
			JDialog dialog = new JDialog(Main.FRAME, "Export");
			dialog.getContentPane()
					.add(new ExportPanel(dialog, ic.getSelectedFile().getName(), detected, (fileName, out, format) -> {
						CSVPrinter csvPrinter = new CSVPrinter(out, format.withHeader("Plate Name", "Cell Size (µm)"));

						for (Detection detection : detected) {
							csvPrinter.printRecord(fileName, Integer.toString((int) Measurement.PIXELS
									.convert(Measurement.MICROMETERS, detection.getDiameter())));
						}

						csvPrinter.flush();
						csvPrinter.close();
					}));

			Dimension size = dialog.getContentPane().getPreferredSize();
			size.width *= 1.5;
			size.height *= 1.5;
			dialog.setSize(size);
			dialog.setLocationRelativeTo(Main.FRAME);

			dialog.setVisible(true);
		});
		insertButton.addActionListener(event -> {
			JButton[] buttons = new JButton[DetectionDatabase.CURRENT.size()];

			for (int i = 0; i < buttons.length; i++) {
				buttons[i] = new JButton(DetectionDatabase.CURRENT.get(i).getName());
				buttons[i].addActionListener(evt -> {
					DetectionDatabase db = DetectionDatabase.forName(((JButton) evt.getSource()).getText());
					db.insert(ic.getSelectedFile(), detected);
					try {
						db.save();
					} catch (IOException e) {
						Program.exit(e);
					}

					SwingUtilities.getWindowAncestor((JButton) evt.getSource()).dispose();
				});
			}

			Object[] result = new Object[buttons.length + 1];
			result[0] = "Choose a database to insert the data into:";
			System.arraycopy(buttons, 0, result, 1, buttons.length);
			JOptionPane.showOptionDialog(Main.FRAME, result, Program.APPLICATION_NAME, JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, new Object[0], null);
		});

		detected.onListChanged().addObserver((source, detection) -> {
			outputCircles.setText(detected.size() + " organoids");
			outputAverage.setText("Average "
					+ (int) Measurement.PIXELS.convert(Measurement.MICROMETERS, detected.getAverageDiameter()) + "μm");
		});
	}

	public ImageComponent getInputComponent() {
		return ic;
	}

	public DetectionPanel getOutputComponent() {
		return output;
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
