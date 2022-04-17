package org.cryptomator.cli.commands.impl;

import de.skymatic.autobindings.BindIntoSet;
import org.apache.commons.cli.CommandLine;
import org.cryptomator.cli.CallContext;
import org.cryptomator.cli.commands.Command;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.commands.NoArgsInteractiveCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CommandVersion implements NoArgsInteractiveCommand, ConsoleCommand {

	private final static String NAME = "version";

	@Inject
	@BindIntoSet(module = "org.cryptomator.cli.commands.CommandModule", bindTo = Command.class)
	public CommandVersion() {

	}

	@Override
	public String interactiveCommandName() {
		return NAME;
	}

	@Override
	public String consoleCommandName() {
		return NAME;
	}

	@Override
	public void consoleExecute(CallContext context, CommandLine consoleCmdLine) {
		execute();
	}

	@Override
	public void interactiveExecute(CallContext context) {
		execute();
	}

	private void execute() {
		var appVer = System.getProperty("cryptomator.appVersion", "SNAPSHOT");
		var buildNumber = System.getProperty("cryptomator.buildNumber", "SNAPSHOT");

		//Reduce noise for parsers by using System.out directly
		System.out.printf("Cryptomator version %s (build %s)%n", appVer, buildNumber);
	}
}