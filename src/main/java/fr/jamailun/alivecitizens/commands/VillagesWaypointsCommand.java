package fr.jamailun.alivecitizens.commands;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VillagesWaypointsCommand extends JamCommand {
	
	public VillagesWaypointsCommand(AliveCitizens plugin) {
		super(plugin, "villages.edit-waypoints");
		addArgument(0, () -> plugin.getFarmersManager().villagesIds());
	}
	
	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		Player player = (Player) sender;
		if(args.length == 0) {
			Village editing = plugin.getFarmersManager().getEditedWaypoints(player);
			if(editing != null) {
				plugin.getFarmersManager().stopEdit(player);
				sendSuccess(player, "Stopped editing waypoints of village '" + editing.getId() + "'.");
			} else {
				reject(sender, "Syntax: /"+label+" <village id>");
			}
			return;
		}
		Village village = plugin.getFarmersManager().getVillage(args[0]);
		if(village == null) {
			sendError(sender, "Unknown village id '" + args[0] + "'.");
			return;
		}
		if(village.equals(plugin.getFarmersManager().getEditedWaypoints(player))) {
			plugin.getFarmersManager().stopEdit(player);
			sendSuccess(player, "Stopped editing waypoints of village '" + args[0] + "'.");
		} else {
			plugin.getFarmersManager().startEdit(player, village);
			sendSuccess(player, "Started editing waypoints of village '" + args[0] + "'.");
		}
	}
	
	@Override
	public CommandExecutors getAllowedExecutors() {
		return CommandExecutors.PLAYER;
	}
	
}
