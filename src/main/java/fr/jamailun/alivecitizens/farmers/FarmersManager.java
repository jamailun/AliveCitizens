package fr.jamailun.alivecitizens.farmers;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.configuration.file.YamlConfiguration;
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

public class FarmersManager {
	
	private final Set<FarmerTrait> farmers;
	private final Map<String, Village> villages = new HashMap<>();
	
	private final File villagesFile;
	private final YamlConfiguration villagesConfig;
	
	public FarmersManager(AliveCitizens plugin) {
		farmers = new HashSet<>();
		villagesFile = new File(plugin.getDataFolder(), "villages.yml");
		villagesConfig = YamlConfiguration.loadConfiguration(villagesFile);
		
		reloadData();
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
