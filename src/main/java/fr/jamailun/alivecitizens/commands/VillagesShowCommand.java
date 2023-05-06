package fr.jamailun.alivecitizens.commands;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VillagesShowCommand extends JamCommand {
	
	public VillagesShowCommand(AliveCitizens plugin) {
		super(plugin, "villages.show");
		addArgument(0, () -> plugin.getFarmersManager().villagesIds());
	}
	
	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if(args.length < 1) {
			reject(sender, "Syntax: /"+label+" <village id>");
			return;
		}
		Player player = (Player) sender;
		Village village = plugin.getFarmersManager().getVillage(args[0]);
		if(village == null) {
			sendError(sender, "Unknown village id '" + args[0] + "'.");
			return;
		}
		if(village.show(player)) {
			sendSuccess(player, "Started showing village '" + args[0] + "'.");
		} else {
			sendSuccess(player, "Stopped showing village '" + args[0] + "'.");
		}
	}
	
	@Override
	public CommandExecutors getAllowedExecutors() {
		return CommandExecutors.PLAYER;
	}
	
}
