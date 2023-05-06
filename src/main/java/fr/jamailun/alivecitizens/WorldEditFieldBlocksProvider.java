package fr.jamailun.alivecitizens;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import fr.jamailun.alivecitizens.utils.ParticlesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorldEditFieldBlocksProvider implements FieldBlocksProvider {
	
	private List<Block> fields = null;
	private final World world;
	private final BlockVector3 min, max;
	
	private final Location minLoc, maxLoc;
	
	public WorldEditFieldBlocksProvider(World world, BlockVector3 min, BlockVector3 max) {
		this.world = world;
		this.min = min;
		this.max = max;
		this.minLoc = new Location(world, min.getX(), min.getY(), min.getZ());
		this.maxLoc = new Location(world, max.getX(), max.getY(), max.getZ());
	}
	
	private void compute() {
		List<Block> fields = new ArrayList<>();
		
		Region region = new CuboidRegion(min, max);
		for(BlockVector3 pos : region) {
			Block block = new Location(world, pos.getX(), pos.getY(), pos.getZ()).getBlock();
			if(ALLOWED_TYPES.contains(block.getType())
				&& ALLOWED_TYPES_TOP.contains(block.getRelative(BlockFace.UP).getType())
			) {
				fields.add(block);
			}
		}
		this.fields = Collections.unmodifiableList(fields);
	}
	
	@Override
	public List<Block> getFieldBlocks() {
		if(fields == null)
			compute();
		return fields;
	}
	
	private static String serializeeBV(BlockVector3 bv) {
		return bv.getX() + ";" + bv.getY() + ";" + bv.getZ();
	}
	
	private static BlockVector3 deserializeeBV(String str) {
		String[] tokens = str.split(";", 3);
		int x = Integer.parseInt(tokens[0]);
		int y = Integer.parseInt(tokens[1]);
		int z = Integer.parseInt(tokens[2]);
		return BlockVector3.at(x, y, z);
	}
	
	@Override
	public @NotNull Map<String, Object> serialize() {
		return Map.of(
				"world-name", world.getName(),
				"min", serializeeBV(min),
				"max", serializeeBV(max)
		);
	}
	
	@NotNull
	public static WorldEditFieldBlocksProvider deserialize(@NotNull Map<String, Object> map) {
		String worldName = (String) map.get("world-name");
		BlockVector3 min = deserializeeBV((String)map.get("min"));
		BlockVector3 max = deserializeeBV((String)map.get("max"));
		return new WorldEditFieldBlocksProvider(Bukkit.getWorld(worldName), min, max);
	}
	
	public void showPlayer(Player target) {
		ParticlesPlayer.playBox(target, minLoc, maxLoc, 0.2, Particle.NOTE);
	}
}
