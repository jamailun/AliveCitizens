package fr.jamailun.alivecitizens.commands;

import fr.jamailun.alivecitizens.AliveCitizens;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;

public abstract class JamCommand implements CommandExecutor, TabCompleter {
	
	protected enum CommandExecutors { ALL, CONSOLE, PLAYER }
	
	protected static final List<String> BOOLEAN = List.of("true","false");
	protected static final List<String> STRUCTURES = List.of("fields", "house");
	
	private final Map<Integer, Set<JamSubCommand>> subs = new HashMap<>();
	
	private int maxIndex = -1;
	protected final String label;
	protected final AliveCitizens plugin;
	
	protected JamCommand(AliveCitizens plugin, String command) {
		this.plugin = plugin;
		this.label = command;
		PluginCommand cmd = plugin.getCommand(command);
		assert cmd != null : "Define command '" + command + "'.";
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
	}
	
	protected String getStringFrom(String[] elems, int from) {
		StringBuilder sb = new StringBuilder();
		for(int i = from; i < elems.length; i++) {
			if(i>from) sb.append(" ");
			sb.append(elems[i]);
		}
		return sb.toString();
	}
	
	private Set<JamSubCommand> get(int index) {
		return subs.getOrDefault(index, Collections.emptySet());
	}
	private void put(int index, JamSubCommand sb) {
		if(!subs.containsKey(index))
			subs.put(index, new HashSet<>());
		subs.get(index).add(sb);
		if(index > maxIndex)
			maxIndex = index;
	}
	
	protected void addArgument(int index, String... arguments) {
		put(index, new JamSubCommand.SimpleArgumentsSubCommand(Arrays.asList(arguments)));
	}
	protected void addArgument(int index, Collection<String> arguments) {
		put(index, new JamSubCommand.SimpleArgumentsSubCommand(arguments));
	}
	protected void addArgument(int index, Supplier<Collection<String>> argumentsSupplier) {
		put(index, new JamSubCommand.SupplierArgumentsSubCommand(argumentsSupplier));
	}
	
	protected void addArgumentIf(int index, JamArg condition, String... arguments) {
		addArgumentIf(index, new JamArg[]{condition}, arguments);
	}
	protected void addArgumentIf(int index, JamArg[] conditions, String... arguments) {
		put(index, new JamSubCommand.ConditionalSimpleArgumentsSubCommand(Arrays.asList(arguments), Set.of(conditions)));
	}
	protected void addArgumentIf(int index, JamArg[] conditions, Collection<String> arguments) {
		put(index, new JamSubCommand.ConditionalSimpleArgumentsSubCommand(arguments, Set.of(conditions)));
	}
	protected void addArgumentIf(int index, JamArg[] conditions, Supplier<Collection<String>> argumentsSupplier) {
		put(index, new JamSubCommand.ConditionalSupplierArgumentsSubCommand(argumentsSupplier, Set.of(conditions)));
	}
	
	protected void addPlayerArgument(int index, boolean onlyOnline) {
		put(index, new JamSubCommand.PlayerSubCommand(onlyOnline));
	}
	protected void addPlayerArgumentIf(int index, JamArg condition, boolean onlyOnline) {
		addPlayerArgumentIf(index, new JamArg[]{condition}, onlyOnline);
	}
	protected void addPlayerArgumentIf(int index, JamArg[] conditions, boolean onlyOnline) {
		put(index, new JamSubCommand.ConditionalPlayerSubCommand(onlyOnline, Set.of(conditions)));
	}
	
	public abstract void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);
	
	public abstract CommandExecutors getAllowedExecutors();
	
	@Override
	public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		boolean isPlayer = sender instanceof Player;
		if(isPlayer && getAllowedExecutors() == CommandExecutors.CONSOLE) {
			reject(sender, true, label);
			return true;
		}
		if(!isPlayer && getAllowedExecutors() == CommandExecutors.PLAYER) {
			reject(sender, false, label);
			return true;
		}
		onCommand(sender, label, args);
		return true;
	}
	
	protected void reject(CommandSender sender, boolean isPlayer, String label) {
		if(isPlayer)
			sender.sendMessage(AliveCitizens.PREFIX + ChatColor.RED + "La commande '" + label + "' est réservée à la console.");
		else
			sender.sendMessage(AliveCitizens.PREFIX + ChatColor.RED + "La commande '" + label + "' est réservée aux joueurs physiques.");
	}
	
	protected void reject(CommandSender sender, String reason) {
		sender.sendMessage(AliveCitizens.PREFIX + ChatColor.RED + reason);
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(args.length <= 1) {
			return get(0).stream().filter(s -> s.accepts(args)).findFirst().orElse(JamSubCommand.EMPTY).getList(args);
		}
		return get(args.length - 1).stream().filter(s -> s.accepts(args)).findFirst().orElse(JamSubCommand.EMPTY).getList(args);
	}
	
	protected String captureArgs(String[] args, int indexStart) {
		StringJoiner sj = new StringJoiner(" ");
		for(int i = indexStart; i < args.length; i++) {
			sj.add(args[i]);
		}
		return sj.toString();
	}
	
	protected int parseInt(String arg, int orElse) {
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			return orElse;
		}
	}
	
	protected void sendError(CommandSender sender, String messsage) {
		sender.sendMessage(AliveCitizens.PREFIX + ChatColor.RED + messsage);
	}
	protected void sendSuccess(CommandSender sender, String messsage) {
		sender.sendMessage(AliveCitizens.PREFIX + ChatColor.GREEN + messsage);
	}
	protected void sendInfo(CommandSender sender, String messsage) {
		sender.sendMessage(AliveCitizens.PREFIX + ChatColor.YELLOW + messsage);
	}
}
