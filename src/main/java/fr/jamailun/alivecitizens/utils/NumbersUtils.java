package fr.jamailun.alivecitizens.utils;

import org.bukkit.Location;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumbersUtils {
	private NumbersUtils() {}

	private final static DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.ROOT) {{
		setDecimalSeparator('.');
	}};
	
	public final static DecimalFormat FORMAT_1 = new DecimalFormat("0.#", SYMBOLS);
	public final static DecimalFormat FORMAT_2 = new DecimalFormat("0.##", SYMBOLS);
	public final static DecimalFormat FORMAT_4 = new DecimalFormat("0.####", SYMBOLS);

	public static String formatLocation(Location loc) {
		return "(" + FORMAT_1.format(loc.getX()) + ", " + FORMAT_1.format(loc.getY()) + ", " + FORMAT_1.format(loc.getZ()) + ")";
	}
	
}
