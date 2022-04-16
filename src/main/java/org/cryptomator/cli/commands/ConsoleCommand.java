package org.cryptomator.cli.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public non-sealed interface ConsoleCommand extends Command {

	String consoleCommandName();

	default Options consoleOptions() {
		return null;
	}

	void consoleExecute(CommandLine consoleCmdLine);

}