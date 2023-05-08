package fr.jamailun.alivecitizens.listeners;

import fr.jamailun.alivecitizens.AliveCitizens;
import fr.jamailun.alivecitizens.structures.Village;
import fr.jamailun.alivecitizens.utils.NumbersUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InteractListener implements Listener {
	
	private final AliveCitizens plugin;
	private static final Set<UUID> UUIDS = new HashSet<>();
	
	public InteractListener(AliveCitizens plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerBlockInterract(PlayerInteractEvent e) {
		if(UUIDS.contains(e.getPlayer().getUniqueId()))
			return;
		UUIDS.add(e.getPlayer().getUniqueId());
		Bukkit.getScheduler().runTaskLater(plugin, () -> UUIDS.remove(e.getPlayer().getUniqueId()), 2L);
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
			return;
		Village edited = plugin.getFarmersManager().getEditedWaypoints(e.getPlayer());
		if(edited == null)
			return;
		assert e.getClickedBlock() != null;
		
		if(e.getBlockFace() != BlockFace.UP) {
			e.getPlayer().sendMessage(ChatColor.RED + "Can only add waypoint on top of blocks.");
			return;
		}
		
		if(!(e.getClickedBlock().getType().isSolid())) {
			e.getPlayer().sendMessage(ChatColor.RED + "Can only add waypoint on top of solid blocks.");
			return;
		}
		
		if(edited.isWaypoint(e.getClickedBlock())) {
			Location l = edited.removeWaypoint(e.getClickedBlock());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Removed waypoint at " + ChatColor.GOLD + NumbersUtils.formatLocation(l));
		} else {
			Location l = edited.addWaypoint(e.getClickedBlock());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Added waypoint at " + ChatColor.GOLD + NumbersUtils.formatLocation(l));
		}
	}
	
}
