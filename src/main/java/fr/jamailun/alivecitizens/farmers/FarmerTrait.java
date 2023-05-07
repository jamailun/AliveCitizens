package fr.jamailun.alivecitizens.farmers;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import fr.jamailun.alivecitizens.structures.VillageHouse;
import fr.jamailun.alivecitizens.utils.NumbersUtils;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

@TraitName("farmer")
public class FarmerTrait extends Trait {
	
	private final static Random random = new Random();
	
	private transient VillageHouse house;
	private transient FarmerBehaviour behaviour = FarmerBehaviour.UNDEFINED;
	private transient HumanEntity __self;
	private World world;
	private HumanEntity self() {
		if(__self == null || !__self.isValid()) {
			if(!getNPC().isSpawned()) {
				if(house.isValid()) {
					getNPC().spawn(house.getBed().getLocation());
				} else {
					AliveCitizens.logError("Cannot spawn NPC " + getNPC().getName());
				}
			}
			__self = (HumanEntity) getNPC().getEntity();
		}
		return __self;
	}
	
	private FarmerBehaviour lastBehave = FarmerBehaviour.UNDEFINED;
	
	private boolean spawned = false;
	private boolean sleeping = false;
	private long nextMove;
	
	@Persist private String houseId;
	@Persist private String villageId;
	
	public FarmerTrait() {
		super("farmer");
	}
	
	private void stopSleepIfNeeded() {
		if(sleeping) {
			try {
				self().wakeup(false);
			} catch(IllegalStateException ignored) { }
			
			Bukkit.broadcastMessage(getNPC().getName() + " woke up!");
			sleeping = false;
		}
	}
	
	@Override
	public void run() {
		if(!spawned) return;
		boolean firstTimeBehave;
		if(behaviour != lastBehave) {
			Bukkit.broadcastMessage("§anew behaviour for §2"+getNPC().getName()+"§a : §6" + behaviour);
			lastBehave = behaviour;
			firstTimeBehave = true;
		} else {
			firstTimeBehave = false;
		}
		
		switch (behaviour) {
			case SLEEP -> {
				if(firstTimeBehave || getWorldTime() > nextMove) {
					nextMove = getWorldTime() + 20L * 5;
					// if should be sleeping but is not, do it.
					if(!sleeping && house.isValid()) {
						// distance : if too large move to bed
						double distance = house.getBed().getLocation().distance(self().getLocation());
						if(distance > 2) {
							Bukkit.broadcastMessage(getNPC().getName() + " go to BED (" + NumbersUtils.formatLocation(house.getBed().getLocation()));
							getNPC().getNavigator().setTarget(house.getBed().getLocation());
						} else {
							Bukkit.broadcastMessage(getNPC().getName() + " go sleep (d="+distance+") ");
							getNPC().getNavigator().cancelNavigation();
							if(!self().sleep(house.getBed().getLocation().clone(), true)) {
								AliveCitizens.logError("Could not make NPC " + getNPC().getName() + " sleep.");
							}
							sleeping = true;
						}
					}
				}
			}
			case WAKE_UP -> {
				stopSleepIfNeeded();
				// move around in the house
				if(firstTimeBehave || getWorldTime() > nextMove) {
					nextMove = getWorldTime() + 20L * randInt(10, 20);
					Location randomLoc = findNearPlace(self().getLocation(), 2, 3, 5);
					Bukkit.broadcastMessage(getNPC().getName() + " walk random HOUSE = " + NumbersUtils.formatLocation(randomLoc));
					if(randomLoc != null)
						getNPC().getNavigator().setTarget(randomLoc);
				}
			}
			case VILLAGE_EXPLORE -> {
				if(firstTimeBehave || getWorldTime() > nextMove) {
					nextMove = getWorldTime() + 20L * randInt(20, 30);
					Location randomLoc = findNearPlace(house.getVillage().getPlace().getCenter(), 1, house.getVillage().getPlace().getRadius(), 15);
					Bukkit.broadcastMessage(getNPC().getName() + " walk random VILLAGE = " + NumbersUtils.formatLocation(randomLoc));
					if(randomLoc != null)
						getNPC().getNavigator().setTarget(randomLoc);
				}
			}
			case GO_BACK_HOME -> {
				if(firstTimeBehave) {
					Bukkit.broadcastMessage(getNPC().getName() + " go back home !");
					getNPC().getNavigator().setTarget(house.getBed().getLocation());
				}
			}
			default -> {
				stopSleepIfNeeded();
			}
		}
	}
	
	private long getWorldTime() {
		return getNPC().getEntity().getWorld().getFullTime();
	}
	
	public void updateBehaviour() {
		if(!spawned)
			return;
		behaviour = FarmerBehaviour.theoricalBehaviour(self().getWorld().getTime());
	}
	
	public @Nullable Location findNearPlace(Location from, double minDistance, double maxDistance, int maxTries) {
		double r = randDouble(minDistance, maxDistance);
		for(int tries = 0; tries < maxTries; tries++) {
			double theta = randDouble(0, Math.PI * 2);
			Location loc = from.add(new Vector(r*Math.cos(theta), 0, r*Math.sin(theta)));
			Block b = world.getBlockAt(loc);
			if(b.getType().isSolid()) {
				b = b.getRelative(BlockFace.UP);
				if(b.getType().isSolid())
					continue;
			}
			return b.getLocation();
		}
		return null;
	}
	
	private static int randInt(int min, int max) {
		return random.nextInt(max + 1 - min) + min;
	}
	private static double randDouble(double min, double max) {
		return min + (max - min) * random.nextDouble();
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
			if( ! house.isValid()) {
				AliveCitizens.logError("Found gouse : '"+villageId+"/" + houseId + "' for NPC '" + getNPC().getName()+"' but it's invalid... Keeping the config.");
			}
			AliveCitizens.logSuccess("Found house ("+villageId+"/"+houseId+") for NPC " + getNPC().getName());
			setHouse(house);
		} else {
			AliveCitizens.log("NPC " + getNPC().getName() + " does not have any farmer data. Removing trait.");
			getNPC().removeTrait(getClass());
		}
	}
	
	@Override
	public void onSpawn() {
		Bukkit.broadcastMessage(getNPC().getName() + " spawned.");
		spawned = true;
		if(!(getNPC().getEntity() instanceof HumanEntity)) {
			getNPC().removeTrait(getClass());
			AliveCitizens.logError("Cannot have a non-human farmer (" + getNPC().getEntity() + ")");
			return;
		}
		AliveCitizens.plugin().getFarmersManager().registerFarmerTrait(this);
		world = self().getWorld();
		
		updateBehaviour();
	}
	
	@Override
	public void onDespawn() {
		spawned = false;
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
		//
		updateBehaviour();
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
