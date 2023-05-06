package fr.jamailun.alivecitizens.commands;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class VillagesCreateCommand extends JamCommand {
	
	public VillagesCreateCommand(AliveCitizens plugin) {
		super(plugin, "villages.create");
	}
	
	@Override
	public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if(args.length < 1) {
			reject(sender, "Syntax: /"+label+" <id> [radius=15]");
			return;
		}
		Player player = (Player) sender;
		String id = args[0];
		double radius = 15;
		if(args.length > 1) {
			try {
				radius = Double.parseDouble(args[1]);
			} catch(NumberFormatException e) {
				reject(sender, "Invalid number '"+ args[1] + "'.");
				return;
			}
		}
		
		Village village = new Village(id, player.getLocation(), radius);
		
		if(plugin.getFarmersManager().registerNewVillage(village)) {
			sendError(sender, "A village with this ID already exists !");
			return;
		}
		sendSuccess(sender, "Succefully created village §2" + id + "§a. Don't forget to create houses and fields.");
	}
	
	@Override
	public CommandExecutors getAllowedExecutors() {
		return CommandExecutors.PLAYER;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		return Collections.emptyList();
	}
}
