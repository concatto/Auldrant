package br.concatto.karnaugh;

import java.util.Arrays;

public class Expression {
	public int a;
	public int b;
	public int c;
	public int d;
	private boolean used = false;
	
	public Expression(boolean a, boolean b, boolean c, boolean d) {
		this.a = a ? 1 : 0;
		this.b = b ? 1 : 0;
		this.c = c ? 1 : 0;
		this.d = d ? 1 : 0;
	}
	
	/**
	 * Constructs an expression. 1 for true, 0 for false, -1 for inexistant.
	 */
	public Expression(int a, int b, int c, int d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	@Override
	public String toString() {
		String expression = 
				subExpression(a, 'a') +
				subExpression(b, 'b') +
				subExpression(c, 'c') +
				subExpression(d, 'd');
		return expression;
	}
	
	private String subExpression(int value, char letter) {
		letter = Character.toUpperCase(letter);
		if (value == -1) {
			return "";
		} else if (value == 0) {
			return letter + "\' ";
		} else if (value == 1) {
			return letter + "  ";
		} else {
			return "?";
		}
	}
	
	public int[] toArray() {
		return new int[]{a, b, c, d};
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public int getLength() {
		int length = 4;
		for (int i : this.toArray()) {
			if (i == -1) length--;
		}
		return length;
	}
	
	public Expression tryMerge(Expression other) {
		int[] thisArray = this.toArray();
		int[] otherArray = other.toArray();
		int[] newArray = Arrays.copyOf(thisArray, thisArray.length);
		
		if (this.getLength() != other.getLength()) return null;
		
		for (int i = 0; i < newArray.length; i++) {
			if (newArray[i] != otherArray[i]) newArray[i] = -1;
		}
		
		Expression expression = new Expression(newArray[0], newArray[1], newArray[2], newArray[3]);
		if (expression.getLength() != this.getLength() - 1) return null;
		return expression;
	}
}
