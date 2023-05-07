package fr.jamailun.alivecitizens.structures;

import com.sk89q.worldedit.math.BlockVector3;
import fr.jamailun.alivecitizens.FieldBlocksProvider;
import fr.jamailun.alivecitizens.WorldEditFieldBlocksProvider;
import fr.jamailun.alivecitizens.utils.Showable;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Fields extends Structure implements Showable {
	
	private final Village village;
	private final FieldBlocksProvider blocksProvider;
	
	public Fields(Village village, World world, BlockVector3 min, BlockVector3 max) {
		super(); // random UUID
		this.village = village;
		blocksProvider = new WorldEditFieldBlocksProvider(world, min, max);
	}
	
	private Fields(Village village, String uuid, FieldBlocksProvider provider) {
		super(uuid); // build with uuid
		this.village = village;
		blocksProvider = provider;
	}
	
	public List<Block> getFieldsBlocks() {
		return blocksProvider.getFieldBlocks();
	}
	
	@Override
	public void showPlayer(Player target) {
		blocksProvider.showPlayer(target);
	}
	
	public Village getVillage() {
		return village;
	}
	
	@Override
	public @NotNull Map<String, Object> serialize() {
		return Map.of(
				"uuid", uuid,
				"provider", blocksProvider
		);
	}
	
	@NotNull
	public static Fields deserialize(Village village, @NotNull Map<String, Object> map) {
		String uuid = (String) map.get("uuid");
		WorldEditFieldBlocksProvider provider = (WorldEditFieldBlocksProvider) map.get("provider");
		return new Fields(village, uuid, provider);
	}
}
