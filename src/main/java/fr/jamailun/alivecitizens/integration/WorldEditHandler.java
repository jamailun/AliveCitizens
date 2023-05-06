package fr.jamailun.alivecitizens.integration;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class WorldEditHandler {
	private WorldEditHandler() {}
	
	/***
	 * Get min & max selection of a player.
	 * @param sender the owner of the region
	 * @return an array of length 2 (0=min, 1=max)
	 */
	public static @Nullable BlockVector3[] getMinMax(CommandSender sender) {
		LocalSession worldEditSession = WorldEdit.getInstance().getSessionManager().findByName(sender.getName());
		if(worldEditSession != null && worldEditSession.getSelectionWorld() != null) {
			RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
			if(regionSelector.isDefined()) {
				try {
					Region region = regionSelector.getRegion();
					return new BlockVector3[] { region.getMinimumPoint(), region.getMaximumPoint() };
				} catch (IncompleteRegionException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	@NotNull
	public static Block[] getSelectedBlocks(CommandSender sender) {
		LocalSession worldEditSession = WorldEdit.getInstance().getSessionManager().findByName(sender.getName());
		if(worldEditSession != null) {
			if(worldEditSession.getSelectionWorld() != null) {
				RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
				if(regionSelector.isDefined()) {
					try {
						Region region = regionSelector.getRegion();
						World world = Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName());
						Block[] blocks = new Block[(int) region.getVolume()];
						
						int i = 0;
						for(BlockVector3 blockVector3 : region) {
							blocks[i] = new Location(world, blockVector3.getX(), blockVector3.getY(), blockVector3.getZ()).getBlock();
							i++;
						}
						
						return blocks;
					} catch (IncompleteRegionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return new Block[0];
	}
}
