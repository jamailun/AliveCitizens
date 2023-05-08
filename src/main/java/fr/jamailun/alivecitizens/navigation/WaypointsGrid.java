package fr.jamailun.alivecitizens.navigation;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.utils.NumbersUtils;
import fr.jamailun.alivecitizens.utils.ParticlesPlayer;
import fr.jamailun.alivecitizens.utils.Showable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WaypointsGrid implements Showable {
	private final static Vector BLOCK_DELTA = new Vector(0.5, 1.1, 0.5);
	
	private final Set<Waypoint> nodes = new HashSet<>();
	
	public WaypointsGrid(List<String> data) {
		data.stream().map(NumbersUtils::deserializeVectorBlockMiddle)
				.map(l -> new Waypoint(this, l))
				.forEach(this::registerNewWaypoint);
		AliveCitizens.log("Loaded " + nodes.size() + " nodes.");
	}
	
	public @NotNull List<String> serialize() {
		return nodes.stream()
				.map(n -> NumbersUtils.serializeLocationBlockMiddle(n.getLocation()))
				.toList();
	}
	
	public boolean contains(Block block) {
		Location loc = block.getLocation().add(BLOCK_DELTA);
		return nodes.stream().anyMatch(w -> w.getLocation().equals(loc));
	}
	
	private void registerNewWaypoint(Waypoint wp) {
		for(Waypoint present : nodes) {
			present.tryConnectWith(wp);
		}
		nodes.add(wp);
	}
	
	public Location add(Block block) {
		String a = NumbersUtils.formatLocation(block.getLocation());
		Location loc = block.getLocation().add(BLOCK_DELTA);
		String b = NumbersUtils.formatLocation(loc);
		
		Bukkit.broadcastMessage(a + " -> " + b);
		
		Waypoint wp = new Waypoint(this, loc);
		registerNewWaypoint(wp);
		
		return loc.clone();
	}
	
	public Location remove(Block block) {
		Location loc = block.getLocation().add(BLOCK_DELTA);
		Waypoint wp = nodes.stream().filter(w -> w.getLocation().equals(loc)).findFirst().orElse(null);
		if(wp == null) {
			//
			return loc;
		}
		wp.delete(); // remove all connections
		nodes.remove(wp);
		return loc;
	}
	
	boolean isBlockedByBlock(Location source, Location dest) {
		ParticlesPlayer.playLine(source.clone(), dest.clone(), 0.2, Particle.END_ROD);
		
		final double delta = 0.5;
		Vector dir = dest.toVector().subtract(source.toVector()).normalize().multiply(delta);
		double distance = dest.distance(source);
		Location iterator = source.clone().add(0, 0.5, 0);
		for(double d = 0; d <= distance; d += delta) {
			Block b = iterator.add(dir).getBlock();
			if(b.getType().isSolid()) { //XXX add doors check
				return true;
			}
		}
		return false;
	}
	
	public double getMaxDistance() {
		return 10;
	}
	
	@Override
	public void showPlayer(Player target) {
		nodes.forEach(n -> n.showPlayer(target));
		
		nodes.stream()
				.flatMap(n -> n.getPairs().stream())
				.distinct()
				.forEach(p -> ParticlesPlayer.playLine(
						target,
						p.first().getLocation(),
						p.second().getLocation(),
						0.5,
						Particle.END_ROD
				));
		
	}
}
