package fr.jamailun.alivecitizens.structures;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.farmers.FarmerTrait;
import fr.jamailun.alivecitizens.utils.ParticlesPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VillageHouse extends Structure {
	
	private final transient Village village;
	private final Block bed;
	private final transient boolean valid;
	private transient FarmerTrait inhabitant;
	
	public VillageHouse(Village village, Block bed) {
		super();
		this.village = village;
		this.bed = bed;
		assert bed.getBlockData() instanceof Bed : "This is not a bed !";
		valid= true;
	}
	
	private VillageHouse(Village village, String uuid, Location bedLocation) {
		super(uuid);
		this.village = village;
		bed = bedLocation.getBlock();
		valid = bed.getBlockData() instanceof Bed;
		if(!valid) {
			AliveCitizens.logError("Invalid bed type : " + bed.getType() + " at " + bed.getLocation());
		}
	}
	
	public Village getVillage() {
		return village;
	}
	
	public void setInhabitant(FarmerTrait inhabitant) {
		this.inhabitant = inhabitant;
	}
	
	public FarmerTrait getInhabitant() {
		return inhabitant;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public Block getBed() {
		return bed;
	}
	
	@NotNull
	@Override
	public Map<String, Object> serialize() {
		return new HashMap<>() {{
			put("uuid", uuid);
			put("bed", bed.getLocation().serialize());
		}};
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public static VillageHouse deserialize(Village village, @NotNull Map<String, Object> map) {
		// Village
		String id = (String) map.get("uuid");
		Location bed = Location.deserialize((Map<String, Object>)map.get("bed"));
		return new VillageHouse(village, id, bed);
	}
	
	public void showPlayer(Player target) {
		if(!valid) return;
		ParticlesPlayer.playCircleXZ(target, bed.getLocation(), 6, Math.toRadians(12), Particle.HEART);
		if(inhabitant != null)
			ParticlesPlayer.playLine(target, bed.getLocation(), inhabitant.getNPC().getStoredLocation(), 0.4, Particle.HEART);
	}
}
