package fr.jamailun.alivecitizens;

import fr.jamailun.alivecitizens.commands.VillageSetCenterCommand;
import fr.jamailun.alivecitizens.commands.VillagesAddCommand;
import fr.jamailun.alivecitizens.commands.VillagesCreateCommand;
import fr.jamailun.alivecitizens.commands.VillagesRemoveCommand;
import fr.jamailun.alivecitizens.commands.VillagesShowCommand;
import fr.jamailun.alivecitizens.commands.VillagesStatusCommand;
import fr.jamailun.alivecitizens.farmers.FarmerTrait;
import fr.jamailun.alivecitizens.farmers.FarmersManager;
import fr.jamailun.alivecitizens.structures.Fields;
import fr.jamailun.alivecitizens.structures.Village;
import fr.jamailun.alivecitizens.structures.VillagePlace;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AliveCitizens extends JavaPlugin {
	
	public static final String PREFIX = "§aAlive§eCitizens§r§l | " + ChatColor.RESET;
	private static AliveCitizens instance;
	
	private FarmersManager farmersManager;
	
	@Override
	public void onLoad() {
		instance = this;
		
		ConfigurationSerialization.registerClass(Village.class);
		ConfigurationSerialization.registerClass(VillagePlace.class);
		ConfigurationSerialization.registerClass(Fields.class);
		ConfigurationSerialization.registerClass(WorldEditFieldBlocksProvider.class);
	}
	
	@Override
	public void onEnable() {
		TraitFactory traitFactory;
		try {
			traitFactory = CitizensAPI.getTraitFactory();
		} catch(IllegalStateException e) {
			logError("Could not get the TraitFactory of Citizens2 : " + e.getMessage());
			logError("Disabling AliveCitizens.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		traitFactory.registerTrait(TraitInfo.create(FarmerTrait.class).withName("farmer"));
		
		farmersManager = new FarmersManager(this);
		
		// Commands
		new VillagesCreateCommand(this);
		new VillagesAddCommand   (this);
		new VillagesRemoveCommand(this);
		new VillagesShowCommand  (this);
		new VillagesStatusCommand(this);
		new VillageSetCenterCommand(this);
	}
	
	public FarmersManager getFarmersManager() {
		return farmersManager;
	}
	
	
	public static void log(String str) {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + PREFIX + ChatColor.YELLOW + "[-] " + str);
	}
	public static void logSuccess(String str) {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + PREFIX + ChatColor.GREEN + "[>] " + str);
	}
	public static void logError(String str) {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + PREFIX + ChatColor.RED + "[!] " + str);
	}
	
	public static AliveCitizens plugin() {
		return instance;
	}
}
