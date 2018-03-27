package org.jointheleague.ir;

@FunctionalInterface
public interface Observer<T> {
	public void observe(Subject<T> source, T event);
}
