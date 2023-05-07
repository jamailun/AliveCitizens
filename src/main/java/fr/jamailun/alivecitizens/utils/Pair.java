package fr.jamailun.alivecitizens.utils;

import java.util.Objects;

public class Pair<A, B> {
	
	private final A first;
	private final B second;
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	public A first() {
		return first;
	}
	
	public B second() {
		return second;
	}
	
	@Override
	public String toString() {
		return "{" + first + ", " + second + "}";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if(o instanceof Pair pair) {
			return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
	
	public static class PairD<A> extends Pair<A, A> {
		
		public PairD(A first, A second) {
			super(first, second);
		}
	}
	
}
