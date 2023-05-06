package fr.jamailun.alivecitizens.farmers;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import fr.jamailun.alivecitizens.structures.VillageHouse;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;

import java.util.Objects;

@TraitName("farmer")
public class FarmerTrait extends Trait {
	
	private transient VillageHouse house;
	
	@Persist private String houseId;
	@Persist private String villageId;
	
	public FarmerTrait() {
		super("farmer");
	}
	
	@Override
	public void load(DataKey key) throws NPCLoadException {
		for (DataKey subkey : key.getSubKeys()) {
			if(subkey.name().equals("houseId")) {
				houseId = key.getString(subkey.name());
			} else if(subkey.name().equals("villageId")) {
				villageId = key.getString(subkey.name());
			}
		}
		// update
		if(houseId != null && villageId != null) {
			Village village = AliveCitizens.plugin().getFarmersManager().getVillage(villageId);
			if(village == null) {
				AliveCitizens.logError("Invalid village ID : '" + villageId + "' for NPC '" + getNPC().getName()+"'.");
				return;
			}
			VillageHouse house = village.getHouse(houseId);
			if(house == null) {
				AliveCitizens.logError("Invalid house ID : '"+villageId+"/" + houseId + "' for NPC '" + getNPC().getName()+"'.");
				return;
			}
			AliveCitizens.logSuccess("Found house ("+villageId+"/"+houseId+") for NPC " + getNPC().getName());
			setHouse(house);
		}
	}
	
	@Override
	public void onAttach() {
		AliveCitizens.plugin().getFarmersManager().registerFarmerTrait(this);
	}
	
	@Override
	public void onRemove() {
		AliveCitizens.plugin().getFarmersManager().unregisterFarmerTrait(this);
		if(house != null) {
			house.setInhabitant(null);
		}
	}
	
	public void setHouse(VillageHouse house) {
		this.house = house;
		house.setInhabitant(this);
		this.houseId = house.getUuid();
		this.villageId = house.getVillage().getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if(!(o instanceof FarmerTrait farmer)) {
			return false;
		}
		return Objects.equals(getNPC().getUniqueId(), farmer.getNPC().getUniqueId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getNPC().getUniqueId());
	}
}
