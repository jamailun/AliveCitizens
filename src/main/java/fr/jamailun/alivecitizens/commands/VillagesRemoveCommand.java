package fr.jamailun.alivecitizens.commands;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class VillagesRemoveCommand extends JamCommand {
	
	public VillagesRemoveCommand(AliveCitizens plugin) {
		super(plugin, "villages.remove");
		addArgument(0, () -> plugin.getFarmersManager().villagesIds());
		addArgument(1, STRUCTURES);
	}
	
	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if(args.length < 3) {
			reject(sender, "Syntax: /"+label+" <village_id> <structure type> <structure id>");
			return;
		}
		Village village = plugin.getFarmersManager().getVillage(args[0]);
		if(village == null) {
			sendError(sender, "Unknown village id '" + args[0] + "'.");
			return;
		}
		if(!(STRUCTURES.contains(args[1]))) {
			sendError(sender, "Unknown structure type '"+args[1]+"'. Allowed=" + Arrays.toString(STRUCTURES.toArray()));
			return;
		}
		boolean house = args[1].equals("house");
		
		if(house ? village.removeHouse(args[2]) : village.removeFields(args[2])) {
			sendSuccess(sender, "Succefully removed "+args[1]+" §7"+args[2]+"§a in village village §2" + args[0] + "§a.");
			plugin.getFarmersManager().save();
		} else {
			sendError(sender, "Unknown " + args[1] + " id : '" + args[2]+"'.");
		}
	}
	
	@Override
	public CommandExecutors getAllowedExecutors() {
		return CommandExecutors.ALL;
	}
}
