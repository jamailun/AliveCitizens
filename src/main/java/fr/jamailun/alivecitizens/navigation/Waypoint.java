package fr.jamailun.alivecitizens.navigation;

import fr.jamailun.alivecitizens.utils.NumbersUtils;
import fr.jamailun.alivecitizens.utils.Pair;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Waypoint {
	
	private final WaypointsGrid grid;
	private final Location location;
	
	private final Set<Waypoint> connecteds = new HashSet<>();
	
	Waypoint(WaypointsGrid grid, Location location) {
		this.grid = grid;
		this.location = location;
	}
	
	void tryConnectWith(Waypoint waypoint) {
		if(distance(waypoint) > grid.getMaxDistance() || grid.isBlockedByBlock(location, waypoint.location)) {
			return;
		}
		connecteds.add(waypoint);
		waypoint.connecteds.add(this);
	}
	
	double distance(Waypoint other) {
		return other.location.distance(location);
	}
	
	void delete() {
		for(Waypoint other : connecteds) {
			other.connecteds.remove(this);
		}
		connecteds.clear();
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public Set<Pair.PairD<Waypoint>> getPairs() {
		return connecteds.stream()
				.map(o -> new Pair.PairD<Waypoint>(this, o))
				.collect(Collectors.toSet());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if(!(o instanceof Waypoint wayp))
			return false;
		return Objects.equals(location, wayp.location);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(location);
	}
}
