package fr.jamailun.alivecitizens.commands;

import com.sk89q.worldedit.math.BlockVector3;
import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.farmers.FarmerTrait;
import fr.jamailun.alivecitizens.integration.WorldEditHandler;
import fr.jamailun.alivecitizens.structures.Fields;
import fr.jamailun.alivecitizens.structures.Village;
import fr.jamailun.alivecitizens.structures.VillageHouse;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VillagesAddCommand extends JamCommand {
	
	public VillagesAddCommand(AliveCitizens plugin) {
		super(plugin, "villages.add");
		addArgument(0, () -> plugin.getFarmersManager().villagesIds());
		addArgument(1, STRUCTURES);
	}
	
	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if(args.length < 2) {
			reject(sender, "Syntax: /"+label+" <village id> <structure type> [args...]");
			return;
		}
		Player player = (Player) sender;
		Village village = plugin.getFarmersManager().getVillage(args[0]);
		if(village == null) {
			sendError(sender, "Unknown village id '" + args[0] + "'.");
			return;
		}
		
		if(args[1].equals("fields")) {
			BlockVector3[] minMax = WorldEditHandler.getMinMax(sender);
			if(minMax == null) {
				reject(sender, "Please, select a region with world edit first.");
				return;
			}
			Fields fields = new Fields(village, player.getWorld(), minMax[0], minMax[1]);
			village.addFields(fields);
			plugin.getFarmersManager().save();
			sendSuccess(sender, "Added fields to village §2" + village.getId() + "§a. §7field_id="+fields.getUuid());
			return;
		}
		
		if(args[1].equals("house")) {
			NPC selected = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
			if(selected == null) {
				if(args.length < 3) {
					reject(sender, "To create an house, either select a NPC first (/npc select) or add the npc id as an argument (/"+label+" <village_id> house <npc_id>)");
					return;
				}
				int id = parseInt(args[2], -1);
				if(id == -1) {
					sendError(sender, "Bad id format '"+args[2]+"' : must be an integer.");
					return;
				}
				selected = CitizensAPI.getNPCRegistry().getById(id);
				if(selected == null) {
					sendError(sender, "No NPC have the ID '" + id + "'.");
					return;
				}
			}
			if(!selected.hasTrait(FarmerTrait.class)) {
				sendError(sender, "NPC '" + selected.getName()+ "' does NOT gave the §lfarmer§c trait (/trait add farmer)");
				return;
			}
			FarmerTrait farmer = selected.getTrait(FarmerTrait.class);
			
			Block looked = player.getTargetBlockExact(6);
			if(looked == null || !(looked.getBlockData() instanceof Bed)) {
				sendError(sender, "Please, look at a §4bed§c block when creating a house.");
				return;
			}
			VillageHouse house = new VillageHouse(village, looked);
			village.addHouse(house);
			plugin.getFarmersManager().save();
			farmer.setHouse(house);
			sendSuccess(sender, "Added house to village §2" + village.getId() + "§a. Inhabitant = §6"+ selected.getName());
			return;
		}
		
		sendError(sender, "Unknown structure type '" + args[1] + "'.");
	}
	
	@Override
	public CommandExecutors getAllowedExecutors() {
		return CommandExecutors.PLAYER;
	}
}
