package org.jointheleague.ir;

import java.awt.Dimension;

import javax.swing.JPanel;

public class SizedPanel extends JPanel {
	private static final long serialVersionUID = 7333486114046280973L;

	private Dimension preferred;

	public SizedPanel(Dimension preferred) {
		this.preferred = preferred;
	}

	public void setForcedSize(Dimension size) {
		this.preferred = size;
	}

	public Dimension getTypicalSize() {
		return super.getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		return preferred;
	}

	@Override
	public Dimension getMinimumSize() {
		return preferred;
	}

	@Override
	public Dimension getMaximumSize() {
		return preferred;
	}

	@Override
	public Dimension getSize() {
		return preferred;
	}

	@Override
	public Dimension getSize(Dimension rv) {
		rv.width = preferred.width;
		rv.height = preferred.height;
		return rv;
	}
}
