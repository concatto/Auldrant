package br.univali.minseiscluster.model;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {
	private int index;
	private T[] array;

	public ArrayIterator(T[] array) {
		this.array = array;
	}

	@Override
	public boolean hasNext() {
		return index < array.length;
	}

	@Override
	public T next() {
		return array[index++];
	}

}
