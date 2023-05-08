package fr.jamailun.alivecitizens.navigation;

import fr.jamailun.alivecitizens.utils.NumbersUtils;
import fr.jamailun.alivecitizens.utils.Pair;
import fr.jamailun.alivecitizens.utils.ParticlesPlayer;
import fr.jamailun.alivecitizens.utils.Showable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Waypoint implements Showable {
	
	private final WaypointsGrid grid;
	private final Location location;
	
	private final Set<Waypoint> connecteds = new HashSet<>();
	
	Waypoint(WaypointsGrid grid, Location location) {
		this.grid = grid;
		this.location = location;
	}
	
	void tryConnectWith(Waypoint waypoint) {
		String debug = NumbersUtils.formatLocation(location) + " <-> " + NumbersUtils.formatLocation(waypoint.location);
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
	
	@Override
	public void showPlayer(Player target) {
		target.spawnParticle(
				Particle.ELECTRIC_SPARK,
				location.getX(), location.getY(), location.getZ(),
				1,
				0, 0, 0,
				0
		);
		
	}
}
