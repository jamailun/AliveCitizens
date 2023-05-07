package fr.jamailun.alivecitizens.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	public static @NotNull String formatLocation(@Nullable Location loc) {
		if(loc == null)
			return "";
		return "(" + FORMAT_1.format(loc.getX()) + ", " + FORMAT_1.format(loc.getY()) + ", " + FORMAT_1.format(loc.getZ()) + ")";
	}
	
	public static @NotNull String serializeLocationBlockMiddle(@NotNull Location loc) {
		return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
	}
	
	public static @NotNull Location deserializeVectorBlockMiddle(@NotNull String str) {
		String[] tokens = str.split(";", 3);
		String w = tokens[0];
		double x = Integer.parseInt(tokens[1]);
		double y = Integer.parseInt(tokens[2]);
		double z = Integer.parseInt(tokens[3]);
		return new Location(Bukkit.getWorld(w), x + 0.5, y + 0.5, z + 0.5);
	}
	
}
