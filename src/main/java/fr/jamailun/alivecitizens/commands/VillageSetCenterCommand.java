package fr.jamailun.alivecitizens.commands;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VillageSetCenterCommand extends JamCommand {
	
	public VillageSetCenterCommand(AliveCitizens plugin) {
		super(plugin, "villages.set-center");
		addArgument(0, () -> plugin.getFarmersManager().villagesIds());
	}
	
	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if(args.length < 1) {
			reject(sender, "Syntax: /"+label+" <village id> [radius]");
			return;
		}
		Village village = plugin.getFarmersManager().getVillage(args[0]);
		if(village == null) {
			sendError(sender, "Unknown village id '" + args[0] + "'.");
			return;
		}
		Player player = (Player) sender;
		if(args.length > 1) {
			double r;
			try {
				r = Double.parseDouble(args[1]);
			} catch(NumberFormatException e) {
				sendError(sender, "Invalid number format for radius '" + args[1] + "'.");
				return;
			}
			village.getPlace().setCenter(player.getLocation());
			village.getPlace().setRadius(r);
		} else {
			village.getPlace().setCenter(player.getLocation());
		}
		plugin.getFarmersManager().save();
		sendSuccess(sender, "Center of village §2" + village.getId() + "§a modified.");
	}
	
	@Override
	public CommandExecutors getAllowedExecutors() {
		return CommandExecutors.PLAYER;
	}
}
