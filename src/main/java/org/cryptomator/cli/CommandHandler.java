package org.cryptomator.cli;

import com.google.common.base.Joiner;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cryptomator.cli.commands.ArgsInteractiveCommand;
import org.cryptomator.cli.commands.Command;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.commands.InteractiveCommand;
import org.cryptomator.cli.commands.NoArgsInteractiveCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Singleton
public class CommandHandler {

	private final static Pattern COMMAND_SEPARATOR = Pattern.compile("\\s");

	private final Map<String, InteractiveCommand> interactiveCommands = new HashMap<>(); //TODO SortedMap?
	private final Map<String, ConsoleCommand> consoleCommands = new HashMap<>(); //TODO SortedMap?
	private final Options consoleOptions = new Options();

	private final CommandLineParser parser = new DefaultParser();
	private final HelpFormatter helpFormatter = new HelpFormatter();

	private final ExecutorService handle;

	@Inject
	public CommandHandler(Set<Command> commands) {
		var scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		this.handle = Executors.newSingleThreadExecutor();
		this.handle.submit(() -> handleInput(scanner));

		registerCommands(commands);
	}

	public void processArgs(String[] rawArgs) throws ParseException {
		var cmdLine = this.parser.parse(this.consoleOptions, rawArgs);
		var commands = this.consoleCommands.entrySet() //
				.stream() //
				.filter(entry -> cmdLine.hasOption(entry.getKey())) //
				.map(Map.Entry::getValue) //
				.toList();
		commands.forEach(cmd -> cmd.consoleExecute(new CallContext(this, Joiner.on(" ").join(rawArgs)), cmdLine));
	}

	public void stop() {
		try {
			var finished = this.handle.awaitTermination(1L, TimeUnit.SECONDS);
			System.out.println(finished);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void registerCommands(Command... commands) {
		for (Command command : commands) {
			registerCommand(command);
		}
	}

	public void registerCommands(Iterable<Command> commands) {
		for (Command command : commands) {
			registerCommand(command);
		}
	}

	public void registerCommand(Command command) {
		if (command instanceof InteractiveCommand interactive) {
			if (this.interactiveCommands.put(interactive.interactiveCommandName(), interactive) != null) {
				throw new AssertionError("Duplicate interactive command name");
			}
		}
		if (command instanceof ConsoleCommand consoleCommand) {
			registerConsoleCommand(consoleCommand);
		}
	}

	public Set<String> getInteractiveCommandNames() {
		return new HashSet<>(this.interactiveCommands.keySet());
	}

	private void registerConsoleCommand(ConsoleCommand command) {
		var name = command.consoleCommandName();
		if (this.consoleOptions.hasLongOption(name)) {
			throw new AssertionError("Duplicate console command name");
		}
		this.consoleCommands.put(name, command);
		this.consoleOptions.addOption(Option.builder() //
				.longOpt(name) //
				.build());
		//TODO Groups, etc.
		var options = command.consoleOptions();
		if (options != null) {
			options.getOptions().forEach(this::registerConsoleOption);
		}
	}

	private void registerConsoleOption(Option option) {
		var name = option.getLongOpt();
		if (this.consoleOptions.hasLongOption(name)) {
			throw new AssertionError("Duplicate console command option");
		}
		this.consoleOptions.addOption(option);
	}

	//TODO: Empty args, too many spaces
	private void handleInput(Scanner scanner) {
		try {
			System.out.print("> ");
			while (scanner.hasNextLine()) {
				//TODO Keep looping even on exceptions
				handleLine(scanner.nextLine());
				System.out.print("> ");
			}
		} catch (Exception e) {
			e.printStackTrace(); //TODO
		}
	}

	private void handleLine(String read) {
		System.out.printf("Read: \"%s\"%n", read);

		var split = COMMAND_SEPARATOR.split(read, 2);
		var command = this.interactiveCommands.get(split[0]);
		if (command == null) {
			System.out.println("Unknown command! Try " + Joiner.on(", ").join(this.interactiveCommands.keySet()));
			return;
		}

		var args = split.length == 1 ? null : blankToNull(split[1]);
		var context = new CallContext(this, read);
		if (args == null) {
			if (command instanceof NoArgsInteractiveCommand withoutArgs) {
				withoutArgs.interactiveExecute(context);
			} else {
				this.helpFormatter.printHelp("TODO", "Arguments required for command!", ((ArgsInteractiveCommand) command).interactiveOptions(), null);
			}
		} else {
			if (command instanceof ArgsInteractiveCommand withArgs) {
				try {
					var options = withArgs.interactiveOptions();
					withArgs.interactiveExecute(context, options == null ? null : this.parser.parse(options, COMMAND_SEPARATOR.split(args)));
				} catch (ParseException e) {
					withArgs.interactiveParsingFailed(context, e); //TODO A bit of formatting?
				}
			} else {
				System.out.println("Command must not be invoked with arguments!");
			}
		}
	}

	private String blankToNull(String s) {
		return s.isBlank() ? null : s;
	}
}