package fr.jamailun.alivecitizens.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class JamSubCommand {
	
	final static JamSubCommand EMPTY = new JamSubCommand() {
		@Override List<String> getList(String[] args) {return Collections.emptyList();}
		@Override boolean accepts(String[] args) {return true;}
	};
	
	abstract List<String> getList(String[] args);
	abstract boolean accepts(String[] args);
	
	private static final BiFunction<String[],String,Boolean> DEFAULT_FILTER = (args,n) -> {
		if(args.length == 0)
			return true;
		return n.toLowerCase().startsWith(args[args.length-1].toLowerCase());
	};
	
	static class SimpleArgumentsSubCommand extends JamSubCommand {
		private final Collection<String> arguments;
		SimpleArgumentsSubCommand(Collection<String> arguments) {
			this.arguments = arguments;
		}
		@Override
		public List<String> getList(String[] args) {
			return arguments.stream()
					.filter(n -> DEFAULT_FILTER.apply(args, n))
					.toList();
		}
		@Override
		boolean accepts(String[] args) {
			return true;
		}
	}
	static class SupplierArgumentsSubCommand extends JamSubCommand {
		private final Supplier<Collection<String>> supplier;
		SupplierArgumentsSubCommand(Supplier<Collection<String>> supplier) {
			this.supplier = supplier;
		}
		@Override
		public List<String> getList(String[] args) {
			return supplier.get().stream()
					.filter(n -> DEFAULT_FILTER.apply(args, n))
					.toList();
		}
		@Override
		boolean accepts(String[] args) {
			return true;
		}
	}
	static class ConditionalSimpleArgumentsSubCommand extends SimpleArgumentsSubCommand {
		private final Set<JamArg> conditions;
		ConditionalSimpleArgumentsSubCommand(Collection<String> arguments, Set<JamArg> conditions) {
			super(arguments);
			this.conditions = conditions;
		}
		@Override
		boolean accepts(String[] args) {
			return conditions.stream().allMatch(a -> a.accepts(args));
		}
	}
	static class ConditionalSupplierArgumentsSubCommand extends SupplierArgumentsSubCommand {
		private final Set<JamArg> conditions;
		ConditionalSupplierArgumentsSubCommand(Supplier<Collection<String>> supplier, Set<JamArg> conditions) {
			super(supplier);
			this.conditions = conditions;
		}
		@Override
		boolean accepts(String[] args) {
			return conditions.stream().allMatch(a -> a.accepts(args));
		}
	}
	
	static class PlayerSubCommand extends JamSubCommand {
		private final Supplier<Stream<String>> players;
		PlayerSubCommand(boolean onlyOnline) {
			if(onlyOnline)
				players = () -> Bukkit.getOnlinePlayers().stream().map(Player::getName);
			else
				players = () -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName);
		}
		@Override
		public List<String> getList(String[] args) {
			return players.get()
					.filter(n -> DEFAULT_FILTER.apply(args, n))
					.toList();
		}
		@Override
		boolean accepts(String[] args) {
			return true;
		}
	}
	static class ConditionalPlayerSubCommand extends PlayerSubCommand {
		private final Set<JamArg> conditions;
		ConditionalPlayerSubCommand(boolean onlyOnline, Set<JamArg> conditions) {
			super(onlyOnline);
			this.conditions = conditions;
		}
		@Override
		boolean accepts(String[] args) {
			return conditions.stream().allMatch(a -> a.accepts(args));
		}
	}
}
