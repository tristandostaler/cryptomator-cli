package org.cryptomator.cli.commands.impl;

import org.apache.commons.cli.CommandLine;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.commands.NoArgsInteractiveCommand;
import org.cryptomator.cli.frontend.WebDav;

public class CommandUnbind implements NoArgsInteractiveCommand, ConsoleCommand {

	private static final String NAME = "unbind";

	private final CommandBind bind;

	public CommandUnbind(CommandBind bind) {
		this.bind = bind;
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
	public void interactiveExecute() {
		execute();
	}

	@Override
	public void consoleExecute(CommandLine consoleCmdLine) {
		execute();
	}

	private void execute() {
		if (this.bind.server == null) {
			System.out.println("None bound!");
			return;
		}
		this.bind.server.stop();
		this.bind.server = null;
	}
}