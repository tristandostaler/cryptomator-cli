package org.cryptomator.cli.commands.impl;

import dagger.Lazy;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cryptomator.cli.CommandHandler;
import org.cryptomator.cli.commands.ArgsInteractiveCommand;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.commands.NoArgsInteractiveCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.MessageFormat;

@Singleton
public class CommandHelp implements ArgsInteractiveCommand, NoArgsInteractiveCommand, ConsoleCommand {

	private final static String NAME = "help";

	private final Lazy<CommandHandler> commandHandler;

	@Inject
	public CommandHelp(Lazy<CommandHandler> commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public String consoleCommandName() {
		return NAME;
	}

	@Override
	public String interactiveCommandName() {
		return NAME;
	}

	@Override
	public Options interactiveOptions() {
		return null; //TODO
	}

	@Override
	public Options consoleOptions() {
		return null; //TODO
	}

	@Override
	public void interactiveExecute() {
		var builder = new StringBuilder("=== Help for Cryptomator CLI ===\n") //
				.append("Commands:\n");
		for (String cmd : this.commandHandler.get().getInteractiveCommandNames()) {
			var desc = "TODO"; //TODO
			builder.append("  ");
			builder.append("%-15s".formatted(MessageFormat.format("{0}:", cmd)));
			builder.append("\t").append(desc);
			builder.append("\n");
		}
		System.out.println(builder.substring(0, builder.length() - 1));
	}

	@Override
	public void interactiveExecute(CommandLine cmdLine) {

	}

	@Override
	public void interactiveParsingFailed(ParseException parseException, String cmdLine) {

	}

	@Override
	public void consoleExecute(CommandLine consoleCmdLine) {

	}
}