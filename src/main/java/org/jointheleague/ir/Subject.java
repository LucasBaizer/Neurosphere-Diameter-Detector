package org.jointheleague.ir;

import java.util.ArrayList;
import java.util.List;

public class Subject<T> {
	private List<Observer<T>> observers = new ArrayList<Observer<T>>();
	private boolean changed;

	public int getObserverCount() {
		return observers.size();
	}

	public void markUnchanged() {
		this.changed = false;
	}

	public void markChanged() {
		this.changed = true;
	}

	public boolean hasChanged() {
		return this.changed;
	}

	public boolean notifyObservers(T change) {
		if (changed) {
			for (Observer<T> observer : observers) {
				observer.observe(this, change);
			}

			markUnchanged();
			return true;
		}
		return false;
	}

	public List<Observer<T>> getObservers() {
		return new ArrayList<Observer<T>>(observers);
	}

	public void addObserver(Observer<T> o) {
		observers.add(o);
	}

	public void removeObserver(Observer<T> o) {
		observers.remove(o);
	}

	public void setObservers(List<Observer<T>> os) {
		this.observers = new ArrayList<Observer<T>>(os);
	}
}
