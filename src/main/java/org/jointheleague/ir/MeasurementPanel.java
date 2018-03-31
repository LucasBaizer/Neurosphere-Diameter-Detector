package org.jointheleague.ir;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MeasurementPanel extends JPanel {
	private static final long serialVersionUID = -7252883951454034864L;

	private Subject<Void> onExit = new Subject<>();

	public MeasurementPanel(JDialog container) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel pxPanel = new JPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JTextField pxAmount = new JTextField(5);
		JLabel pxLabel = new JLabel("pixels = ");
		JTextField umAmount = new JTextField(5);
		JLabel umLabel = new JLabel("micrometers");

		Fraction amount = Measurement.CONVERSIONS.get(new Conversion(Measurement.PIXELS, Measurement.MICROMETERS));
		pxAmount.setText(Integer.toString(amount.getDenominator()));
		umAmount.setText(Integer.toString(amount.getNumerator()));

		pxPanel.add(pxAmount);
		pxPanel.add(pxLabel);
		pxPanel.add(umAmount);
		pxPanel.add(umLabel);

		JPanel buttonsPanel = new JPanel();
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			container.dispose();
		});
		JButton okButton = new JButton("Save & Close");
		okButton.addActionListener(e -> {
			try {
				int px = Integer.parseInt(pxAmount.getText());
				int um = Integer.parseInt(umAmount.getText());

				Fraction newFraction = new Fraction(um, px);
				Measurement.setConversion(new Conversion(Measurement.PIXELS, Measurement.MICROMETERS), newFraction);

				container.dispose();

				onExit().markChanged();
				onExit().notifyObservers(null);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(Main.FRAME, "Please enter a valid amount (whole numbers only).",
						Program.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
		});

		buttonsPanel.add(cancelButton);
		buttonsPanel.add(okButton);

		add(pxPanel);
		add(buttonsPanel);
	}

	public Subject<Void> onExit() {
		return onExit;
	}
}
