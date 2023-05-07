package fr.jamailun.alivecitizens.listeners;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import fr.jamailun.alivecitizens.utils.NumbersUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {
	
	private final AliveCitizens plugin;
	
	public InteractListener(AliveCitizens plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerBlockInterract(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		assert e.getClickedBlock() != null;
		Village edited = plugin.getFarmersManager().getEditedWaypoints(e.getPlayer());
		if(edited == null)
			return;
		
		if(e.getBlockFace() != BlockFace.UP) {
			e.getPlayer().sendMessage(ChatColor.RED + "Can only add waypoint on top of blocks.");
			return;
		}
		
		if(!(e.getClickedBlock().getType().isSolid())) {
			e.getPlayer().sendMessage(ChatColor.RED + "Can only add waypoint on top of solid blocks.");
			return;
		}
		
		if(edited.isWaypoint(e.getClickedBlock())) {
			edited.removeWaypoint(e.getClickedBlock());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Removed waypoint at " + ChatColor.GOLD + NumbersUtils.formatLocation(e.getClickedBlock().getLocation()));
		} else {
			edited.addWaypoint(e.getClickedBlock());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Added waypoint at " + ChatColor.GOLD + NumbersUtils.formatLocation(e.getClickedBlock().getLocation()));
		}
	}
	
}
