package fr.jamailun.alivecitizens.structures;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VillagePlace implements ConfigurationSerializable {
	
	private Location center;
	private double radius;
	
	public VillagePlace(Location center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public void setCenter(Location center) {
		this.center = center;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("center", center.serialize());
		map.put("radius", radius);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public static VillagePlace deserialize(@NotNull Map<String, Object> map) {
		Location center = Location.deserialize((Map<String, Object>)map.get("center"));
		double radius = (double) map.get("radius");
		
		return new VillagePlace(center, radius);
	}
}
