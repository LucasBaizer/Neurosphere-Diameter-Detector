package org.jointheleague.ir;

public class ListEvent<T> {
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int MANUAL = 2;
	
	private int type;
	private T object;

	public ListEvent(int type, T object) {
		this.type = type;
		this.object = object;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
}
