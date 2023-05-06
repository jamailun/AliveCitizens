package fr.jamailun.alivecitizens.commands;

import java.util.Set;

public class JamArg {
	private final int index;
	private final Set<String> values;
	public JamArg(int index, String value) {
		this.index = index;
		this.values = Set.of(value);
	}
	public JamArg(int index, String... values) {
		this.index = index;
		this.values = Set.of(values);
	}
	public boolean accepts(String[] args) {
		if(index < args.length)
			return values.contains(args[index]);
		return false;
	}
}
