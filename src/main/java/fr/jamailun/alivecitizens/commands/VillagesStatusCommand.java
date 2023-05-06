package fr.jamailun.alivecitizens.commands;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VillagesStatusCommand extends JamCommand {
	
	public VillagesStatusCommand(AliveCitizens plugin) {
		super(plugin, "villages.status");
		addArgument(0, () -> plugin.getFarmersManager().villagesIds());
	}
	
	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if(args.length < 1) {
			reject(sender, "Syntax: /"+label+" <village id>");
			return;
		}
		Village village = plugin.getFarmersManager().getVillage(args[0]);
		if(village == null) {
			sendError(sender, "Unknown village id '" + args[0] + "'.");
			return;
		}
		sender.sendMessage(village.getStatus());
	}
	
	@Override
	public CommandExecutors getAllowedExecutors() {
		return CommandExecutors.ALL;
	}
}
