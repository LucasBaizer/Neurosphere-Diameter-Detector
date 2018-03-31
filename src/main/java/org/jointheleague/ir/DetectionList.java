package org.jointheleague.ir;

import java.util.ArrayList;

public class DetectionList extends ArrayList<Detection> {
	private static final long serialVersionUID = 1372891071449469303L;

	private Subject<ListEvent<Detection>> listChanged = new Subject<ListEvent<Detection>>();

	public DetectionList() {
		super();
	}

	public DetectionList(int size) {
		super(size);
	}

	public int getAverageDiameter() {
		int totalDiameter = 0;
		for (Detection detection : this) {
			totalDiameter += detection.getDiameter();
		}
		return (int) (totalDiameter / (double) size());
	}

	@Override
	public boolean add(Detection e) {
		boolean x = super.add(e);

		if (x) {
			listChanged.markChanged();
			listChanged.notifyObservers(new ListEvent<Detection>(ListEvent.ADD, e));
		}

		return x;
	}

	@Override
	public boolean remove(Object e) {
		boolean x = super.remove(e);

		if (x) {
			listChanged.markChanged();
			listChanged.notifyObservers(new ListEvent<Detection>(ListEvent.REMOVE, (Detection) e)); // this
																									// is
																									// not
																									// type-safe,
																									// but
																									// we'll
																									// just
																									// trust
																									// ourselves
																									// for
																									// now
		}

		return x;
	}

	public Subject<ListEvent<Detection>> onListChanged() {
		return listChanged;
	}
}
