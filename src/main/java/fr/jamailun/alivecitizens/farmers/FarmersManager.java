package fr.jamailun.alivecitizens.farmers;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FarmersManager {
	
	private final Set<FarmerTrait> farmers= new HashSet<>();
	private final Map<String, Village> villages = new HashMap<>();
	
	private final Map<UUID, Village> waypointsEditors = new HashMap<>();
	
	private final File villagesFile;
	private final YamlConfiguration villagesConfig;
	
	public FarmersManager(AliveCitizens plugin) {
		villagesFile = new File(plugin.getDataFolder(), "villages.yml");
		villagesConfig = YamlConfiguration.loadConfiguration(villagesFile);
		
		reloadData();
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			//Bukkit.broadcastMessage("update farmers");
			farmers.forEach(FarmerTrait::updateBehaviour);
		}, 60, 20*10);
	}
	
	public void reloadData() {
		// Load villages
		AliveCitizens.log("Loading villages");
		for(String key : villagesConfig.getKeys(false)) {
			Village village = villagesConfig.getSerializable(key, Village.class);
			if(village == null) {
				AliveCitizens.logError("Could NOT load village of name '"+key+"'.");
				continue;
			}
			System.out.println("loaded village " + village);
			villages.put(village.getId(), village);
		}
		AliveCitizens.log("Loaded " + villages.size() + " villages.");
	}
	
	public void save() {
		try {
			villagesConfig.save(villagesFile);
		} catch (IOException e) {
			AliveCitizens.logError("Could not save villages config.");
			e.printStackTrace();
		}
	}
	
	public boolean registerNewVillage(@NotNull Village village) {
		if(villages.containsKey(village.getId())) {
			return true;
		}
		villages.put(village.getId(), village);
		villagesConfig.set(village.getId(), village);
		save();
		return false;
	}
	
	public void removeVillage(@NotNull Village village) {
		villages.remove(village.getId());
		villagesConfig.set(village.getId(), null);
		save();
	}
	
	public Village getEditedWaypoints(Player player) {
		return waypointsEditors.get(player.getUniqueId());
	}
	
	public void startEdit(Player player, Village village) {
		stopEdit(player);
		waypointsEditors.put(player.getUniqueId(), village);
	}
	public void stopEdit(Player player) {
		waypointsEditors.remove(player.getUniqueId());
	}
	
	public List<String> villagesIds() {
		return new ArrayList<>(villages.keySet());
	}
	
	public @Nullable Village getVillage(String id) {
		return villages.get(id);
	}
	
	public void registerFarmerTrait(FarmerTrait farmerTrait) {
		AliveCitizens.log("Register FarmerTrait attached to " + farmerTrait.getNPC() + ".");
		farmers.add(farmerTrait);
	}
	public void unregisterFarmerTrait(FarmerTrait farmerTrait) {
		AliveCitizens.log("Register FarmerTrait attached to " + farmerTrait.getNPC() + ".");
		farmers.add(farmerTrait);
	}
}
