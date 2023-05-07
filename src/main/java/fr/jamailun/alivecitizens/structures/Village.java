package fr.jamailun.alivecitizens.structures;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.navigation.WaypointsGrid;
import fr.jamailun.alivecitizens.utils.NumbersUtils;
import fr.jamailun.alivecitizens.utils.ParticlesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class Village extends Structure {
	
	private final VillagePlace place;
	private final List<Fields> fields = new ArrayList<>();
	private final List<VillageHouse> houses = new ArrayList<>();
	
	private final WaypointsGrid grid;
	
	private Village(String uuid, @NotNull VillagePlace place, WaypointsGrid grid) {
		super(uuid);
		this.place = place;
		this.grid = grid;
	}
	
	public Village(@NotNull String id, Location center, double radius) {
		super(id);
		this.place = new VillagePlace(center, radius);
		this.grid = new WaypointsGrid(Collections.emptyList());
	}
	
	public @NotNull String getId() {
		return getUuid();
	}
	
	public @NotNull VillagePlace getPlace() {
		return place;
	}
	
	public void addFields(Fields fields) {
		this.fields.add(fields);
	}
	
	public boolean removeFields(String uuid) {
		return this.fields.removeIf(f -> f.getUuid().equals(uuid));
	}
	
	public void addHouse(VillageHouse house) {
		this.houses.add(house);
	}
	
	public boolean removeHouse(String uuid) {
		return this.houses.removeIf(h -> h.getUuid().equals(uuid));
	}
	
	public VillageHouse getHouse(String id) {
		return houses.stream().filter(h -> h.getUuid().equals(id)).findFirst().orElse(null);
	}
	
	public boolean isWaypoint(Block block) {
		return grid.contains(block);
	}
	
	public void addWaypoint(Block block) {
		grid.add(block);
	}
	
	public void removeWaypoint(Block block) {
		grid.remove(block);
	}
	
	
	
	@Override
	public @NotNull Map<String, Object> serialize() {
		return new HashMap<>() {{
			put("uuid", uuid);
			put("place", place);
			put("waypoints", grid.serialize());
			put("houses", houses.stream().map(VillageHouse::serialize).toList());
			put("fields", fields.stream().map(Fields::serialize).toList());
		}};
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public static Village deserialize(@NotNull Map<String, Object> map) {
		// Village
		String id = (String) map.get("uuid");
		VillagePlace place = (VillagePlace) map.get("place");
		List<String> waypoints = (List<String>) map.get("waypoints");
		Village village = new Village(id, place, new WaypointsGrid(waypoints));
		
		// Fields
		List<Map<String, Object>> fieldsList = (List<Map<String, Object>>) map.get("fields");
		if(fieldsList != null)
			village.fields.addAll(fieldsList.stream().map(m -> Fields.deserialize(village, m)).toList());
		// Houses
		List<Map<String, Object>> housesList = (List<Map<String, Object>>) map.get("houses");
		if(housesList != null)
			village.houses.addAll(housesList.stream().map(m -> VillageHouse.deserialize(village, m)).toList());
		return village;
	}
	
	@Override
	public String toString() {
		return "Village{'" + getUuid() + "', place=" + place + "}";
	}
	
	public String getStatus() {
		StringJoiner fieldsJoiner = new StringJoiner(ChatColor.YELLOW + ",");
		for(Fields field : this.fields) {
			fieldsJoiner.add(ChatColor.YELLOW + "\n    - [" + field.getUuid() + "]");
		}
		
		StringJoiner housesJoiner = new StringJoiner(ChatColor.YELLOW + ",");
		for(VillageHouse house : this.houses) {
			housesJoiner.add((house.isValid() ? ChatColor.YELLOW : ChatColor.RED) + "\n    - [" + house.getUuid().substring(0, 8) + "]§e Inhabitant = " + ChatColor.WHITE + (house.getInhabitant()));
		}
		
		return ChatColor.YELLOW + "Village's ID : [" + ChatColor.DARK_GREEN + getUuid() + ChatColor.YELLOW + "]\n"
				+ ChatColor.GOLD   + " - Place :" + "\n"
				+ ChatColor.YELLOW + "    - Center = " + ChatColor.WHITE + NumbersUtils.formatLocation(place.getCenter()) + "\n"
				+ ChatColor.YELLOW + "    - Radius = " + ChatColor.WHITE + NumbersUtils.FORMAT_1.format(place.getRadius()) + "\n"
				+ ChatColor.GOLD   + " - Fields : " + ChatColor.GRAY
					+ (fields.isEmpty() ? (
							"No fields.\n"
					) : (
							"(" + fields.size() + ")" + fieldsJoiner + "\n"
					))
				+ ChatColor.GOLD   + " - Houses : " + ChatColor.GRAY
					+ (houses.isEmpty() ? (
							"No houses."
					) : (
							"(" + houses.size() + ")" + housesJoiner
					))
				;
	}
	
	private final Map<UUID, BukkitTask> showing = new HashMap<>();
	public boolean show(Player player) {
		UUID uuid = player.getUniqueId();
		if(showing.containsKey(uuid)) {
			showing.remove(uuid).cancel();
			return false;
		}
		showing.put(uuid, Bukkit.getScheduler().runTaskTimer(AliveCitizens.plugin(), () -> {
			// place center
			ParticlesPlayer.playCircleXZ(player, getPlace().getCenter(), getPlace().getRadius(), Math.toRadians(2), Particle.FLAME);
			ParticlesPlayer.playCircleXZ(player, getPlace().getCenter(), 1, Math.toRadians(6), Particle.DRAGON_BREATH);
			// waypoints
			grid.showPlayer(player);
			// Houses
			houses.forEach(h -> {
				if(h.isValid()) {
					ParticlesPlayer.playLine(player, getPlace().getCenter(), h.getBed().getLocation(), 0.4, Particle.DRAGON_BREATH);
					h.showPlayer(player);
				}
			});
			fields.forEach(f -> {
				f.showPlayer(player);
			});
		}, 0, 20));
		return true;
	}
}
