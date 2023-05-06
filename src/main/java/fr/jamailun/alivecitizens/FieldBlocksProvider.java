package fr.jamailun.alivecitizens;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;

public interface FieldBlocksProvider extends ConfigurationSerializable {
	
	List<Material> ALLOWED_TYPES = List.of(Material.FARMLAND, Material.DIRT, Material.GRASS_BLOCK);
	List<Material> ALLOWED_TYPES_TOP = List.of(
			Material.AIR, Material.VOID_AIR, Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS
	);
	
	List<Block> getFieldBlocks();
	
	void showPlayer(Player target);
	
}
