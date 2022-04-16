package org.cryptomator.cli.commands.impl;

import org.apache.commons.cli.CommandLine;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.commands.NoArgsInteractiveCommand;

public class CommandVersion implements NoArgsInteractiveCommand, ConsoleCommand {

	private final static String NAME = "version";

	@Override
	public String interactiveCommandName() {
		return NAME;
	}

	@Override
	public String consoleCommandName() {
		return NAME;
	}

	@Override
	public void consoleExecute(CommandLine consoleCmdLine) {
		execute();
	}

	@Override
	public void interactiveExecute() {
		execute();
	}

	private void execute() {
		var appVer = System.getProperty("cryptomator.appVersion", "SNAPSHOT");
		var buildNumber = System.getProperty("cryptomator.buildNumber", "SNAPSHOT");

		//Reduce noise for parsers by using System.out directly
		System.out.printf("Cryptomator version %s (build %s)%n", appVer, buildNumber);
	}
}